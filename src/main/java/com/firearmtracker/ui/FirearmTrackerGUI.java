package com.firearmtracker.ui;

import com.firearmtracker.model.Employee;
import com.firearmtracker.model.Firearm;
import com.firearmtracker.service.InventoryManagementSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FirearmTrackerGUI extends JFrame {
    private InventoryManagementSystem inventorySystem;
    private JTable employeeTable;
    private JTable availableFirearmsTable;

    public FirearmTrackerGUI(InventoryManagementSystem inventorySystem) {
        this.inventorySystem = inventorySystem;
        initComponents();
    }

    private void initComponents() {
        setTitle("Firearm Inventory Tracking System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main panel
        JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        add(mainPanel, BorderLayout.CENTER);

        // Employees with Firearms
        String[] employeeColumns = {"Employee ID", "Name", "Contract", "Firearm Serial", "Firearm Type", "Issued By"};
        DefaultTableModel employeeModel = new DefaultTableModel(employeeColumns, 0);
        employeeTable = new JTable(employeeModel);
        JScrollPane employeeScrollPane = new JScrollPane(employeeTable);
        employeeScrollPane.setBorder(BorderFactory.createTitledBorder("Employees with Firearms"));
        mainPanel.add(employeeScrollPane);

        // Available Firearms
        String[] firearmColumns = {"Serial Number", "Type", "Status"};
        DefaultTableModel firearmModel = new DefaultTableModel(firearmColumns, 0);
        availableFirearmsTable = new JTable(firearmModel);
        JScrollPane firearmScrollPane = new JScrollPane(availableFirearmsTable);
        firearmScrollPane.setBorder(BorderFactory.createTitledBorder("Available Firearms"));
        mainPanel.add(firearmScrollPane);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        add(buttonPanel, BorderLayout.SOUTH);

        JButton addEmployeeButton = new JButton("Add an Employee to the System");
        addEmployeeButton.addActionListener(e -> addEmployee());
        buttonPanel.add(addEmployeeButton);

        JButton removeEmployeeButton = new JButton("Remove an Employee from the System");
        removeEmployeeButton.addActionListener(e -> removeEmployee());
        buttonPanel.add(removeEmployeeButton);

        JButton addFirearmButton = new JButton("Add a Firearm to the System");
        addFirearmButton.addActionListener(e -> addFirearm());
        buttonPanel.add(addFirearmButton);

        JButton removeFirearmButton = new JButton("Remove a Firearm from the System");
        removeFirearmButton.addActionListener(e -> removeFirearm());
        buttonPanel.add(removeFirearmButton);

        JButton issueFirearmButton = new JButton("Issue a Firearm to an Employee");
        issueFirearmButton.addActionListener(e -> issueFirearm());
        buttonPanel.add(issueFirearmButton);

        JButton returnFirearmButton = new JButton("Collect a Firearm from an Employee");
        returnFirearmButton.addActionListener(e -> returnFirearm());
        buttonPanel.add(returnFirearmButton);

        // Populate Tables
        refreshTables();

        setVisible(true);
    }

    private void refreshTables() {
        DefaultTableModel employeeModel = (DefaultTableModel) employeeTable.getModel();
        DefaultTableModel firearmModel = (DefaultTableModel) availableFirearmsTable.getModel();

        // Clear existing rows
        employeeModel.setRowCount(0);  // Clears existing rows
        firearmModel.setRowCount(0);   // Clears existing rows

        // Populate Employees Table
        List<Employee> employeesWithFirearms = inventorySystem.getEmployees();
        for (Employee emp : employeesWithFirearms) {
            Firearm firearm = emp.getAssignedFirearm();  // Assuming this method exists in Employee
            employeeModel.addRow(new Object[]{
                    emp.getEmployeeId(),
                    emp.getFullName(),
                    emp.getContract(),
                    firearm != null ? firearm.getSerialNumber() : "None",
                    firearm != null ? firearm.getType() : "None",
                    firearm != null ? firearm.getIssuedBy() : "None"
            });
        }

        // Populate Available Firearms Table
        List<Firearm> availableFirearms = inventorySystem.getAvailableFirearms();
        for (Firearm firearm : availableFirearms) {
            firearmModel.addRow(new Object[]{
                    firearm.getSerialNumber(),
                    firearm.getType(),
                    firearm.isIssued() ? "Issued" : "Available"
            });
        }
    }

    private void addEmployee() {
        try {
            String firstName = JOptionPane.showInputDialog(this, "Enter First Name:");
            String lastName = JOptionPane.showInputDialog(this, "Enter Last Name:");
            String contract = JOptionPane.showInputDialog(this, "Enter Contract:");

            if (firstName == null || lastName == null || contract == null ||
                    firstName.trim().isEmpty() || lastName.trim().isEmpty() || contract.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create and add the new employee to the system
            int id = inventorySystem.getNextEmployeeId();  // Make sure this method is working correctly
            Employee newEmployee = new Employee(id, firstName, lastName, contract);
            inventorySystem.addEmployee(newEmployee);

            JOptionPane.showMessageDialog(this, "Employee added successfully!");

            // Refresh the tables to reflect the new employee
            refreshTables();  // This is the key point

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeEmployee() {
        try {
            // Prompt the user for the Employee ID
            String employeeIdInput = JOptionPane.showInputDialog(this, "Enter Employee ID to remove:");
            int employeeId = Integer.parseInt(employeeIdInput);

            // Find the employee
            Employee employee = inventorySystem.getEmployees()
                    .stream()
                    .filter(emp -> emp.getEmployeeId() == employeeId)
                    .findFirst()
                    .orElse(null);

            if (employee != null) {
                // If the employee has an assigned firearm, return it first
                if (employee.getAssignedFirearm() != null) {
                    inventorySystem.returnFirearm(employee);
                }

                // Remove the employee
                inventorySystem.removeEmployee(employee);
                JOptionPane.showMessageDialog(this, "Employee removed successfully.");
                refreshTables();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Employee ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input for Employee ID. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error removing employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addFirearm() {
        try {
            String serialNumber = JOptionPane.showInputDialog(this, "Enter Serial Number:");
            String type = JOptionPane.showInputDialog(this, "Enter Firearm Type:");
            Firearm newFirearm = new Firearm(serialNumber, type);
            inventorySystem.addFirearm(newFirearm);
            refreshTables();
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding firearm: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void issueFirearm() {
        try {
            String employeeIdInput = JOptionPane.showInputDialog(this, "Enter Employee ID:");
            String serialNumberInput = JOptionPane.showInputDialog(this, "Enter Firearm Serial Number:");
            String issuedBy = JOptionPane.showInputDialog(this, "Issued By (e.g., Admin or Officer Name):");

            int employeeId = Integer.parseInt(employeeIdInput);
            inventorySystem.issueFirearm(employeeId, serialNumberInput, issuedBy); // Pass issuedBy
            JOptionPane.showMessageDialog(this, "Firearm issued successfully!");

            refreshTables();
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input for employee ID. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnFirearm() {
        try {
            String employeeIdStr = JOptionPane.showInputDialog(this, "Enter Employee ID:");
            int employeeId = Integer.parseInt(employeeIdStr);
            Employee employee = inventorySystem.getEmployees()
                    .stream()
                    .filter(emp -> emp.getEmployeeId() == employeeId)
                    .findFirst()
                    .orElse(null);
            if (employee != null) {
                inventorySystem.returnFirearm(employee);
                JOptionPane.showMessageDialog(this, "Firearm returned successfully!");
                // Refresh the tables to reflect the change
                refreshTables();
            }
            else {
                JOptionPane.showMessageDialog(this, "Invalid Employee ID", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input for employee ID. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error returning firearm: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeFirearm() {
        try {
            String serialNumber = JOptionPane.showInputDialog(this, "Enter Firearm Serial Number to Remove:");
            Firearm firearm = inventorySystem.getAvailableFirearms()
                    .stream()
                    .filter(f -> f.getSerialNumber().equals(serialNumber))
                    .findFirst()
                    .orElse(null);
            if (firearm != null) {
                inventorySystem.removeFirearm(firearm);
                JOptionPane.showMessageDialog(this, "Firearm removed successfully!");
                refreshTables();
            }
            else {
                JOptionPane.showMessageDialog(this, "Invalid Firearm Serial Number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error removing firearm: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}