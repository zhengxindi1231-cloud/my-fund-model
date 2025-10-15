package com.myfund.advice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 投顾建议请求，包含用户 ID、问题类型等。
 */
public class AdviceRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String question;

    @NotBlank
    private String adviceType;

    @NotNull
    private Boolean requireHumanReview;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAdviceType() {
        return adviceType;
    }

    public void setAdviceType(String adviceType) {
        this.adviceType = adviceType;
    }

    public Boolean getRequireHumanReview() {
        return requireHumanReview;
    }

    public void setRequireHumanReview(Boolean requireHumanReview) {
        this.requireHumanReview = requireHumanReview;
    }
}
