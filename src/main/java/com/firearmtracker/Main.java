package com.firearmtracker;

import com.firearmtracker.model.Employee;
import com.firearmtracker.model.Firearm;
import com.firearmtracker.service.InventoryManagementSystem;
import com.firearmtracker.ui.FirearmTrackerGUI;

public class Main {
    public static void main(String[] args) {
        InventoryManagementSystem inventorySystem = new InventoryManagementSystem();

        // Predefined Firearms
        Firearm glock1 = new Firearm("G12345", "Glock");
        Firearm delta1 = new Firearm("D67890", "Delta");
        inventorySystem.addFirearm(glock1);
        inventorySystem.addFirearm(delta1);

        // Predefined Employees
        Employee john = new Employee(1, "Steven", "Kurt", "Ops");
        Employee sarah = new Employee(2, "Kevin", "Waters", "Mecklenberg");
        inventorySystem.addEmployee(john);
        inventorySystem.addEmployee(sarah);

        // Launch GUI
        new FirearmTrackerGUI(inventorySystem);
    }
}

