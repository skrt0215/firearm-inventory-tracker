package com.firearmtracker.service;

import com.firearmtracker.model.Employee;
import com.firearmtracker.model.Firearm;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryManagementSystem {
    private List<Employee> employees;
    private List<Firearm> firearms;
    private int employeeIdCounter = 1;

    public InventoryManagementSystem() {
        employees = new ArrayList<>();
        firearms = new ArrayList<>();
    }

    public void addEmployee(Employee employee) {
        if (employee != null) {
            employees.add(employee); // 'employees' is your list of Employee objects
        }
        else {
            throw new IllegalArgumentException("Employee cannot be null");
        }
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
    }


    public void addFirearm(Firearm firearm) {
        firearms.add(firearm);
    }
    public void removeFirearm(Firearm firearm) {
        if (firearm != null && firearms.contains(firearm)) {
            firearms.remove(firearm);
        }
        else {
            throw new IllegalArgumentException("Firearm not found in the inventory");
        }
    }
    public void issueFirearm(int employeeId, String firearmSerialNumber, String issuedBy) {
        Employee employee = employees.stream()
                .filter(emp -> emp.getEmployeeId() == employeeId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));

        Firearm firearm = firearms.stream()
                .filter(f -> f.getSerialNumber().equals(firearmSerialNumber))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid firearm serial number"));

        if (!firearm.isIssued()) {
            employee.assignFirearm(firearm);
            firearm.issueFirearm(issuedBy);  // Use issueFirearm() to set both "Issued By" and "Date Issued"
        } else {
            throw new IllegalStateException("Firearm is already issued");
        }
    }

    public void returnFirearm(Employee employee) {
        Firearm assignedFirearm = employee.getAssignedFirearm();
        if (assignedFirearm != null) {
            assignedFirearm.returnFirearm();
            employee.returnFirearm();
        }
    }

    public List<Employee> getEmployeesWithIssuedFirearms() {
        return employees.stream()
                .filter(emp -> emp.getAssignedFirearm() != null)
                .collect(Collectors.toList());
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public int getNextEmployeeId() {
        return employeeIdCounter++;
    }

    public List<Firearm> getAvailableFirearms() {
        List<Firearm> availableFirearms = new ArrayList<>();
        for (Firearm firearm : firearms) {
            if (!firearm.isIssued()) {  // Only add firearms that are not issued
                availableFirearms.add(firearm);
            }
        }
        return availableFirearms;
    }

}


