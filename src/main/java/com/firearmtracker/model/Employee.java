package com.firearmtracker.model;

public class Employee {
    private int employeeId;
    private String firstName;
    private String lastName;
    private String contract;
    private Firearm assignedFirearm;

    public Employee(int employeeId, String firstName, String lastName, String department) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contract = department;
    }

    public void assignFirearm(Firearm firearm) {
        this.assignedFirearm = firearm;
    }

    public void returnFirearm() {
        this.assignedFirearm = null;
    }

    // Getters and Setters
    public int getEmployeeId() { return employeeId; }
    public String getFullName() { return firstName + " " + lastName; }
    public Firearm getAssignedFirearm() { return assignedFirearm; }

    public Object getContract() { return contract; }

    }


