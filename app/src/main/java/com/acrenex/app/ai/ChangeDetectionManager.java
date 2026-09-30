package com.acrenex.app.ai;

public class ChangeDetectionManager {

    public static class ChangeResult {

        private final boolean changed;
        private final double changePercentage;
        private final String description;

        public ChangeResult(
                boolean changed,
                double changePercentage,
                String description
        ) {

            this.changed = changed;
            this.changePercentage =
                    changePercentage;
            this.description =
                    description;
        }

        public boolean isChanged() {
            return changed;
        }

        public double getChangePercentage() {
            return changePercentage;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Prototype comparison method.
     *
     * The actual production version can receive
     * processed satellite-image features from the
     * backend and a validated change-detection model.
     */
    public ChangeResult evaluate(
            double changePercentage
    ) {

        boolean changed =
                changePercentage >= 10.0;

        String description;

        if (changePercentage >= 30.0) {

            description =
                    "Significant spatial change detected. "
                            + "Officer review recommended.";

        } else if (changePercentage >= 10.0) {

            description =
                    "Potential spatial change detected. "
                            + "Requires verification.";

        } else {

            description =
                    "No significant demo change detected.";
        }

        return new ChangeResult(
                changed,
                changePercentage,
                description
        );
    }
}