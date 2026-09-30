package com.acrenex.app.models;

public class AIAlert {

    private String alertId;
    private String ulpin;
    private String alertType;
    private String description;
    private String severity;
    private int riskScore;
    private String detectedDate;
    private String status;

    public AIAlert() {
    }

    public AIAlert(
            String alertId,
            String ulpin,
            String alertType,
            String description,
            String severity,
            int riskScore,
            String detectedDate,
            String status
    ) {
        this.alertId = alertId;
        this.ulpin = ulpin;
        this.alertType = alertType;
        this.description = description;
        this.severity = severity;
        this.riskScore = riskScore;
        this.detectedDate = detectedDate;
        this.status = status;
    }

    public String getAlertId() {
        return alertId;
    }

    public String getUlpin() {
        return ulpin;
    }

    public String getAlertType() {
        return alertType;
    }

    public String getDescription() {
        return description;
    }

    public String getSeverity() {
        return severity;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public String getDetectedDate() {
        return detectedDate;
    }

    public String getStatus() {
        return status;
    }
}