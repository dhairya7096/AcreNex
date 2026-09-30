package com.acrenex.app.models;

public class Parcel {

    private String id;
    private String ulpin;
    private String location;
    private String landUse;

    private double areaAcres;

    private boolean ownershipVerified;
    private boolean registrationVerified;
    private boolean taxVerified;
    private boolean landUseVerified;
    private boolean documentsVerified;

    public Parcel() {
    }

    public Parcel(
            String id,
            String ulpin,
            String location,
            String landUse,
            double areaAcres,
            boolean ownershipVerified,
            boolean registrationVerified,
            boolean taxVerified,
            boolean landUseVerified,
            boolean documentsVerified
    ) {
        this.id = id;
        this.ulpin = ulpin;
        this.location = location;
        this.landUse = landUse;
        this.areaAcres = areaAcres;
        this.ownershipVerified = ownershipVerified;
        this.registrationVerified = registrationVerified;
        this.taxVerified = taxVerified;
        this.landUseVerified = landUseVerified;
        this.documentsVerified = documentsVerified;
    }

    public String getId() {
        return id;
    }

    public String getUlpin() {
        return ulpin;
    }

    public String getLocation() {
        return location;
    }

    public String getLandUse() {
        return landUse;
    }

    public double getAreaAcres() {
        return areaAcres;
    }

    public boolean isOwnershipVerified() {
        return ownershipVerified;
    }

    public boolean isRegistrationVerified() {
        return registrationVerified;
    }

    public boolean isTaxVerified() {
        return taxVerified;
    }

    public boolean isLandUseVerified() {
        return landUseVerified;
    }

    public boolean isDocumentsVerified() {
        return documentsVerified;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUlpin(String ulpin) {
        this.ulpin = ulpin;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLandUse(String landUse) {
        this.landUse = landUse;
    }

    public void setAreaAcres(double areaAcres) {
        this.areaAcres = areaAcres;
    }

    public void setOwnershipVerified(boolean ownershipVerified) {
        this.ownershipVerified = ownershipVerified;
    }

    public void setRegistrationVerified(boolean registrationVerified) {
        this.registrationVerified = registrationVerified;
    }

    public void setTaxVerified(boolean taxVerified) {
        this.taxVerified = taxVerified;
    }

    public void setLandUseVerified(boolean landUseVerified) {
        this.landUseVerified = landUseVerified;
    }

    public void setDocumentsVerified(boolean documentsVerified) {
        this.documentsVerified = documentsVerified;
    }
}