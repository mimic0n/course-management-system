package com.coursemanagement.controller;

import com.coursemanagement.dao.CourseDAO;
import com.coursemanagement.model.Course;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddCourseDialogController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> levelComboBox;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField imageUrlField;

    private Stage dialogStage;
    private CourseDAO courseDAO = new CourseDAO();
    private boolean courseAdded = false;

    @FXML
    public void initialize() {
        // Configure ComboBoxes for level and category
        levelComboBox.setItems(FXCollections.observableArrayList("Beginner", "Intermediate", "Advanced", "All Levels"));
        levelComboBox.getSelectionModel().selectFirst(); // Default selection

        categoryComboBox.setItems(FXCollections.observableArrayList("Development", "Business", "Design", "Marketing", "IT & Software", "Personal Development", "Photography", "Music"));
        categoryComboBox.getSelectionModel().selectFirst(); // Default selection
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isCourseAdded() {
        return courseAdded;
    }

    @FXML
    private void handleSaveCourse() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priceText = priceField.getText().trim();
        String level = levelComboBox.getValue();
        String category = categoryComboBox.getValue();
        String imageUrl = imageUrlField.getText().trim();

        if (title.isEmpty() || description.isEmpty() || priceText.isEmpty() || level == null || category == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in all required fields (Title, Description, Price, Level, Category).");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Price cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Invalid price format. Please enter a valid number.");
            return;
        }

        // Use the constructor that doesn't require an ID for new courses
        Course newCourse = new Course(title, description, price, level, category, imageUrl.isEmpty() ? null : imageUrl);

        if (courseDAO.create(newCourse)) {
            courseAdded = true;
            showAlert(Alert.AlertType.INFORMATION, "Success", "Course added successfully!");
            dialogStage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not add course. The course title might already exist or another error occurred.");
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