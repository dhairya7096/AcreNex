package com.acrenex.app.models;

public class OwnershipRecord {

    private String id;
    private String ulpin;
    private String ownerName;
    private String ownershipType;
    private String share;
    private String recordDate;
    private String source;
    private String verificationStatus;

    public OwnershipRecord() {
    }

    public OwnershipRecord(
            String id,
            String ulpin,
            String ownerName,
            String ownershipType,
            String share,
            String recordDate,
            String source,
            String verificationStatus
    ) {
        this.id = id;
        this.ulpin = ulpin;
        this.ownerName = ownerName;
        this.ownershipType = ownershipType;
        this.share = share;
        this.recordDate = recordDate;
        this.source = source;
        this.verificationStatus = verificationStatus;
    }

    public String getId() {
        return id;
    }

    public String getUlpin() {
        return ulpin;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnershipType() {
        return ownershipType;
    }

    public String getShare() {
        return share;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public String getSource() {
        return source;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }
}