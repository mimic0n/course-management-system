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

public class AdminDashboardController {

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, Integer> courseIdColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> courseDescriptionColumn;
    @FXML private TableColumn<Course, Double> coursePriceColumn;
    @FXML private TableColumn<Course, String> courseLevelColumn;
    @FXML private TableColumn<Course, String> courseCategoryColumn;
    @FXML private TableColumn<Course, String> courseImageUrlColumn;
    // ... thêm các cột khác cho khóa học

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> userIdColumn;
    @FXML private TableColumn<User, String> userNameColumn;
    @FXML private TableColumn<User, String> userRoleColumn;
    @FXML private TableColumn<User, String> userEmailColumn;
    @FXML private TableColumn<User, String> userFullNameColumn;
    // ... thêm các cột khác cho người dùng

    private CourseDAO courseDAO = new CourseDAO();
    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/coursemanagement/views/AddCourseDialog.fxml"));
            Parent root = loader.load();
            CourseController controller = loader.getController();

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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/coursemanagement/views/EditCourseDialog.fxml"));
                Parent root = loader.load();
                CourseController controller = loader.getController();
                controller.setCourse(selectedCourse); // Truyền khóa học để chỉnh sửa

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
            // Xác nhận xóa
            if (courseDAO.delete(selectedCourse.getId())) {
                loadCourses(); // Tải lại danh sách
            } else {
                // Xử lý lỗi xóa
            }
        } else {
            // Hiển thị thông báo yêu cầu chọn khóa học
        }
    }

    // Tương tự cho Add/Edit/Delete User
    @FXML private void handleAddUser() { /* ... */ }
    @FXML private void handleEditUser() { /* ... */ }
    @FXML private void handleDeleteUser() { /* ... */ }
}