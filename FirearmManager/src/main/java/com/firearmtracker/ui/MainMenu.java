package com.firearmtracker.ui;

import com.firearmtracker.service.InventoryManagementSystem;
import com.firearmtracker.model.User;
import com.firearmtracker.model.UserRole;
import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    private InventoryManagementSystem inventorySystem;
    private LoginScreen loginScreen;
    private User currentUser;

    public MainMenu(InventoryManagementSystem inventorySystem, LoginScreen loginScreen, User currentUser) {
        this.inventorySystem = inventorySystem;
        this.loginScreen = loginScreen;
        this.currentUser = currentUser;
        initUI();
    }

    private void initUI() {
        setTitle("Strategic Security Firearm Database (SSFD)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome message with user name
        JLabel titleLabel = new JLabel("Welcome, " + currentUser.getFullName() + "!");
        JLabel roleLabel = new JLabel(currentUser.getRole().getDisplayName() + " Access");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        roleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(titleLabel);
        mainPanel.add(roleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Add buttons based on role
        addButtons(mainPanel);

        add(mainPanel);
    }

    private void addButtons(JPanel panel) {
        // Basic buttons for all users
        JButton searchBySerialButton = createMenuButton("Lookup by Serial Number");
        JButton searchByEmployeeButton = createMenuButton("Lookup by Employee ID");

        panel.add(searchBySerialButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(searchByEmployeeButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Add role-specific buttons
        if (currentUser.getRole() != UserRole.EMPLOYEE) {
            JButton accessDatabaseButton = createMenuButton("Access Database");
            panel.add(accessDatabaseButton);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
            accessDatabaseButton.addActionListener(e -> openDatabasePanel());
        }

        // Admin-specific buttons
        if (currentUser.getRole() == UserRole.ADMIN) {
            JButton userManagementButton = createMenuButton("User Management");
            JButton systemLogsButton = createMenuButton("System Logs");

            panel.add(userManagementButton);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
            panel.add(systemLogsButton);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));

            userManagementButton.addActionListener(e -> openUserManagement());
            systemLogsButton.addActionListener(e -> openSystemLogs());
        }

        // Logout button (always last)
        JButton logoutButton = createMenuButton("Logout");
        panel.add(logoutButton);
        logoutButton.addActionListener(e -> logout());

        // Add action listeners for common buttons
        searchBySerialButton.addActionListener(e -> openSerialLookup());
        searchByEmployeeButton.addActionListener(e -> openEmployeeLookup());
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 50));  // Changed from 180, 40 to 250, 50
        button.setPreferredSize(new Dimension(250, 50));  // Added preferred size
        button.setFont(new Font("Arial", Font.BOLD, 14));  // Added font styling
        return button;
    }

    private void openUserManagement() {
        JFrame userManagementFrame = new JFrame("User Management");
        UserManagementPanel userManagementPanel = new UserManagementPanel(currentUser);
        userManagementFrame.add(userManagementPanel);
        userManagementFrame.setSize(800, 500);
        userManagementFrame.setLocationRelativeTo(null);
        userManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        userManagementFrame.setVisible(true);
    }

    private void openSystemLogs() {
        JFrame logsFrame = new JFrame("System Logs");
        SystemLogsPanel systemLogsPanel = new SystemLogsPanel(inventorySystem, currentUser);
        logsFrame.add(systemLogsPanel);
        logsFrame.setSize(800, 500);
        logsFrame.setLocationRelativeTo(null);
        logsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        logsFrame.setVisible(true);
    }

    private void openDatabasePanel() {
        JFrame databaseFrame = new JFrame("Database");
        DatabasePanel databasePanel = new DatabasePanel(inventorySystem, loginScreen);
        databaseFrame.add(databasePanel);
        databaseFrame.setSize(1000, 600);
        databaseFrame.setLocationRelativeTo(null);
        databaseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        databaseFrame.setVisible(true);
    }

    private void openSerialLookup() {
        String serialNumber = JOptionPane.showInputDialog(this, "Enter Firearm Serial Number:");
        if (serialNumber != null && !serialNumber.isBlank()) {
            com.firearmtracker.model.Firearm firearm = inventorySystem.getFirearmBySerial(serialNumber);
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

    private void openEmployeeLookup() {
        String employeeIdInput = JOptionPane.showInputDialog(this, "Enter Employee ID:");
        if (employeeIdInput != null && !employeeIdInput.isBlank()) {
            try {
                int employeeId = Integer.parseInt(employeeIdInput);
                com.firearmtracker.model.Employee employee = inventorySystem.getEmployeeById(employeeId);
                if (employee != null) {
                    com.firearmtracker.model.Firearm assignedFirearm = employee.getAssignedFirearm();
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

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Close all open windows
            for (Window window : Window.getWindows()) {
                window.dispose();
            }

            // Create and show new login screen
            SwingUtilities.invokeLater(() -> {
                LoginScreen newLoginScreen = new LoginScreen();
                newLoginScreen.setVisible(true);
            });
        }
    }
}