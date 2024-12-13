package com.firearmtracker.ui;

import com.firearmtracker.model.Employee;
import com.firearmtracker.model.Firearm;
import com.firearmtracker.model.User;
import com.firearmtracker.model.UserRole;
import com.firearmtracker.service.InventoryManagementSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabasePanel extends JPanel {
    private InventoryManagementSystem inventorySystem;
    private LoginScreen loginScreen;
    private JTable employeeTable;
    private JTable firearmTable;
    private DefaultTableModel employeeTableModel, firearmTableModel;
    private User currentUser;

    public DatabasePanel(InventoryManagementSystem inventorySystem, LoginScreen loginScreen) {
        this.inventorySystem = inventorySystem;
        this.loginScreen = loginScreen;
        this.currentUser = loginScreen.getCurrentUser();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Create tables panel
        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        tablesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create tables (make them read-only)
        createTables(tablesPanel);

        // Create button panel based on user role
        if (currentUser.getRole() != UserRole.EMPLOYEE) {
            JPanel buttonPanel = createButtonPanel();
            add(buttonPanel, BorderLayout.EAST);
        }

        add(tablesPanel, BorderLayout.CENTER);

        // Initialize tables
        updateTables();
    }

    private void createTables(JPanel tablesPanel) {
        // Employee table
        String[] employeeColumns = {"Employee ID", "Name", "Contract", "Firearm Serial", "Firearm Type", "Issued By", "Date Issued"};
        employeeTableModel = new DefaultTableModel(employeeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        employeeTable = new JTable(employeeTableModel);
        JScrollPane employeeScrollPane = new JScrollPane(employeeTable);
        employeeScrollPane.setBorder(BorderFactory.createTitledBorder("All Employees"));
        tablesPanel.add(employeeScrollPane);

        // Firearms table
        String[] firearmColumns = {"Serial Number", "Type", "Status", "Location", "State"};
        firearmTableModel = new DefaultTableModel(firearmColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        firearmTable = new JTable(firearmTableModel);
        JScrollPane firearmScrollPane = new JScrollPane(firearmTable);
        firearmScrollPane.setBorder(BorderFactory.createTitledBorder("Available Firearms"));
        tablesPanel.add(firearmScrollPane);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setPreferredSize(new Dimension(200, getHeight()));

        // Add role-specific buttons
        if (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.OPERATIONS) {
            addButton(buttonPanel, "Add Employee", e -> addEmployee());
            addButton(buttonPanel, "Remove Employee", e -> removeEmployee());
            addButton(buttonPanel, "Add Firearm", e -> addFirearm());
            addButton(buttonPanel, "Remove Firearm", e -> removeFirearm());
            addButton(buttonPanel, "Issue Firearm", e -> issueFirearm());
            addButton(buttonPanel, "Return Firearm", e -> returnFirearm());
            addButton(buttonPanel, "Update Firearm Location", e -> updateLocation());
        }

        // Common buttons
        addButton(buttonPanel, "Return to Main Menu", e -> returnToMainMenu());
        addButton(buttonPanel, "Logout", e -> logout());

        return buttonPanel;
    }

    private void addButton(JPanel panel, String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.addActionListener(e -> {
            if (hasPermissionForAction(text)) {
                listener.actionPerformed(e);
                logAction(text);
            } else {
                JOptionPane.showMessageDialog(this,
                        "You don't have permission to perform this action",
                        "Permission Denied",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private boolean hasPermissionForAction(String action) {
        return switch (action) {
            case "Add Employee", "Remove Employee", "Add Firearm", "Remove Firearm" ->
                    currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.OPERATIONS;
            case "Issue Firearm", "Return Firearm", "Update Firearm Location" ->
                    currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.OPERATIONS;
            case "Return to Main Menu", "Logout" -> true;
            default -> false;
        };
    }

    private void logAction(String action) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(String.format("[%s] User %s (%s) performed action: %s",
                timestamp,
                currentUser.getUsername(),
                currentUser.getRole(),
                action));
    }

    private void addEmployee() {
        String firstName = JOptionPane.showInputDialog(this, "Enter the employee's first name:");
        String lastName = JOptionPane.showInputDialog(this, "Enter the employee's last name:");
        String contractType = JOptionPane.showInputDialog(this, "Enter the employee's contract type:");
        inventorySystem.addEmployee(firstName, lastName, contractType);
        updateTables();
    }

    private void removeEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow >= 0) {
            int employeeId = (int) employeeTable.getValueAt(selectedRow, 0);
            Employee employee = inventorySystem.getEmployeeById(employeeId);
            if (employee != null) {
                inventorySystem.removeEmployee(employee);
                updateTables();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to remove.");
        }
    }

    private void addFirearm() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField serialField = new JTextField();

        // Dropdown for firearm type/make
        String[] types = {"Glock", "Delta"};
        JComboBox<String> typeBox = new JComboBox<>(types);

        // Dropdown for location
        String[] locations = {"Armory", "Range", "Supervisor", "N/A"};
        JComboBox<String> locationBox = new JComboBox<>(locations);

        // Dropdown for states
        String[] states = {
                "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut",
                "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa",
                "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan",
                "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire",
                "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio",
                "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota",
                "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia",
                "Wisconsin", "Wyoming", "N/A"
        };
        JComboBox<String> stateBox = new JComboBox<>(states);

        panel.add(new JLabel("Serial Number:"));
        panel.add(serialField);
        panel.add(new JLabel("Type:"));
        panel.add(typeBox);
        panel.add(new JLabel("Location:"));
        panel.add(locationBox);
        panel.add(new JLabel("State:"));
        panel.add(stateBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Firearm",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String serial = serialField.getText().trim();
            String type = (String) typeBox.getSelectedItem();
            String location = (String) locationBox.getSelectedItem();
            String state = (String) stateBox.getSelectedItem();

            if (!serial.isEmpty()) {
                try {
                    Firearm newFirearm = new Firearm(serial, type);
                    newFirearm.setLocation(location);
                    newFirearm.setState(state);
                    inventorySystem.addFirearm(newFirearm);
                    updateTables();
                    JOptionPane.showMessageDialog(this,
                            "Firearm added successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Error adding firearm: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please enter a serial number",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeFirearm() {
        int selectedRow = firearmTable.getSelectedRow();
        if (selectedRow >= 0) {
            String serialNumber = (String) firearmTable.getValueAt(selectedRow, 0);
            Firearm firearm = inventorySystem.getFirearmBySerial(serialNumber);
            if (firearm != null) {
                inventorySystem.removeFirearm(firearm);
                updateTables();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a firearm to remove.");
        }
    }

    private void issueFirearm() {
        int selectedEmployeeRow = employeeTable.getSelectedRow();
        int selectedFirearmRow = firearmTable.getSelectedRow();
        if (selectedEmployeeRow >= 0 && selectedFirearmRow >= 0) {
            int employeeId = (int) employeeTable.getValueAt(selectedEmployeeRow, 0);
            String serialNumber = (String) firearmTable.getValueAt(selectedFirearmRow, 0);
            String issuedBy = JOptionPane.showInputDialog(this, "Enter the name of the person issuing the firearm:");
            Employee employee = inventorySystem.getEmployeeById(employeeId);
            Firearm firearm = inventorySystem.getFirearmBySerial(serialNumber);
            if (employee != null && firearm != null && !firearm.isIssued()) {
                inventorySystem.issueFirearm(employee.getEmployeeId(), firearm.getSerialNumber(), issuedBy);
                updateTables();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid employee and an available firearm.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee and a firearm to issue.");
        }
    }

    private void returnFirearm() {
        int selectedEmployeeRow = employeeTable.getSelectedRow();
        if (selectedEmployeeRow >= 0) {
            int employeeId = (int) employeeTable.getValueAt(selectedEmployeeRow, 0);
            Employee employee = inventorySystem.getEmployeeById(employeeId);
            if (employee != null && employee.getAssignedFirearm() != null) {
                inventorySystem.returnFirearm(employee);
                updateTables();
            } else {
                JOptionPane.showMessageDialog(this, "The selected employee does not have an assigned firearm.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to return a firearm.");
        }
    }

    private void updateLocation() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField serialField = new JTextField();

        // Dropdown for location
        String[] locations = {"Armory", "Range", "Maintenance", "Field", "New York"};
        JComboBox<String> locationBox = new JComboBox<>(locations);

        // Dropdown for states
        String[] states = {
                "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut",
                "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa",
                "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan",
                "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire",
                "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio",
                "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota",
                "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia",
                "Wisconsin", "Wyoming", "N/A"
        };
        JComboBox<String> stateBox = new JComboBox<>(states);

        // First, show serial number input
        panel.add(new JLabel("Firearm Serial:"));
        panel.add(serialField);
        panel.add(new JLabel("New Location:"));
        panel.add(locationBox);
        panel.add(new JLabel("State:"));
        panel.add(stateBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Firearm Location",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String serial = serialField.getText().trim();
            String newLocation = (String) locationBox.getSelectedItem();
            String newState = (String) stateBox.getSelectedItem();

            if (!serial.isEmpty()) {
                try {
                    Firearm firearmToUpdate = findFirearmBySerial(serial);

                    if (firearmToUpdate != null) {
                        firearmToUpdate.setLocation(newLocation);
                        firearmToUpdate.setState(newState);
                        updateTables();
                        JOptionPane.showMessageDialog(this,
                                "Location updated successfully",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Firearm not found",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Error updating location: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please enter a serial number",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateTables() {
        // Update Employee table
        employeeTableModel.setRowCount(0);
        for (Employee employee : inventorySystem.getEmployees()) {
            Firearm firearm = employee.getAssignedFirearm();
            employeeTableModel.addRow(new Object[]{
                    employee.getEmployeeId(),
                    employee.getFullName(),
                    employee.getContractType(),
                    firearm != null ? firearm.getSerialNumber() : "No Firearm",
                    firearm != null ? firearm.getType() : "N/A",
                    firearm != null ? firearm.getIssuedBy() : "N/A",
                    firearm != null ? firearm.getDateIssued() : "N/A"
            });
        }

        // Update Firearms table
        firearmTableModel.setRowCount(0);
        for (Firearm firearm : inventorySystem.getAvailableFirearms()) {
            firearmTableModel.addRow(new Object[]{
                    firearm.getSerialNumber(),
                    firearm.getType(),
                    firearm.isIssued() ? "Issued" : "Available",
                    firearm.getLocation(),
                    firearm.getState() != null ? firearm.getState() : "N/A"
            });
        }
    }

    private void returnToMainMenu() {
        logAction("Return to Main Menu");
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.dispose();
        MainMenu mainMenu = new MainMenu(inventorySystem, loginScreen, currentUser);
        mainMenu.setVisible(true);
    }

    private void logout() {
        logAction("Logout");
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) {
            topFrame.dispose();
        }
        loginScreen.setVisible(false);
        loginScreen = new LoginScreen(inventorySystem);
        loginScreen.setVisible(true);
    }

    // Helper methods
    private Firearm findFirearmBySerial(String serial) {
        return inventorySystem.getFirearmBySerial(serial);
    }

    private Employee findEmployeeById(int id) {
        return inventorySystem.getEmployeeById(id);
    }
}