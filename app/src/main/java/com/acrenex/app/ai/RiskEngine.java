package com.acrenex.app.ai;

public class RiskEngine {

    public static class RiskResult {

        private final int score;
        private final String level;
        private final String reason;

        public RiskResult(
                int score,
                String level,
                String reason
        ) {

            this.score = score;
            this.level = level;
            this.reason = reason;
        }

        public int getScore() {
            return score;
        }

        public String getLevel() {
            return level;
        }

        public String getReason() {
            return reason;
        }
    }

    public RiskResult calculateRisk(
            boolean ownerMismatch,
            boolean areaMismatch,
            boolean ulpinMismatch,
            boolean encumbrance,
            boolean dispute,
            boolean taxPending
    ) {

        int score = 0;

        StringBuilder reasons =
                new StringBuilder();

        if (ownerMismatch) {

            score += 30;

            reasons.append(
                    "Ownership mismatch; "
            );
        }

        if (areaMismatch) {

            score += 20;

            reasons.append(
                    "Area mismatch; "
            );
        }

        if (ulpinMismatch) {

            score += 35;

            reasons.append(
                    "ULPIN mismatch; "
            );
        }

        if (encumbrance) {

            score += 10;

            reasons.append(
                    "Encumbrance detected; "
            );
        }

        if (dispute) {

            score += 25;

            reasons.append(
                    "Dispute record detected; "
            );
        }

        if (taxPending) {

            score += 5;

            reasons.append(
                    "Tax dues pending; "
            );
        }

        score =
                Math.min(
                        score,
                        100
                );

        String level;

        if (score >= 60) {

            level = "HIGH";

        } else if (score >= 30) {

            level = "MEDIUM";

        } else {

            level = "LOW";
        }

        if (reasons.length() == 0) {

            reasons.append(
                    "No major demo risk indicators detected."
            );
        }

        return new RiskResult(
                score,
                level,
                reasons.toString()
        );
    }
}