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

    private Stage dialogStage;
    private UserDAO userDAO = new UserDAO();
    private boolean userAdded = false;

    @FXML
    public void initialize() {
        // Cấu hình ComboBox vai trò
        roleComboBox.setItems(FXCollections.observableArrayList("student", "teacher", "admin"));
        roleComboBox.getSelectionModel().select("student"); // Mặc định là student
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isUserAdded() {
        return userAdded;
    }

    @FXML
    private void handleSaveUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String fullName = fullNameField.getText();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || fullName.isEmpty() || role == null) {
            showAlert(Alert.AlertType.WARNING, "Lỗi Nhập liệu", "Vui lòng điền đầy đủ tất cả các trường.");
            return;
        }
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) { // Simple email regex
            showAlert(Alert.AlertType.WARNING, "Lỗi Email", "Định dạng email không hợp lệ.");
            return;
        }

        User newUser = new User(username, password, email, fullName, role);
        if (userDAO.create(newUser)) {
            userAdded = true;
            dialogStage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm người dùng. Tên đăng nhập có thể đã tồn tại.");
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