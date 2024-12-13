package com.firearmtracker;

import com.firearmtracker.model.User;
import com.firearmtracker.service.InventoryManagementSystem;
import com.firearmtracker.ui.LoginScreen;
import com.firearmtracker.ui.MainMenu;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Create inventory system and login screen
            InventoryManagementSystem inventorySystem = new InventoryManagementSystem();
            LoginScreen loginScreen = new LoginScreen(inventorySystem);

            // Create and display login screen
            loginScreen.setVisible(true);

            // Create a thread to monitor login status
            Thread loginMonitor = new Thread(() -> {
                while (loginScreen.isVisible()) {
                    try {
                        Thread.sleep(100); // Wait to avoid high CPU usage
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Once login screen is closed, check if login was successful
                if (loginScreen.isLoginSuccessful()) {
                    SwingUtilities.invokeLater(() -> {
                        User currentUser = loginScreen.getCurrentUser();
                        // Create and display the Main Menu
                        MainMenu mainMenu = new MainMenu(inventorySystem, loginScreen, currentUser);
                        mainMenu.setVisible(true);
                    });
                } else {
                    JOptionPane.showMessageDialog(null, "Login Failed. Exiting.");
                    System.exit(0); // Close the application if login fails
                }
            });

            // Start the login monitoring thread
            loginMonitor.start();
        });
    }
}