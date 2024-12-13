package com.firearmtracker.model;

public enum UserRole {
    EMPLOYEE("Employee", new String[]{
            "VIEW_INVENTORY",
            "LOOKUP_SERIAL",
            "LOOKUP_EMPLOYEE"
    }),
    OPERATIONS("Operations", new String[]{
            "VIEW_INVENTORY",
            "LOOKUP_SERIAL",
            "LOOKUP_EMPLOYEE",
            "MANAGE_EMPLOYEES",
            "MANAGE_FIREARMS",
            "ISSUE_FIREARM",
            "RETURN_FIREARM",
            "UPDATE_LOCATION",
            "MANAGE_USERS"
    }),
    ADMIN("Admin", new String[]{
            "VIEW_INVENTORY",
            "LOOKUP_SERIAL",
            "LOOKUP_EMPLOYEE",
            "MANAGE_EMPLOYEES",
            "MANAGE_FIREARMS",
            "ISSUE_FIREARM",
            "RETURN_FIREARM",
            "UPDATE_LOCATION",
            "MANAGE_USERS",
            "MANAGE_ADMINS",
            "VIEW_LOGS",
            "SYSTEM_CONFIG"
    });

    private final String displayName;
    private final String[] permissions;

    UserRole(String displayName, String[] permissions) {
        this.displayName = displayName;
        this.permissions = permissions;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean hasPermission(String permission) {
        for (String p : permissions) {
            if (p.equals(permission)) return true;
        }
        return false;
    }

    public static UserRole fromString(String text) {
        for (UserRole role : UserRole.values()) {
            if (role.displayName.equalsIgnoreCase(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No role found for: " + text);
    }
}