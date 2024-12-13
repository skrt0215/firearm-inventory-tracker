package com.firearmtracker.ui;

import com.firearmtracker.model.User;
import com.firearmtracker.service.InventoryManagementSystem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SystemLogsPanel extends JPanel {
    private JTable logsTable;
    private DefaultTableModel logsTableModel;
    private List<SystemLog> logs;
    private InventoryManagementSystem inventorySystem;
    private User currentUser;

    public SystemLogsPanel(InventoryManagementSystem inventorySystem, User currentUser) {
        this.inventorySystem = inventorySystem;
        this.currentUser = currentUser;
        logs = new ArrayList<>();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table
        String[] columns = {"Timestamp", "User", "Action", "Details"};
        logsTableModel = new DefaultTableModel(columns, 0);
        logsTable = new JTable(logsTableModel);
        JScrollPane scrollPane = new JScrollPane(logsTable);

        // Create control panel
        JPanel controlPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        JButton exportButton = new JButton("Export Logs");
        String[] filters = {"All", "Login/Logout", "Database Changes", "User Management"};
        JComboBox<String> filterBox = new JComboBox<>(filters);

        controlPanel.add(refreshButton);
        controlPanel.add(exportButton);
        controlPanel.add(new JLabel("Filter:"));
        controlPanel.add(filterBox);

        refreshButton.addActionListener(e -> refreshLogs());
        exportButton.addActionListener(e -> exportLogs());
        filterBox.addActionListener(e -> filterLogs((String) filterBox.getSelectedItem()));

        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        refreshLogs();
    }

    private void refreshLogs() {
        logs.clear();
        logs.add(new SystemLog(currentUser.getUsername(), "Logged in", ""));
        updateLogsTable();
    }

    private void exportLogs() {
        // Implementation for exporting logs
        JOptionPane.showMessageDialog(this, "Exporting logs to a file...");
    }

    private void filterLogs(String filter) {
        logsTableModel.setRowCount(0);
        for (SystemLog log : logs) {
            if (filter.equalsIgnoreCase("All") || isLogMatchingFilter(log, filter)) {
                logsTableModel.addRow(new Object[]{
                        log.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        log.username,
                        log.action,
                        log.details
                });
            }
        }
    }

    private boolean isLogMatchingFilter(SystemLog log, String filter) {
        return (filter.equalsIgnoreCase("Login/Logout") && (log.action.startsWith("Logged") || log.action.startsWith("Logout"))) ||
                (filter.equalsIgnoreCase("Database Changes") && (log.action.contains("Firearm") || log.action.contains("Employee"))) ||
                (filter.equalsIgnoreCase("User Management") && log.action.contains("User"));
    }

    private void updateLogsTable() {
        logsTableModel.setRowCount(0);
        for (SystemLog log : logs) {
            logsTableModel.addRow(new Object[]{
                    log.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    log.username,
                    log.action,
                    log.details
            });
        }
    }

    // Inner class for log entries
    private static class SystemLog {
        LocalDateTime timestamp;
        String username;
        String action;
        String details;

        SystemLog(String username, String action, String details) {
            this.timestamp = LocalDateTime.now();
            this.username = username;
            this.action = action;
            this.details = details;
        }
    }
}