package com.firearmtracker.model;

public class Employee {
    private int employeeId;
    private String firstName;
    private String lastName;
    private String contractType;
    private Firearm assignedFirearm;

    public Employee(int employeeId, String firstName, String lastName, String contractType) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contractType = contractType;
        this.assignedFirearm = null;
    }

    public void assignFirearm(Firearm firearm) {
        this.assignedFirearm = firearm;
    }

    public void returnFirearm() {
        this.assignedFirearm = null;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getContractType() {
        return contractType;
    }

    public Firearm getAssignedFirearm() {
        return assignedFirearm;
    }
}