package com.acrenex.app.models;

public class Application {

    private String applicationId;
    private String ulpin;
    private String applicantId;
    private String serviceType;
    private String submissionDate;
    private String currentDepartment;
    private String currentStatus;
    private int progress;

    public Application() {
    }

    public Application(
            String applicationId,
            String ulpin,
            String applicantId,
            String serviceType,
            String submissionDate,
            String currentDepartment,
            String currentStatus,
            int progress
    ) {
        this.applicationId = applicationId;
        this.ulpin = ulpin;
        this.applicantId = applicantId;
        this.serviceType = serviceType;
        this.submissionDate = submissionDate;
        this.currentDepartment = currentDepartment;
        this.currentStatus = currentStatus;
        this.progress = progress;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getUlpin() {
        return ulpin;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public String getCurrentDepartment() {
        return currentDepartment;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public int getProgress() {
        return progress;
    }
}