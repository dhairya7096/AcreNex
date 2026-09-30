package com.acrenex.app.ai;

public class MatchingEngine {

    public static class MatchResult {

        private final boolean ownerMatch;
        private final boolean areaMatch;
        private final boolean ulpinMatch;
        private final int confidence;

        public MatchResult(
                boolean ownerMatch,
                boolean areaMatch,
                boolean ulpinMatch,
                int confidence
        ) {

            this.ownerMatch = ownerMatch;
            this.areaMatch = areaMatch;
            this.ulpinMatch = ulpinMatch;
            this.confidence = confidence;
        }

        public boolean isOwnerMatch() {
            return ownerMatch;
        }

        public boolean isAreaMatch() {
            return areaMatch;
        }

        public boolean isUlpinMatch() {
            return ulpinMatch;
        }

        public int getConfidence() {
            return confidence;
        }
    }

    public MatchResult compare(
            String documentOwner,
            String parcelOwner,
            String documentArea,
            String parcelArea,
            String documentUlpin,
            String parcelUlpin
    ) {

        boolean owner =
                normalize(documentOwner)
                        .equals(
                                normalize(parcelOwner)
                        );

        boolean area =
                normalize(documentArea)
                        .equals(
                                normalize(parcelArea)
                        );

        boolean ulpin =
                normalize(documentUlpin)
                        .equals(
                                normalize(parcelUlpin)
                        );

        int score = 0;

        if (owner) {
            score += 40;
        }

        if (area) {
            score += 25;
        }

        if (ulpin) {
            score += 35;
        }

        return new MatchResult(
                owner,
                area,
                ulpin,
                score
        );
    }

    private String normalize(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .replaceAll(
                        "\\s+",
                        " "
                )
                .toLowerCase();
    }
}