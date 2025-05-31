package com.coursemanagement.controller;


import com.coursemanagement.dao.UserDAO;
import com.coursemanagement.model.User;
import com.coursemanagement.util.SessionManager;
import com.coursemanagement.util.PasswordHasher;
import com.coursemanagement.dao.CourseDAO;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    @FXML private TextField emailField;
    @FXML private Hyperlink forgotPasswordLink;

    private UserDAO userDAO ;
    public LoginController() {
        this.userDAO = new UserDAO(); // Khởi tạo UserDAO ở constructor
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
                SessionManager.getInstance().setCurrentUser(user); // User object đã được điền đủ thông tin từ UserDAO

                if ("ADMIN".equals(user.getRole())) {
                    openAdminDashboard();
                } else {
                    openMainWindow();
                }
                showAlert("Login Successful", "Welcome " + user.getFullName() + "!");
            } else {
                showAlert("Login Failed", "Invalid username or password.");
            }
        } catch (Exception e) { // Bắt Exception chung hơn nếu UserDAO có thể throw RuntimeException
            showAlert("Login Error", "An error occurred during login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        String username = regUsernameField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = regPasswordField.getText().trim(); // Mật khẩu thuần
        String fullName = regFullNameField.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            showAlert("Input Error", "Please fill in all registration fields.");
            return;
        }

        // Kiểm tra xem username hoặc email đã tồn tại chưa (tùy chọn, nhưng nên có)
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
            User newUser = new User(username, email, hashedPassword, fullName); // Constructor này đặt role="USER"

            if (userDAO.create(newUser)) { // Gọi UserDAO.create
                showAlert("Registration Successful", "Registration successful! Please login.");
                tabPane.getSelectionModel().select(0); // Chuyển về tab login
                clearRegistrationFields();
            } else {
                showAlert("Registration Failed", "Could not register user. Please try again or contact support.");
            }
        } catch (Exception e) { // Bắt Exception chung nếu có lỗi không mong muốn
            showAlert("Registration Error", "An error occurred during registration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void handleForgotPassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Quên mật khẩu");
        dialog.setHeaderText("Nhập email của bạn");
        dialog.setContentText("Email:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String email = result.get();
            User user = userDAO.findByEmail(email);

            if (user != null) {
                // Generate new random password
                String newPassword = generateRandomPassword();
                user.setPassword(PasswordHasher.hashPassword(newPassword));

                if (userDAO.update(user)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Khôi phục mật khẩu");
                    alert.setHeaderText(null);
                    alert.setContentText("Mật khẩu mới của bạn là: " + newPassword +
                            "\nVui lòng đổi mật khẩu sau khi đăng nhập!");
                    alert.showAndWait();
                } else {
                    showError("Không thể cập nhật mật khẩu mới");
                }
            } else {
                showError("Email không tồn tại trong hệ thống");
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
            Stage stage = (Stage) usernameField.getScene().getWindow(); // Lấy stage hiện tại

            // Thiết lập cho cửa sổ chính
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


            // Get the controller and pass necessary data
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Hoặc ERROR, WARNING tùy ngữ cảnh
        alert.setTitle(title);
        alert.setHeaderText(null); // Bỏ header nếu không cần
        alert.setContentText(message);
        alert.showAndWait();
    }

}