package com.myfund.advice.service;

import com.myfund.advice.model.UserProfile;
import org.springframework.stereotype.Service;

/**
 * 风险评估服务，根据用户画像和仓位计算风险暴露。
 */
@Service
public class RiskAssessmentService {

    /**
     * 简化版风险评分。真实场景可结合历史波动率、持仓行业集中度等。
     */
    public String evaluateRiskExposure(UserProfile profile) {
        if (profile == null) {
            return "UNKNOWN";
        }
        double expectedReturn = profile.getExpectedAnnualReturn() != null ? profile.getExpectedAnnualReturn() : 0.0;
        if ("激进型".equals(profile.getRiskLevel()) || expectedReturn > 10.0) {
            return "HIGH";
        }
        if ("稳健型".equals(profile.getRiskLevel())) {
            return "MEDIUM";
        }
        return "LOW";
    }
}
