package com.firearmtracker.model;

public class Firearm {
    private String serialNumber;
    private String type; // Glock or Delta
    private String location;
    private String state;
    private boolean issued;
    private String issuedBy;
    private String dateIssued;

    public Firearm(String serialNumber, String type) {
        this.serialNumber = serialNumber;
        this.type = type;
        this.issued = false;
        this.location = "Armory"; // Default location
        this.issuedBy = null;
        this.state = null;
        this.dateIssued = null;
    }

    public void issue(String issuedBy) {
        if (!issued) {
            this.issued = true;
            this.issuedBy = issuedBy;
            this.dateIssued = java.time.LocalDate.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("MM/dd")
            );
            this.location = "Issued";
        }
    }

    public void returnFirearm() {
        this.issued = false;
        this.issuedBy = null;
        this.dateIssued = null;
        this.location = "Armory";
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getType() {
        return type;
    }

    public boolean isIssued() {
        return issued;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public String getDateIssued() {
        return dateIssued;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}