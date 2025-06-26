// Trong AdminDashboardController.java
package com.coursemanagement.controller;

import com.coursemanagement.dao.CourseDAO;
import com.coursemanagement.dao.UserDAO;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class AdminDashboardController {

    @FXML
    private TableView<Course> courseTable;
    @FXML
    private TableColumn<Course, Integer> courseIdColumn;
    @FXML
    private TableColumn<Course, String> courseNameColumn;
    @FXML
    private TableColumn<Course, String> courseDescriptionColumn;
    @FXML
    private TableColumn<Course, Double> coursePriceColumn;
    @FXML
    private TableColumn<Course, String> courseLevelColumn;
    @FXML
    private TableColumn<Course, String> courseCategoryColumn;
    @FXML
    private TableColumn<Course, String> courseImageUrlColumn;


    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Integer> userIdColumn;
    @FXML
    private TableColumn<User, String> userNameColumn;
    @FXML
    private TableColumn<User, String> userRoleColumn;
    @FXML
    private TableColumn<User, String> userEmailColumn;
    @FXML
    private TableColumn<User, String> userFullNameColumn;


    private CourseDAO courseDAO = new CourseDAO();
    private UserDAO userDAO = new UserDAO();

    public void initData(User currentUser) {
        if (currentUser != null && "ADMIN".equals(currentUser.getRole())) {
            // Set window title with admin name
//            Stage stage = (Stage) courseTable.getScene().getWindow();
//            stage.setTitle("Admin Dashboard - " + currentUser.getFullName());
            // Load data
            try {
                loadCourses();
                loadUsers();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Loading Error",
                        "Failed to load data: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Show error and close window if non-admin tries to access
            showAlert(Alert.AlertType.ERROR, "Access Denied",
                    "You do not have permission to access this area.");
            Stage stage = (Stage) courseTable.getScene().getWindow();
            stage.close();
        }
    }



    @FXML
    public void initialize() {
        System.out.println("Initializing AdminDashboardController...");
        if (courseLevelColumn == null) {
            System.err.println("FATAL: courseLevelColumn is null in initialize()!");
            // You can print other @FXML fields too to see which ones are null
        }
        if (courseTable == null) {
            System.err.println("FATAL: courseTable is null in initialize()!");
        }

        // Course Table initialization
        courseIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        courseNameColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        courseDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        coursePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        courseLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        courseCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        courseImageUrlColumn.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

        loadCourses();

        // User Table initialization
        userIdColumn.setCellValueFactory(cellData -> cellData.getValue().userIdProperty().asObject());
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userFullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Load data
        loadUsers();

    }

    private void loadCourses() {
        List<Course> courses = courseDAO.findAll();
        ObservableList<Course> observableCourses = FXCollections.observableArrayList(courses);
        courseTable.setItems(observableCourses);
    }

    private void loadUsers() {
        List<User> users = userDAO.findAll();
        ObservableList<User> observableUsers = FXCollections.observableArrayList(users);
        userTable.setItems(observableUsers);
    }

    @FXML
    private void handleAddCourse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddCourseDialog.fxml"));
            Parent root = loader.load();
            AddCourseDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Thêm khóa học mới");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            // dialogStage.initOwner(((Node)event.getSource()).getScene().getWindow()); // Đặt cửa sổ cha
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            if (controller.isCourseAdded()) {
                loadCourses(); // Tải lại danh sách khóa học sau khi thêm
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý lỗi
        }
    }

    @FXML
    private void handleEditCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditCourseDialog.fxml"));
                Parent root = loader.load();
                EditCourseDialogController controller = loader.getController();
                controller.setCourseToEdit(selectedCourse); // Truyền khóa học để chỉnh sửa

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Chỉnh sửa khóa học");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.setScene(new Scene(root));
                dialogStage.showAndWait();

                if (controller.isCourseUpdated()) {
                    loadCourses(); // Tải lại danh sách khóa học sau khi sửa
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Hiển thị thông báo yêu cầu chọn khóa học
        }
    }

    @FXML
    private void handleDeleteCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText("Xóa khóa học");
            alert.setContentText("Bạn có chắc chắn muốn xóa khóa học này?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (courseDAO.delete(selectedCourse.getId())) {
                    loadCourses();
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa khóa học thành công!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa khóa học!");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn khóa học cần xóa!");
        }
    }

    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddUserDialog.fxml"));
            Parent root = loader.load();
            AddUserDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Thêm người dùng mới");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            if (controller.isUserAdded()) {
                loadUsers();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở form thêm người dùng!");
        }
    }

    @FXML
    private void handleEditUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditUserDialog.fxml"));
                Parent root = loader.load();
                EditUserDialogController controller = loader.getController();
                controller.setUser(selectedUser);

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Chỉnh sửa người dùng");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.setScene(new Scene(root));
                dialogStage.showAndWait();

                if (controller.isUserUpdated()) {
                    loadUsers();
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở form chỉnh sửa!");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn người dùng cần sửa!");
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText("Xóa người dùng");
            alert.setContentText("Bạn có chắc chắn muốn xóa người dùng này?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (userDAO.delete(selectedUser.getUserId())) {
                    loadUsers();
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa người dùng thành công!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa người dùng!");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn người dùng cần xóa!");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }



    public void setCourseLevelColumn(TableColumn<Course, String> courseLevelColumn) {
        this.courseLevelColumn = courseLevelColumn;
    }
}