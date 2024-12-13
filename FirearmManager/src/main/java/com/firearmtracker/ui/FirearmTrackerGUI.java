package com.firearmtracker.ui;

import com.firearmtracker.model.Employee;
import com.firearmtracker.model.Firearm;
import com.firearmtracker.service.InventoryManagementSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FirearmTrackerGUI extends JFrame {
    private InventoryManagementSystem inventorySystem;
    private JTable employeeTable;
    private JTable availableFirearmsTable;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public FirearmTrackerGUI(InventoryManagementSystem inventorySystem) {
        this.inventorySystem = inventorySystem;
        initComponents();
    }

    private void initComponents() {
        setTitle("Firearm Inventory Tracking System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create card layout for switching between panels
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create panels
        JPanel mainMenuPanel = createMainMenu();
        JPanel databasePanel = createDatabasePanel();

        // Add panels to card layout
        mainPanel.add(mainMenuPanel, "MAIN_MENU");
        mainPanel.add(databasePanel, "DATABASE");

        // Add main panel to frame
        add(mainPanel);

        // Show main menu initially
        cardLayout.show(mainPanel, "MAIN_MENU");
        setVisible(true);
    }

    private JPanel createMainMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Strategic Security Firearm Database", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        JButton lookupBySerialButton = new JButton("Lookup by Serial #");
        lookupBySerialButton.addActionListener(e -> lookupBySerialNumber());
        buttonPanel.add(lookupBySerialButton);

        JButton employeeLookupButton = new JButton("Employee Lookup");
        employeeLookupButton.addActionListener(e -> employeeLookup());
        buttonPanel.add(employeeLookupButton);

        JButton openDatabaseButton = new JButton("Open Database");
        openDatabaseButton.addActionListener(e -> showDatabaseView());
        buttonPanel.add(openDatabaseButton);

        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDatabasePanel() {
        JPanel databasePanel = new JPanel(new BorderLayout());

        // Create tables panel
        JPanel tablesPanel = new JPanel(new GridLayout(2, 1));

        // Employee table
        String[] employeeColumns = {"Employee ID", "Name", "Contract", "Firearm Serial", "Firearm Type", "Issued By"};
        DefaultTableModel employeeModel = new DefaultTableModel(employeeColumns, 0);
        employeeTable = new JTable(employeeModel);
        JScrollPane employeeScrollPane = new JScrollPane(employeeTable);
        employeeScrollPane.setBorder(BorderFactory.createTitledBorder("Employees with Firearms"));
        tablesPanel.add(employeeScrollPane);

        // Firearms table
        String[] firearmColumns = {"Serial Number", "Type", "Status"};
        DefaultTableModel firearmModel = new DefaultTableModel(firearmColumns, 0);
        availableFirearmsTable = new JTable(firearmModel);
        JScrollPane firearmScrollPane = new JScrollPane(availableFirearmsTable);
        firearmScrollPane.setBorder(BorderFactory.createTitledBorder("Available Firearms"));
        tablesPanel.add(firearmScrollPane);

        databasePanel.add(tablesPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        JButton addEmployeeButton = new JButton("Add Employee");
        addEmployeeButton.addActionListener(e -> addEmployee());
        buttonPanel.add(addEmployeeButton);

        JButton removeEmployeeButton = new JButton("Remove Employee");
        removeEmployeeButton.addActionListener(e -> removeEmployee());
        buttonPanel.add(removeEmployeeButton);

        JButton addFirearmButton = new JButton("Add Firearm");
        addFirearmButton.addActionListener(e -> addFirearm());
        buttonPanel.add(addFirearmButton);

        JButton removeFirearmButton = new JButton("Remove Firearm");
        removeFirearmButton.addActionListener(e -> removeFirearm());
        buttonPanel.add(removeFirearmButton);

        JButton issueFirearmButton = new JButton("Issue Firearm");
        issueFirearmButton.addActionListener(e -> issueFirearm());
        buttonPanel.add(issueFirearmButton);

        JButton returnFirearmButton = new JButton("Return Firearm");
        returnFirearmButton.addActionListener(e -> returnFirearm());
        buttonPanel.add(returnFirearmButton);

        JButton returnToMainMenuButton = new JButton("Return to Main Menu");
        returnToMainMenuButton.addActionListener(e -> returnToMainMenu());
        buttonPanel.add(returnToMainMenuButton);

        databasePanel.add(buttonPanel, BorderLayout.EAST);
        return databasePanel;
    }

    // Navigation methods
    private void showDatabaseView() {
        cardLayout.show(mainPanel, "DATABASE");
    }

    private void returnToMainMenu() {
        cardLayout.show(mainPanel, "MAIN_MENU");
    }

    // Action handlers
    private void lookupBySerialNumber() {
        String serialNumber = JOptionPane.showInputDialog(this, "Enter Firearm Serial Number:");
        if (serialNumber != null && !serialNumber.isBlank()) {
            Firearm firearm = inventorySystem.getFirearmBySerial(serialNumber);
            if (firearm != null) {
                String result = String.format("""
                        Serial Number: %s
                        Type: %s
                        Status: %s
                        Location: %s
                        State: %s
                        """,
                        firearm.getSerialNumber(),
                        firearm.getType(),
                        firearm.isIssued() ? "Issued" : "Available",
                        firearm.getLocation(),
                        firearm.getState() != null ? firearm.getState() : "N/A"
                );
                JOptionPane.showMessageDialog(this, result, "Lookup Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Firearm not found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void employeeLookup() {
        String employeeIdInput = JOptionPane.showInputDialog(this, "Enter Employee ID:");
        if (employeeIdInput != null && !employeeIdInput.isBlank()) {
            try {
                int employeeId = Integer.parseInt(employeeIdInput);
                Employee employee = inventorySystem.getEmployeeById(employeeId);
                if (employee != null) {
                    Firearm assignedFirearm = employee.getAssignedFirearm();
                    String result = String.format("""
                            Employee ID: %d
                            Name: %s
                            Contract Type: %s
                            Assigned Firearm: %s
                            """,
                            employee.getEmployeeId(),
                            employee.getFullName(),
                            employee.getContractType(),
                            assignedFirearm != null ? assignedFirearm.getSerialNumber() : "None"
                    );
                    JOptionPane.showMessageDialog(this, result, "Lookup Result", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Employee not found.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid Employee ID format",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addEmployee() {
        // Logic for adding an employee
        System.out.println("Add Employee");
    }

    private void removeEmployee() {
        // Logic for removing an employee
        System.out.println("Remove Employee");
    }

    private void addFirearm() {
        // Logic for adding a firearm
        System.out.println("Add Firearm");
    }

    private void removeFirearm() {
        // Logic for removing a firearm
        System.out.println("Remove Firearm");
    }

    private void issueFirearm() {
        // Logic for issuing a firearm
        System.out.println("Issue Firearm");
    }

    private void returnFirearm() {
        // Logic for returning a firearm
        System.out.println("Return Firearm");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InventoryManagementSystem inventorySystem = new InventoryManagementSystem();
            new FirearmTrackerGUI(inventorySystem);
        });
    }
}