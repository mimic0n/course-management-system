package com.coursemanagement.controller;


import com.coursemanagement.model.User;
import com.coursemanagement.util.SessionManager;
import com.coursemanagement.model.DatabaseConnection;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField regUsernameField;
    @FXML private TextField regEmailField;
    @FXML private PasswordField regPasswordField;
    @FXML private TextField regFullNameField;
    @FXML private TabPane tabPane;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        User user = DatabaseConnection.authenticateUser(username, password);
        if (user != null) {
            SessionManager.getInstance().setCurrentUser(user);
            openMainWindow();
        } else {
            showAlert("Error", "Invalid username or password");
        }
    }

    @FXML
    private void handleRegister() {
        String username = regUsernameField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = regPasswordField.getText();
        String fullName = regFullNameField.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        User user = new User(username, email, password, fullName);
        if (DatabaseConnection.registerUser(user)) {
            showAlert("Success", "Registration successful! Please login.");
            tabPane.getSelectionModel().select(0); // Switch to login tab
            clearRegistrationFields();
        } else {
            showAlert("Error", "Registration failed. Username or email might already exist.");
        }
    }

    private void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearRegistrationFields() {
        regUsernameField.clear();
        regEmailField.clear();
        regPasswordField.clear();
        regFullNameField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}