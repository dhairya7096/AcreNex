package com.acrenex.app.models;

public class RegistrationRecord {

    private String registrationId;
    private String ulpin;
    private String documentNumber;
    private String registrationDate;
    private String transactionType;
    private String buyerName;
    private String sellerName;
    private String status;

    public RegistrationRecord() {
    }

    public RegistrationRecord(
            String registrationId,
            String ulpin,
            String documentNumber,
            String registrationDate,
            String transactionType,
            String buyerName,
            String sellerName,
            String status
    ) {
        this.registrationId = registrationId;
        this.ulpin = ulpin;
        this.documentNumber = documentNumber;
        this.registrationDate = registrationDate;
        this.transactionType = transactionType;
        this.buyerName = buyerName;
        this.sellerName = sellerName;
        this.status = status;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getUlpin() {
        return ulpin;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getStatus() {
        return status;
    }
}