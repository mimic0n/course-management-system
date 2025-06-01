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
    private Course courseToEdit; // The current course being edited
    private CourseDAO courseDAO = new CourseDAO();
    private boolean courseUpdated = false;

    @FXML
    public void initialize() {
        levelComboBox.setItems(FXCollections.observableArrayList("Beginner", "Intermediate", "Advanced"));
        categoryComboBox.setItems(FXCollections.observableArrayList("Development", "Business", "Design", "Marketing", "IT & Software", "Personal Development", "Photography", "Music"));
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setCourseToEdit(Course course) {
        this.courseToEdit = course;
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
        if (this.courseToEdit == null) {
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
        courseToEdit.setTitle(title);
        courseToEdit.setDescription(description);
        courseToEdit.setPrice(price);
        courseToEdit.setLevel(level);
        courseToEdit.setCategory(category);
        courseToEdit.setImageUrl(imageUrl.isEmpty() ? null : imageUrl);


        if (courseDAO.update(courseToEdit)) {
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


    @FXML
    private void handleUpdate() {
        if (validateInput()) {
            courseToEdit.setTitle(titleField.getText());
            courseToEdit.setDescription(descriptionArea.getText());
            courseToEdit.setPrice(Double.parseDouble(priceField.getText()));
            courseToEdit.setLevel(levelComboBox.getValue());
            courseToEdit.setCategory(categoryComboBox.getValue());
            courseToEdit.setImageUrl(imageUrlField.getText());

            if (courseDAO.update(courseToEdit)) {
                courseUpdated = true;
                dialogStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update course");
            }
        }
    }


    private boolean validateInput() {
        String errorMessage = "";
        if (titleField.getText().isEmpty()) {
            errorMessage += "Course title is required\n";
        }
        if (descriptionArea.getText().isEmpty()) {
            errorMessage += "Course description is required\n";
        }
        if (priceField.getText().isEmpty()) {
            errorMessage += "Price is required\n";
        } else {
            try {
                Double.parseDouble(priceField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Price must be a valid number\n";
            }
        }
        if (levelComboBox.getValue() == null) {
            errorMessage += "Level is required\n";
        }
        if (categoryComboBox.getValue().isEmpty()) {
            errorMessage += "Category is required\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Validation Error", errorMessage);
            return false;
        }
    }
}