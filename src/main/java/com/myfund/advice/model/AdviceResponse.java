package com.myfund.advice.model;

import java.time.Instant;
import java.util.List;

/**
 * 投顾响应结构，便于前端展示建议、理由与风险提示。
 */
public class AdviceResponse {

    private String recommendation;
    private List<String> rationale;
    private List<String> risks;
    private List<String> references;
    private boolean requiresHumanReview;
    private Instant generatedAt;

    public static AdviceResponseBuilder builder() {
        return new AdviceResponseBuilder();
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public List<String> getRationale() {
        return rationale;
    }

    public void setRationale(List<String> rationale) {
        this.rationale = rationale;
    }

    public List<String> getRisks() {
        return risks;
    }

    public void setRisks(List<String> risks) {
        this.risks = risks;
    }

    public List<String> getReferences() {
        return references;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }

    public boolean isRequiresHumanReview() {
        return requiresHumanReview;
    }

    public void setRequiresHumanReview(boolean requiresHumanReview) {
        this.requiresHumanReview = requiresHumanReview;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }

    public static class AdviceResponseBuilder {
        private final AdviceResponse response = new AdviceResponse();

        public AdviceResponseBuilder recommendation(String recommendation) {
            response.setRecommendation(recommendation);
            return this;
        }

        public AdviceResponseBuilder rationale(List<String> rationale) {
            response.setRationale(rationale);
            return this;
        }

        public AdviceResponseBuilder risks(List<String> risks) {
            response.setRisks(risks);
            return this;
        }

        public AdviceResponseBuilder references(List<String> references) {
            response.setReferences(references);
            return this;
        }

        public AdviceResponseBuilder requiresHumanReview(boolean requiresHumanReview) {
            response.setRequiresHumanReview(requiresHumanReview);
            return this;
        }

        public AdviceResponseBuilder generatedAt(Instant generatedAt) {
            response.setGeneratedAt(generatedAt);
            return this;
        }

        public AdviceResponse build() {
            return response;
        }
    }
}
