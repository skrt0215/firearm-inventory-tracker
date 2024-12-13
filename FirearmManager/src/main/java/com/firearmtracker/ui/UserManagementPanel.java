package com.firearmtracker.ui;

import com.firearmtracker.model.User;
import com.firearmtracker.model.UserRole;
import com.firearmtracker.service.UserManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class UserManagementPanel extends JPanel {
    private JTable userTable;
    private DefaultTableModel userModel;
    private UserManager userManager;
    private User currentUser;

    public UserManagementPanel(User currentUser) {
        this.currentUser = currentUser;
        this.userManager = UserManager.getInstance();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table
        String[] columns = {"Username", "Name", "Role", "Status"};
        userModel = new DefaultTableModel(columns, 0);
        userTable = new JTable(userModel);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setPreferredSize(new Dimension(150, getHeight()));

        addButton(buttonPanel, "Add User", e -> addUser());
        addButton(buttonPanel, "Edit User", e -> editUser());
        addButton(buttonPanel, "Reset Password", e -> resetPassword());
        addButton(buttonPanel, "Toggle Status", e -> toggleUserStatus());

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);

        updateUserTable();
    }

    private void addButton(JPanel panel, String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(140, 40));
        button.addActionListener(listener);
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void updateUserTable() {
        userModel.setRowCount(0);
        for (User user : userManager.getUsers().values()) {
            userModel.addRow(new Object[]{
                    user.getUsername(),
                    user.getFirstName() + " " + user.getLastName(),
                    user.getRole().getDisplayName(),
                    user.isActive() ? "Active" : "Inactive"
            });
        }
    }

    private void addUser() {
        JPanel panel = createUserFormPanel();
        int result = JOptionPane.showConfirmDialog(this, panel, "Add User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Process form data and add user
            // Implementation details...
            updateUserTable();
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

    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to edit",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) userTable.getValueAt(selectedRow, 0);
        User user = userManager.getUserByUsername(username);

        if (user != null) {
            JPanel panel = createUserFormPanel();
            // Pre-fill the fields
            ((JTextField) panel.getComponent(1)).setText(user.getUsername());
            ((JTextField) panel.getComponent(5)).setText(user.getFirstName());
            ((JTextField) panel.getComponent(7)).setText(user.getLastName());
            ((JComboBox<?>) panel.getComponent(9)).setSelectedItem(user.getRole().getDisplayName());

            int result = JOptionPane.showConfirmDialog(this, panel,
                    "Edit User", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                updateUserFromPanel(user, panel);
                updateUserTable();
            }
        }
    }

    private void updateUserFromPanel(User user, JPanel panel) {
        user.setUsername(((JTextField) panel.getComponent(1)).getText());
        user.setFirstName(((JTextField) panel.getComponent(5)).getText());
        user.setLastName(((JTextField) panel.getComponent(7)).getText());
        String roleStr = (String) ((JComboBox<?>) panel.getComponent(9)).getSelectedItem();
        user.setRole(UserRole.fromString(roleStr));
    }

    private void resetPassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) userTable.getValueAt(selectedRow, 0);
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Reset Password", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.equals(confirmPassword)) {
                userManager.resetPassword(username, newPassword);
                JOptionPane.showMessageDialog(this,
                        "Password reset successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Passwords do not match",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleUserStatus() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) userTable.getValueAt(selectedRow, 0);
        User user = userManager.getUserByUsername(username);

        if (user != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to " + (user.isActive() ? "deactivate" : "activate") +
                            " user: " + username + "?",
                    "Confirm Status Change",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                user.setActive(!user.isActive());
                updateUserTable();
            }
        }
    }

    private void searchUsers() {
        String searchTerm = JOptionPane.showInputDialog(this,
                "Enter username or name to search:");
        if (searchTerm != null && !searchTerm.isEmpty()) {
            userModel.setRowCount(0);
            for (User user : userManager.getUsers().values()) {
                if (user.getUsername().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        user.getFirstName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        user.getLastName().toLowerCase().contains(searchTerm.toLowerCase())) {
                    userModel.addRow(new Object[]{
                            user.getUsername(),
                            user.getFirstName() + " " + user.getLastName(),
                            user.getRole().getDisplayName(),
                            user.isActive() ? "Active" : "Inactive"
                    });
                }
            }
        }
    }
}