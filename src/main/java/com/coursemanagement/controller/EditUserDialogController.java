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

public class EditUserDialogController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField; // Để trống nếu không thay đổi
    @FXML private TextField emailField;
    @FXML private TextField fullNameField;
    @FXML private ComboBox<String> roleComboBox;

    private Stage dialogStage;
    private User user; // Người dùng hiện tại đang được chỉnh sửa
    private UserDAO userDAO = new UserDAO();
    private boolean userUpdated = false;

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList("student", "teacher", "admin"));
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setUser(User user) {
        this.user = user;
        // Điền dữ liệu của người dùng vào các trường
        usernameField.setText(user.getUsername());
        emailField.setText(user.getEmail());
        fullNameField.setText(user.getFullName());
        roleComboBox.getSelectionModel().select(user.getRole());
    }

    public boolean isUserUpdated() {
        return userUpdated;
    }

    @FXML
    private void handleSaveUser() {
        String username = usernameField.getText();
        String password = passwordField.getText(); // Mật khẩu mới, có thể trống
        String email = emailField.getText();
        String fullName = fullNameField.getText();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() || role == null) {
            showAlert(Alert.AlertType.WARNING, "Lỗi Nhập liệu", "Vui lòng điền đầy đủ các trường bắt buộc.");
            return;
        }
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert(Alert.AlertType.WARNING, "Lỗi Email", "Định dạng email không hợp lệ.");
            return;
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(role);

        // Chỉ cập nhật mật khẩu nếu người dùng nhập mật khẩu mới
        if (!password.isEmpty()) {
            user.setPassword(password); // Mật khẩu này sẽ được băm trong DAO
        } else {
            user.setPassword(null); // Đặt là null để DAO biết không cập nhật mật khẩu
        }


        if (userDAO.update(user)) {
            userUpdated = true;
            dialogStage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật người dùng. Tên đăng nhập có thể đã tồn tại.");
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