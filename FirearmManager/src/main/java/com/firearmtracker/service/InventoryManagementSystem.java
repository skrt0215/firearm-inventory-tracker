package com.firearmtracker.service;

import com.firearmtracker.model.Employee;
import com.firearmtracker.model.Firearm;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryManagementSystem {
    private static InventoryManagementSystem instance;
    private List<Employee> employees;
    private List<Firearm> firearms;
    private int employeeIdCounter = 1;

    // Private constructor for singleton
    public InventoryManagementSystem() {
        employees = new ArrayList<>();
        firearms = new ArrayList<>();
    }

    // Singleton getInstance method
    public static InventoryManagementSystem getInstance() {
        if (instance == null) {
            instance = new InventoryManagementSystem();
        }
        return instance;
    }

    public void addEmployee(Employee employee) {
        if (employee != null) {
            employees.add(employee);
        } else {
            throw new IllegalArgumentException("Employee cannot be null");
        }
    }

    public int addEmployee(String firstName, String lastName, String contractType) {
        Employee newEmployee = new Employee(getNextEmployeeId(), firstName, lastName, contractType);
        employees.add(newEmployee);
        return newEmployee.getEmployeeId();
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
    }

    public void addFirearm(Firearm firearm) {
        firearms.add(firearm);
    }

    public void addFirearm(String serialNumber, String type) {
        Firearm newFirearm = new Firearm(serialNumber, type);
        firearms.add(newFirearm);
    }

    public void removeFirearm(Firearm firearm) {
        if (firearm != null && firearms.contains(firearm)) {
            firearms.remove(firearm);
        } else {
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
            firearm.issue(issuedBy);
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

    public void updateFirearmLocation(Firearm firearm, String newLocation) {
        firearm.setLocation(newLocation);
    }

    // Getters and utility methods
    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Employee> getEmployeesWithIssuedFirearms() {
        return employees.stream()
                .filter(emp -> emp.getAssignedFirearm() != null)
                .collect(Collectors.toList());
    }

    public List<Firearm> getAvailableFirearms() {
        return firearms.stream()
                .filter(f -> !f.isIssued())
                .collect(Collectors.toList());
    }

    public List<Firearm> getFirearms() {
        return firearms;
    }

    public int getNextEmployeeId() {
        return employeeIdCounter++;
    }

    public Firearm getFirearmBySerial(String serialNumber) {
        return firearms.stream()
                .filter(f -> f.getSerialNumber().equals(serialNumber))
                .findFirst()
                .orElse(null);
    }

    public Employee getEmployeeById(int employeeId) {
        return employees.stream()
                .filter(emp -> emp.getEmployeeId() == employeeId)
                .findFirst()
                .orElse(null);
    }
}