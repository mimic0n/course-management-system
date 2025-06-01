package com.coursemanagement.controller;


import com.coursemanagement.dao.UserDAO;
import com.coursemanagement.model.User;
import com.coursemanagement.util.SessionManager;
import com.coursemanagement.util.PasswordHasher;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.Random;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField regUsernameField;
    @FXML private TextField regEmailField;
    @FXML private PasswordField regPasswordField;
    @FXML private TextField regFullNameField;
    @FXML private TabPane tabPane;
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";


    private UserDAO userDAO ;
    public LoginController() {
        this.userDAO = new UserDAO();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String plainPassword = passwordField.getText().trim();

        if (username.isEmpty() || plainPassword.isEmpty()) {
            showAlert("Input Error", "Username and password cannot be empty.");
            return;
        }

        try {
            User user = userDAO.authenticate(username, plainPassword);
            if (user != null) {
                SessionManager.getInstance().setCurrentUser(user);
                if ("ADMIN".equals(user.getRole())) {
                    openAdminDashboard();
                } else {
                    openMainWindow();
                }
                showAlert("Login Successful", "Welcome " + user.getFullName() + "!");
            } else {
                showAlert("Login Failed", "Invalid username or password.");
            }
        } catch (Exception e) {
            showAlert("Login Error", "An error occurred during login: " + e.getMessage());
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
            showAlert("Input Error", "Please fill in all registration fields.");
            return;
        }
        if (!email.matches(EMAIL_REGEX)) {
            showAlert( " Input Error", "Email is not valid. Please enter a valid email address: user@example.com");
            return;
        }

        if (!password.matches(PASSWORD_REGEX)) {
            showAlert( "Input Error", "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, and one digit.");
            return;
        }

        if (username.contains(" ")) {
            showAlert( "Input Error", "Username cannot contain spaces.");
            return;
        }

        if (userDAO.findByUsername(username) != null) {
            showAlert("Registration Failed", "Username already exists. Please choose another one.");
            return;
        }
        if (userDAO.emailExists(email)) {
            showAlert("Registration Failed", "Email already registered. Please use another one or login.");
            return;
        }

        try {
            String hashedPassword = PasswordHasher.hashPassword(password);
            User newUser = new User(username, email, hashedPassword, fullName);

            if (userDAO.create(newUser)) {
                showAlert("Registration Successful", "Registration successful! Please login.");
                tabPane.getSelectionModel().select(0);
                clearRegistrationFields();
            } else {
                showAlert("Registration Failed", "Could not register user. Please try again or contact support.");
            }
        } catch (Exception e) {
            showAlert("Registration Error", "An error occurred during registration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void handleForgotPassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Forgot Password ?");
        dialog.setHeaderText("Enter your registered email address to reset your password.");
        dialog.setContentText("Email:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String email = result.get();
            User user = userDAO.findByEmail(email);

            if (user != null) {
                String newPassword = generateRandomPassword();
                user.setPassword(PasswordHasher.hashPassword(newPassword));

                if (userDAO.update(user)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(" Reset Password Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("Your password has been reset successfully. New password: " + newPassword + "\nPlease change it after logging in.");
                    alert.showAndWait();
                } else {
                    showError(" Password reset failed. Please try again later.");
                }
            } else {
                showError(" Email not found. Please check your email address or register if you don't have an account.");
            }
        }
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void openMainWindow() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.initData(SessionManager.getInstance().getCurrentUser());


            Scene scene = new Scene(root);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Course Management System ");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setResizable(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Application Error", "Could not open the main window: " + e.getMessage());
        }
    }

    private void openAdminDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboardView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);

            AdminDashboardController adminController = loader.getController();
            adminController.initData(SessionManager.getInstance().getCurrentUser());

            stage.setTitle("Admin Dashboard");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open admin dashboard: " + e.getMessage());
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
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}