package com.coursemanagement.controller;

import com.coursemanagement.dao.UserDAO;
import com.coursemanagement.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddUserDialogController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private TextField fullNameField;
    @FXML private ComboBox<String> roleComboBox;
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";


    private Stage dialogStage;
    private UserDAO userDAO = new UserDAO();
    private boolean userAdded = false;

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList("USER" , "ADMIN"));
        roleComboBox.getSelectionModel().select("USER");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isUserAdded() {
        return userAdded;
    }

    @FXML
    private void handleSaveUser() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || fullName.isEmpty() || role == null) {
            showAlert(Alert.AlertType.WARNING, "Error Adding User", "Please fill in all fields.");
            return;
        }

        if (!email.matches(EMAIL_REGEX)) {
            showAlert(Alert.AlertType.WARNING, "Error Adding User Email", "Email is not valid. Please enter a valid email address : user@example.com .");
            return;
        }

        if (!password.matches(PASSWORD_REGEX)) {
            showAlert(Alert.AlertType.WARNING, "Error Adding Password", "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, and one digit.");
            return;
        }

        if (username.contains(" ")) {
            showAlert(Alert.AlertType.WARNING, "Error Adding Username", "Username cannot contain spaces.");
            return;
        }

        User newUser = new User(username, password, email, fullName, role);
        if (userDAO.create(newUser)) {
            userAdded = true;
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "User Already Exists", "User with the same username already exists.");
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}