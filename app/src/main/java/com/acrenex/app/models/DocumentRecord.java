package com.acrenex.app.models;

public class DocumentRecord {

    private String documentId;
    private String ulpin;
    private String documentType;
    private String documentName;
    private String uploadedDate;
    private String extractedText;
    private String verificationStatus;
    private double confidenceScore;

    public DocumentRecord() {
    }

    public DocumentRecord(
            String documentId,
            String ulpin,
            String documentType,
            String documentName,
            String uploadedDate,
            String extractedText,
            String verificationStatus,
            double confidenceScore
    ) {
        this.documentId = documentId;
        this.ulpin = ulpin;
        this.documentType = documentType;
        this.documentName = documentName;
        this.uploadedDate = uploadedDate;
        this.extractedText = extractedText;
        this.verificationStatus = verificationStatus;
        this.confidenceScore = confidenceScore;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getUlpin() {
        return ulpin;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getUploadedDate() {
        return uploadedDate;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }
}