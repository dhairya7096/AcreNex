package com.acrenex.app.ai;

public class AIAssistantManager {

    public String answer(
            String question,
            String ownerName,
            String area,
            String landUse,
            String zoning,
            String taxStatus,
            String registrationStatus,
            String encumbranceStatus
    ) {

        if (question == null) {

            return defaultResponse();
        }

        String q =
                question
                        .trim()
                        .toLowerCase();

        if (q.contains("owner") ||
                q.contains("ownership")) {

            return
                    "Recorded owner: "
                            + safe(ownerName);
        }

        if (q.contains("area")) {

            return
                    "Recorded parcel area: "
                            + safe(area);
        }

        if (q.contains("land use") ||
                q.contains("landuse")) {

            return
                    "Recorded land use: "
                            + safe(landUse);
        }

        if (q.contains("zone") ||
                q.contains("zoning")) {

            return
                    "Recorded zoning: "
                            + safe(zoning);
        }

        if (q.contains("tax")) {

            return
                    "Property tax status: "
                            + safe(taxStatus);
        }

        if (q.contains("registration")) {

            return
                    "Registration status: "
                            + safe(registrationStatus);
        }

        if (q.contains("mortgage") ||
                q.contains("encumbrance")) {

            return
                    "Encumbrance status: "
                            + safe(encumbranceStatus);
        }

        return defaultResponse();
    }

    private String defaultResponse() {

        return
                "I can help you explore this parcel's "
                        + "ownership, area, land use, zoning, "
                        + "registration, tax and encumbrance "
                        + "information.";
    }

    private String safe(String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "Not available";
        }

        return value;
    }
}