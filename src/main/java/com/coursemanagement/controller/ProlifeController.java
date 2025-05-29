package com.coursemanagement.controller;
import com.coursemanagement.model.DatabaseConnection;
import com.coursemanagement.model.User;
import com.coursemanagement.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ProlifeController implements Initializable {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField fullNameField;

    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = SessionManager.getInstance().getCurrentUser();
        loadUserData();
    }

    private void loadUserData() {
        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
            emailField.setText(currentUser.getEmail());
            fullNameField.setText(currentUser.getFullName());
            usernameField.setEditable(false); // Username shouldn't be editable
        }
    }

    @FXML
    private void saveProfile() {
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();

        if (email.isEmpty() || fullName.isEmpty()) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        currentUser.setEmail(email);
        currentUser.setFullName(fullName);

        if (DatabaseConnection.updateUser(currentUser)) {
            showAlert("Success", "Profile updated successfully!");
        } else {
            showAlert("Error", "Failed to update profile");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}