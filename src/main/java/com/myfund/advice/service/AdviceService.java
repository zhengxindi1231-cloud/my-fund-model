package com.myfund.advice.service;

import com.myfund.advice.client.ThinkModelClient;
import com.myfund.advice.compliance.ComplianceService;
import com.myfund.advice.model.AdviceRequest;
import com.myfund.advice.model.AdviceResponse;
import com.myfund.advice.model.AuditLog;
import com.myfund.advice.model.UserProfile;
import com.myfund.advice.repository.UserProfileRepository;
import com.myfund.advice.tool.FundProfileTool;
import com.myfund.advice.tool.MarketDataTool;
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

    private final MarketDataTool marketDataTool;
    private final FundProfileTool fundProfileTool;
    private final RiskAssessmentService riskAssessmentService;
    private final ComplianceService complianceService;
    private final UserProfileRepository userProfileRepository;
    private final ThinkModelClient thinkModelClient;
    private final AuditLogService auditLogService;

    public AdviceService(MarketDataTool marketDataTool,
                         FundProfileTool fundProfileTool,
                         RiskAssessmentService riskAssessmentService,
                         ComplianceService complianceService,
                         UserProfileRepository userProfileRepository,
                         ThinkModelClient thinkModelClient,
                         AuditLogService auditLogService) {
        this.marketDataTool = marketDataTool;
        this.fundProfileTool = fundProfileTool;
        this.riskAssessmentService = riskAssessmentService;
        this.complianceService = complianceService;
        this.userProfileRepository = userProfileRepository;
        this.thinkModelClient = thinkModelClient;
        this.auditLogService = auditLogService;
    }

    public AdviceResponse generateAdvice(AdviceRequest request) {
        UserProfile profile = userProfileRepository.findByUserId(request.getUserId()).orElse(null);
        Map<String, Object> marketMetrics = marketDataTool.fetchLatestFundMetrics(request.getAdviceType());
        Map<String, Object> marketSentiment = marketDataTool.fetchMarketSentiment();
        Map<String, Object> fundProfile = fundProfileTool.findFundProfile(request.getAdviceType());
        String riskExposure = riskAssessmentService.evaluateRiskExposure(profile);

        String prompt = buildPrompt(request, profile, marketMetrics, marketSentiment, fundProfile, riskExposure);
        log.info("Prompt for advice generation: {}", prompt);

        String recommendation = thinkModelClient.generateAdvice(prompt);
        persistAuditLog(request, prompt, recommendation);

        if (complianceService.containsSensitiveKeyword(recommendation)) {
            recommendation = "系统检测到潜在敏感内容，建议人工顾问复核后再反馈给用户。";
        }

        List<String> references = new ArrayList<>();
        addIfPresent(references, marketMetrics.get("source"));
        addIfPresent(references, marketSentiment.get("source"));

        return AdviceResponse.builder()
            .recommendation(recommendation)
            .rationale(List.of(
                "结合用户风险暴露" + riskExposure,
                "基金基本面：" + fundProfile.get("fundName")))
            .risks(List.of("市场存在波动风险，需关注政策面变化"))
            .references(references)
            .requiresHumanReview(Boolean.TRUE.equals(request.getRequireHumanReview()))
            .generatedAt(Instant.now())
            .build();
    }

    private String buildPrompt(AdviceRequest request,
                               UserProfile profile,
                               Map<String, Object> marketMetrics,
                               Map<String, Object> marketSentiment,
                               Map<String, Object> fundProfile,
                               String riskExposure) {
        return "你是持证金融顾问，需要针对用户问题提供合规建议。" +
            "请引用提供的市场数据和基金档案，输出买入/卖出建议、理由和风险提示。" +
            "\n用户风险画像: " + (profile != null ? profile.getRiskLevel() : "未知") +
            "\n风险暴露: " + riskExposure +
            "\n基金档案: " + fundProfile +
            "\n实时行情: " + marketMetrics +
            "\n市场情绪: " + marketSentiment +
            "\n用户问题: " + request.getQuestion();
    }

    private void addIfPresent(List<String> references, Object value) {
        if (value instanceof String source && !source.isBlank()) {
            references.add(source);
        }
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
