package com.firearmtracker.service;

import com.firearmtracker.model.User;
import com.firearmtracker.model.UserRole;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static UserManager instance;
    private Map<String, User> users;

    private UserManager() {
        users = new HashMap<>();

        // Add preset user accounts
        addUser(new User("employee", "employee", UserRole.EMPLOYEE, "SSC", "Employee"));
        addUser(new User("operations", "operations", UserRole.OPERATIONS, "Ops", "Officer"));
        addUser(new User("admin", "admin", UserRole.ADMIN, "Steven", "Kurt"));
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public User authenticateUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public void resetPassword(String username, String newPassword) {
        User user = users.get(username);
        if (user != null) {
            user.setPassword(newPassword);
        }
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public User getUserByUsername(String username) {
        return users.get(username);
    }

    public void setUserRole(String username, UserRole role) {
        User user = users.get(username);
        if (user != null) {
            user.setRole(role);
        }
    }

    public void setUserStatus(String username, boolean active) {
        User user = users.get(username);
        if (user != null) {
            user.setActive(active);
        }
    }

    public Map<String, User> getUsers() {
        return new HashMap<>(users);
    }
}