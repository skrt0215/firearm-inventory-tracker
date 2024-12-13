package com.firearmtracker.ui;

import com.firearmtracker.model.User;
import com.firearmtracker.service.InventoryManagementSystem;
import com.firearmtracker.service.UserManager;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean loginSuccessful = false;
    private User currentUser = null;
    private UserManager userManager;
    private InventoryManagementSystem inventorySystem;

    public LoginScreen() {
        this(InventoryManagementSystem.getInstance());
    }

    public LoginScreen(InventoryManagementSystem inventorySystem) {
        this.inventorySystem = inventorySystem;
        this.userManager = UserManager.getInstance();
        initUI();
    }

    private void initUI() {
        setTitle("SSFD Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 300));
        setResizable(false);

        ImageIcon backgroundImageIcon = new ImageIcon("/Users/Steven/Desktop/DevelopmentMain/FirearmManager/SSCLogo.jpeg");
        Image backgroundImage = backgroundImageIcon.getImage();
        Image scaledBackgroundImage = backgroundImage.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
        ImageIcon scaledBackgroundImageIcon = new ImageIcon(scaledBackgroundImage);

        JLabel backgroundLabel = new JLabel(scaledBackgroundImageIcon);
        backgroundLabel.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Strategic Security Firearm Database");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton employeeButton = createStyledButton("Employee Login");
        JButton operationsButton = createStyledButton("Operations Login");
        JButton adminButton = createStyledButton("Admin Login");

        employeeButton.addActionListener(e -> showLoginDialog("Employee"));
        operationsButton.addActionListener(e -> showLoginDialog("Operations"));
        adminButton.addActionListener(e -> showLoginDialog("Admin"));

        contentPanel.add(employeeButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(operationsButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(adminButton);

        backgroundLabel.add(contentPanel, BorderLayout.CENTER);

        add(backgroundLabel);
        pack();
        setLocationRelativeTo(null);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private void showLoginDialog(String type) {
        JDialog dialog = new JDialog(this, "Login as " + type, true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> attemptLogin(type, dialog));

        panel.add(new JLabel());
        panel.add(loginButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void attemptLogin(String type, JDialog dialog) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Use UserManager to authenticate
        User user = userManager.authenticateUser(username, password);
        if (user != null && user.getRole().getDisplayName().equals(type)) {
            loginSuccessful = true;
            currentUser = user;
            dialog.dispose();
            this.dispose();

            SwingUtilities.invokeLater(() -> {
                // Add initial firearms and employees
                inventorySystem.addFirearm("G12345", "Glock");
                inventorySystem.addFirearm("D67890", "Delta");
                inventorySystem.addEmployee("Steven", "Kurt", "SSC");
                inventorySystem.addEmployee("David", "Wright", "SSC");

                MainMenu mainMenu = new MainMenu(inventorySystem, this, user);
                mainMenu.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(dialog,
                    "Invalid username or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            usernameField.requestFocus();
        }
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}