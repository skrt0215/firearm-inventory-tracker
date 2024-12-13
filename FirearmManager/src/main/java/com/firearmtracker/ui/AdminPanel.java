package com.firearmtracker.ui;

import com.firearmtracker.model.User;
import com.firearmtracker.model.UserRole;
import com.firearmtracker.service.UserManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JFrame {
    private JTable userTable;
    private DefaultTableModel userModel;
    private UserManager userManager;
    private List<User> users;

    public AdminPanel(List<User> users) {
        this.users = users;
        this.userManager = UserManager.getInstance();
        initUI();
    }

    private void initUI() {
        setTitle("Admin Control Panel");
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create table
        String[] columns = {"Username", "Full Name", "Account Type", "Status"};
        userModel = new DefaultTableModel(columns, 0);
        userTable = new JTable(userModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addButton(buttonPanel, "Add User", e -> addUser());
        addButton(buttonPanel, "Remove User", e -> removeUser());
        addButton(buttonPanel, "Reset Password", e -> resetPassword());
        addButton(buttonPanel, "Change Account Type", e -> changeAccountType());
        addButton(buttonPanel, "Toggle Account Status", e -> toggleUserStatus());

        mainPanel.add(buttonPanel, BorderLayout.EAST);

        add(mainPanel);
        updateUserTable();
    }

    private void addButton(JPanel panel, String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        panel.add(button);
    }

    private void addUser() {
        JPanel panel = createUserFormPanel();
        int result = JOptionPane.showConfirmDialog(this, panel, "Add User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            // Process form data and add user
            String username = ((JTextField) panel.getComponent(1)).getText().trim();
            String password = new String(((JPasswordField) panel.getComponent(3)).getPassword());
            String firstName = ((JTextField) panel.getComponent(5)).getText().trim();
            String lastName = ((JTextField) panel.getComponent(7)).getText().trim();
            String accountType = (String) ((JComboBox<?>) panel.getComponent(9)).getSelectedItem();

            User newUser = new User(username, password, UserRole.fromString(accountType), firstName, lastName);
            users.add(newUser);
            updateUserTable();
            JOptionPane.showMessageDialog(this, "User added successfully");
        }
    }

    private JPanel createUserFormPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        String[] roles = {"Employee", "Operations", "Admin"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Role:"));
        panel.add(roleBox);
        return panel;
    }

    private void removeUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) userTable.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove user: " + username + "?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                users.removeIf(user -> user.getUsername().equals(username));
                updateUserTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to remove");
        }
    }

    private void resetPassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) userTable.getValueAt(selectedRow, 0);
            JPasswordField passwordField = new JPasswordField();

            int result = JOptionPane.showConfirmDialog(this,
                    passwordField,
                    "Enter new password for " + username,
                    JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String newPassword = new String(passwordField.getPassword());
                userManager.resetPassword(username, newPassword);
                JOptionPane.showMessageDialog(this, "Password reset successful");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user");
        }
    }

    private void changeAccountType() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) userTable.getValueAt(selectedRow, 0);
            String[] types = {"Employee", "Operations", "Admin"};
            JComboBox<String> typeBox = new JComboBox<>(types);

            int result = JOptionPane.showConfirmDialog(this,
                    typeBox,
                    "Select new account type for " + username,
                    JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String newType = (String) typeBox.getSelectedItem();
                users.stream()
                        .filter(user -> user.getUsername().equals(username))
                        .findFirst()
                        .ifPresent(user -> user.setRole(UserRole.fromString(newType)));
                updateUserTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user");
        }
    }

    private void toggleUserStatus() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) userTable.getValueAt(selectedRow, 0);
            users.stream()
                    .filter(user -> user.getUsername().equals(username))
                    .findFirst()
                    .ifPresent(user -> {
                        user.setActive(!user.isActive());
                        updateUserTable();
                    });
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user");
        }
    }

    private void updateUserTable() {
        userModel.setRowCount(0);
        for (User user : users) {
            userModel.addRow(new Object[]{
                    user.getUsername(),
                    user.getFullName(),
                    user.getRole().getDisplayName(),
                    user.isActive() ? "Active" : "Inactive"
            });
        }
    }
}