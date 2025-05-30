package com.coursemanagement.controller;


import com.coursemanagement.dao.UserDAO;
import com.coursemanagement.model.User;
import com.coursemanagement.util.SessionManager;
import com.coursemanagement.model.DatabaseConnection;
import com.coursemanagement.util.PasswordHasher;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;

import javafx.scene.Parent;
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

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String plainPassword = passwordField.getText();

        try {
            User user = userDAO.authenticate(username, plainPassword);
            if (user != null) {
                SessionManager.getInstance().setCurrentUser(user);
                openMainWindow();
            } else {
                showAlert("Error", "Invalid username or password");
            }
        } catch (RuntimeException e) {
            showAlert("Error", "Login failed: " + e.getMessage());
            e.printStackTrace();
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

        // Hash the password before creating the user
        String hashedPassword = PasswordHasher.hashPassword(password);
        User user = new User(username, email, hashedPassword, fullName);

        if (DatabaseConnection.registerUser(user)) {
            showAlert("Success", "Registration successful! Please login.");
            tabPane.getSelectionModel().select(0);
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