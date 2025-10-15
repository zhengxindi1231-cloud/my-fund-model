package com.myfund.advice.service;

import com.myfund.advice.compliance.ComplianceService;
import com.myfund.advice.model.AdviceRequest;
import com.myfund.advice.model.AdviceResponse;
import com.myfund.advice.model.AuditLog;
import com.myfund.advice.model.UserProfile;
import com.myfund.advice.repository.UserProfileRepository;
import com.myfund.advice.tool.FundProfileTool;
import com.myfund.advice.tool.MarketDataTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 投顾主流程：封装提示工程、工具调用、合规审计等。
 */
@Service
public class AdviceService {

    private static final Logger log = LoggerFactory.getLogger(AdviceService.class);
    private static final String SYSTEM_PROMPT = """
        你是一名持证证券投资顾问，需要基于给定的市场数据、基金档案和用户画像提供合规、可解释的投资建议。
        输出时必须：
        1. 明确建议类型（买入/卖出/观望/调仓）。
        2. 给出不少于两条理由，并引用提供的数据要点。
        3. 列出需要提醒的风险点。
        4. 如信息不足，需说明缺失数据并建议用户补充。
        回答语言使用简体中文。
        """;
    private static final String USER_PROMPT_TEMPLATE = """
        用户ID: %s
        用户风险等级: %s
        风险暴露描述: %s

        基金档案(JSON):
        %s

        最新行情(JSON):
        %s

        市场情绪(JSON):
        %s

        用户问题:
        %s

        请按照要求生成建议，并在引用处明确指出数据来源。
        """;
    private static final String FALLBACK_RESPONSE = "模型服务暂不可用，请联系人工顾问或稍后重试。";

    private final MarketDataTool marketDataTool;
    private final FundProfileTool fundProfileTool;
    private final RiskAssessmentService riskAssessmentService;
    private final ComplianceService complianceService;
    private final UserProfileRepository userProfileRepository;
    private final AuditLogService auditLogService;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public AdviceService(MarketDataTool marketDataTool,
                         FundProfileTool fundProfileTool,
                         RiskAssessmentService riskAssessmentService,
                         ComplianceService complianceService,
                         UserProfileRepository userProfileRepository,
                         AuditLogService auditLogService,
                         ChatClient.Builder chatClientBuilder,
                         ObjectMapper objectMapper) {
        this.marketDataTool = marketDataTool;
        this.fundProfileTool = fundProfileTool;
        this.riskAssessmentService = riskAssessmentService;
        this.complianceService = complianceService;
        this.userProfileRepository = userProfileRepository;
        this.auditLogService = auditLogService;
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public AdviceResponse generateAdvice(AdviceRequest request) {
        UserProfile profile = userProfileRepository.findByUserId(request.getUserId()).orElse(null);
        Map<String, Object> marketMetrics = marketDataTool.fetchLatestFundMetrics(request.getAdviceType());
        Map<String, Object> marketSentiment = marketDataTool.fetchMarketSentiment();
        Map<String, Object> fundProfile = fundProfileTool.findFundProfile(request.getAdviceType());
        String riskExposure = riskAssessmentService.evaluateRiskExposure(profile);

        String userPrompt = buildUserPrompt(request, profile, riskExposure, marketMetrics, marketSentiment, fundProfile);
        String combinedPrompt = SYSTEM_PROMPT + "\n---\n" + userPrompt;
        log.debug("Prompt payload for user {}: {}", request.getUserId(), combinedPrompt);

        String recommendation = callAdvisorModel(userPrompt);
        persistAuditLog(request, combinedPrompt, recommendation);

        if (complianceService.containsSensitiveKeyword(recommendation)) {
            recommendation = "系统检测到潜在敏感内容，建议人工顾问复核后再反馈给用户。";
        }

        List<String> references = new ArrayList<>();
        addIfPresent(references, fundProfile.get("source"));
        addIfPresent(references, marketMetrics.get("source"));
        addIfPresent(references, marketSentiment.get("source"));

        return AdviceResponse.builder()
            .recommendation(recommendation)
            .rationale(List.of(
                "基金净值" + formatMetric(marketMetrics.get("latestNav")) + ", 日涨跌幅" + formatMetric(marketMetrics.get("dailyChange")) + "%", 
                "基金经理与基准: " + fundProfile.getOrDefault("fundManager", "待补充") + " / " + fundProfile.getOrDefault("benchmark", "待补充")))
            .risks(List.of("市场存在波动风险，需关注政策面变化", "建议确认同花顺实时数据后再执行操作"))
            .references(references)
            .requiresHumanReview(Boolean.TRUE.equals(request.getRequireHumanReview()))
            .generatedAt(Instant.now())
            .build();
    }

    private String buildUserPrompt(AdviceRequest request,
                                   UserProfile profile,
                                   String riskExposure,
                                   Map<String, Object> marketMetrics,
                                   Map<String, Object> marketSentiment,
                                   Map<String, Object> fundProfile) {
        String riskLevel = profile != null ? profile.getRiskLevel() : "未知";
        return USER_PROMPT_TEMPLATE.formatted(
            request.getUserId(),
            riskLevel,
            riskExposure,
            toJson(fundProfile),
            toJson(marketMetrics),
            toJson(marketSentiment),
            request.getQuestion()
        );
    }

    private String callAdvisorModel(String userPrompt) {
        try {
            return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userPrompt)
                .call()
                .content();
        } catch (Exception ex) {
            log.warn("调用 Spring AI ChatClient 生成投顾建议失败，使用兜底响应", ex);
            return FALLBACK_RESPONSE;
        }
    }

    private void addIfPresent(List<String> references, Object value) {
        if (value instanceof String source && !source.isBlank()) {
            references.add(source);
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.debug("Failed to serialize prompt context", e);
            return String.valueOf(value);
        }
    }

    private String formatMetric(Object value) {
        if (value == null) {
            return "未知";
        }
        if (value instanceof Number number) {
            return String.format("%.4f", number.doubleValue());
        }
        return String.valueOf(value);
    }

    private void persistAuditLog(AdviceRequest request, String prompt, String recommendation) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(request.getUserId());
        auditLog.setQuestion(request.getQuestion());
        auditLog.setPrompt(prompt);
        auditLog.setModelResponse(recommendation);
        auditLogService.record(auditLog);
    }
}
