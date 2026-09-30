package com.acrenex.app.models;

public class TaxRecord {

    private String id;
    private String ulpin;
    private double annualTax;
    private double outstandingAmount;
    private String financialYear;
    private String lastPaymentDate;
    private String status;

    public TaxRecord() {
    }

    public TaxRecord(
            String id,
            String ulpin,
            double annualTax,
            double outstandingAmount,
            String financialYear,
            String lastPaymentDate,
            String status
    ) {
        this.id = id;
        this.ulpin = ulpin;
        this.annualTax = annualTax;
        this.outstandingAmount = outstandingAmount;
        this.financialYear = financialYear;
        this.lastPaymentDate = lastPaymentDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getUlpin() {
        return ulpin;
    }

    public double getAnnualTax() {
        return annualTax;
    }

    public double getOutstandingAmount() {
        return outstandingAmount;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public String getLastPaymentDate() {
        return lastPaymentDate;
    }

    public String getStatus() {
        return status;
    }
}