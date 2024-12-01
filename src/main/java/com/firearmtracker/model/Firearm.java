package com.firearmtracker.model;

public class Firearm {
    private String serialNumber;
    private String type; // Glock or Delta
    private boolean isIssued;
    private String dateIssued; // Format as a string for simplicity
    private String issuedBy;

    public Firearm(String serialNumber, String type) {
        this.serialNumber = serialNumber;
        this.type = type;
        this.isIssued = false;
        this.issuedBy = null; // Initialize as null
    }

    public void issue() {
        if (!isIssued) {
            isIssued = true;
            dateIssued = java.time.LocalDate.now().toString(); // Sets current date as "YYYY-MM-DD"
        }
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void issueFirearm(String issuedBy) {
        this.isIssued = true;
        this.issuedBy = issuedBy;  // Set the name of the person who issued the firearm
    }

    public void returnFirearm() {
        this.isIssued = false;
    }

    public String getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(String dateIssued) {
        this.dateIssued = dateIssued;
    }
    // Getters
    public String getSerialNumber() { return serialNumber; }
    public String getType() { return type; }
    public boolean isIssued() { return isIssued; }
}

