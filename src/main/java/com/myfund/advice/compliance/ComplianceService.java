package com.myfund.advice.compliance;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 合规策略中心：负责敏感词检测、输出审计等。
 */
@Service
public class ComplianceService {

    private final List<String> sensitiveKeywords = List.of("保本", "稳赚", "内幕", "绝对收益");

    /**
     * 简单敏感词过滤，后续可替换为策略引擎或外部服务。
     */
    public boolean containsSensitiveKeyword(String content) {
        return sensitiveKeywords.stream().anyMatch(content::contains);
    }
}
