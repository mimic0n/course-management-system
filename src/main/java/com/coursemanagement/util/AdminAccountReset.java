package com.coursemanagement.util;

import com.coursemanagement.dao.UserDAO;
import com.coursemanagement.model.User;
import javafx.scene.control.Alert;

public class AdminAccountReset {
    public static void createNewAdminAccount() {
        UserDAO userDAO = new UserDAO();

        // Create new admin user
        User adminUser = new User(
                "admin",           // username
                "admin123",       // password
                "admin@admin.com", // email
                "Administrator",   // fullName
                "ADMIN"           // role
        );

        try {
            // First try to delete existing admin if exists
            userDAO.deleteByUsername("admin");

            // Insert new admin
            boolean success = userDAO.save(adminUser);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION,
                        "Success",
                        "New admin account created successfully!\nUsername: admin\nPassword: admin123");
            } else {
                showAlert(Alert.AlertType.ERROR,
                        "Error",
                        "Failed to create admin account");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,
                    "Error",
                    "Error creating admin account: " + e.getMessage());
        }
    }

    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}