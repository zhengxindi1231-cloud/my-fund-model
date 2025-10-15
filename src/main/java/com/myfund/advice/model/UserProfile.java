package com.myfund.advice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 用户画像持久化实体，用于构建风险偏好、收益目标等基础信息。
 */
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String userId;

    @NotBlank
    private String riskLevel;

    @NotNull
    @Min(1)
    @Max(120)
    private Integer investmentHorizonMonths;

    @NotNull
    private Double expectedAnnualReturn;

    private String holdingSummary;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Integer getInvestmentHorizonMonths() {
        return investmentHorizonMonths;
    }

    public void setInvestmentHorizonMonths(Integer investmentHorizonMonths) {
        this.investmentHorizonMonths = investmentHorizonMonths;
    }

    public Double getExpectedAnnualReturn() {
        return expectedAnnualReturn;
    }

    public void setExpectedAnnualReturn(Double expectedAnnualReturn) {
        this.expectedAnnualReturn = expectedAnnualReturn;
    }

    public String getHoldingSummary() {
        return holdingSummary;
    }

    public void setHoldingSummary(String holdingSummary) {
        this.holdingSummary = holdingSummary;
    }
}
