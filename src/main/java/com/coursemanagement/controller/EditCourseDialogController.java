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

public class EditCourseDialogController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> levelComboBox;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField imageUrlField;

    private Stage dialogStage;
    private Course course; // The current course being edited
    private CourseDAO courseDAO = new CourseDAO();
    private boolean courseUpdated = false;

    @FXML
    public void initialize() {
        levelComboBox.setItems(FXCollections.observableArrayList("Beginner", "Intermediate", "Advanced", "All Levels"));
        categoryComboBox.setItems(FXCollections.observableArrayList("Development", "Business", "Design", "Marketing", "IT & Software", "Personal Development", "Photography", "Music"));
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setCourseToEdit(Course course) {
        this.course = course;
        // Populate fields with existing course data
        if (course != null) {
            titleField.setText(course.getTitle());
            descriptionArea.setText(course.getDescription());
            priceField.setText(String.valueOf(course.getPrice()));
            levelComboBox.getSelectionModel().select(course.getLevel());
            categoryComboBox.getSelectionModel().select(course.getCategory());
            imageUrlField.setText(course.getImageUrl());
        }
    }

    public boolean isCourseUpdated() {
        return courseUpdated;
    }

    @FXML
    private void handleSaveCourse() {
        if (this.course == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No course selected for editing.");
            return;
        }

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

        // Update the existing course object
        course.setTitle(title);
        course.setDescription(description);
        course.setPrice(price);
        course.setLevel(level);
        course.setCategory(category);
        course.setImageUrl(imageUrl.isEmpty() ? null : imageUrl);


        if (courseDAO.update(course)) {
            courseUpdated = true;
            showAlert(Alert.AlertType.INFORMATION, "Success", "Course updated successfully!");
            dialogStage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not update course. Another error occurred.");
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