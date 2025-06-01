// Trong AdminDashboardController.java
package com.coursemanagement.controller;

import com.coursemanagement.dao.CourseDAO;
import com.coursemanagement.dao.UserDAO;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.util.DatabaseMaintenance;
import com.coursemanagement.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class AdminDashboardController {
    @FXML
    private Label welcomeLabel;
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

    @FXML
    private VBox enrollmentManagementView;
    @FXML
    private EnrollmentManagementController enrollmentManagementViewController;


    private CourseDAO courseDAO = new CourseDAO();
    private UserDAO userDAO = new UserDAO();

    public void initData(User currentUser) {
        if (currentUser != null && "ADMIN".equals(currentUser.getRole())) {
            try {
                loadCourses();
                loadUsers();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Loading Error",
                        "Failed to load data: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
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
            System.err.println("WARNING: courseLevelColumn is null in initialize()!");
        }
        if (courseTable == null) {
            System.err.println("WARNING: courseTable is null in initialize()!");
        }
        // Course Table initialization
        courseIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        courseNameColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        courseDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        coursePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        courseLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        courseCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        loadCourses();
        // User Table initialization
        userIdColumn.setCellValueFactory(cellData -> cellData.getValue().userIdProperty().asObject());
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userFullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
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
            dialogStage.setTitle("Add New Course");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(true);
            dialogStage.showAndWait();

            if (controller.isCourseAdded()) {
                loadCourses();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                controller.setCourseToEdit(selectedCourse);

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Edit Course");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.setScene(new Scene(root));
                dialogStage.setResizable(true);
                dialogStage.showAndWait();

                if (controller.isCourseUpdated()) {
                    loadCourses();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(" Please Select a Course");
            alert.setHeaderText(null);
            alert.setContentText("Please select a course to edit.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleDeleteCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(" Delete Course Confirmation");
            alert.setHeaderText(" Confirm Deletion");
            alert.setContentText(" You are about to delete the course: " + selectedCourse.getTitle() + ".\n" + "This action cannot be undone. Are you sure you want to proceed?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (courseDAO.delete(selectedCourse.getId())) {
                    loadCourses();
                    showAlert(Alert.AlertType.INFORMATION, "Complete", " Course deleted successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", " Could not delete course. Please try again later.");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", " Please select a course to delete.");
        }
    }

    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddUserDialog.fxml"));
            Parent root = loader.load();
            AddUserDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(true);
            dialogStage.showAndWait();

            if (controller.isUserAdded()) {
                loadUsers();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", " Unable to open Add User dialog!");
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
                dialogStage.setTitle("Edit User");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.setScene(new Scene(root));
                dialogStage.showAndWait();

                if (controller.isUserUpdated()) {
                    loadUsers();
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Unable to open Edit User dialog!");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a user to edit.");
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a user to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(" Delete User ");
        alert.setHeaderText(" Confirm Deletion " + selectedUser.getFullName() + "?");
        alert.setContentText(" Are you sure you want to delete user: " + selectedUser.getFullName() + "?\n" + "This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (userDAO.delete(selectedUser.getUserId())) {
                showAlert(Alert.AlertType.INFORMATION, "Complete", "User deleted successfully!");
                loadUsers();
                if (enrollmentManagementViewController != null) {
                    enrollmentManagementViewController.loadEnrollments();
                }

            } else {
                showAlert(Alert.AlertType.ERROR, "Error", " Could not delete user. Please try again later.");
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void logout() {
        SessionManager.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainForAD.fxml"));
            Scene scene = new Scene(loader.load());
            MainController controller = loader.getController();
            controller.initData(SessionManager.getInstance().getCurrentUser());

            Stage stage = (Stage) courseTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.isResizable();
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load main dashboard");
        }
    }

    @FXML
    private void handleDatabaseMaintenance() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Database Maintenance");
        confirmation.setHeaderText("Database Reindexing");
        confirmation.setContentText("This will update all IDs in the database. Continue?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                // First verify integrity
                DatabaseMaintenance.verifyDatabaseIntegrity();

                // Perform reindexing
                DatabaseMaintenance.reindexDatabase();

                // Verify again after reindexing
                DatabaseMaintenance.verifyDatabaseIntegrity();

                showAlert(Alert.AlertType.INFORMATION,
                        "Success",
                        "Database maintenance completed successfully");

                // Refresh the tables
                loadCourses();
                loadUsers();

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR,
                        "Error",
                        "Database maintenance failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    public void setCourseLevelColumn(TableColumn<Course, String> courseLevelColumn) {
        this.courseLevelColumn = courseLevelColumn;
    }
}