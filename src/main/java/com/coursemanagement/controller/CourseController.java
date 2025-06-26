package com.coursemanagement.controller;


import com.coursemanagement.dao.EnrollmentDAO;
import com.coursemanagement.model.Course;
import com.coursemanagement.util.SessionManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent; // Import this for handleCourseClick
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;

public class CourseController {
    @FXML
    private ImageView courseImage;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label enrollmentCountLabel;
    @FXML
    private Button enrollButton;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private VBox courseCardContainer;

    private Course course;
    private final EnrollmentDAO enrollmentDAO;
    private boolean courseAdded = false;
    private boolean courseUpdated = false;

    public CourseController() {
        this.enrollmentDAO = new EnrollmentDAO();
    }

    public void setCourse(Course course) {
        this.course = course;
        loadCourseData();
    }

    private void loadCourseData() {
        if (course != null) {
            titleLabel.setText(course.getTitle());
            descriptionLabel.setText(course.getDescription());
            priceLabel.setText("$" + String.format("%.2f", course.getPrice()));
            levelLabel.setText(course.getLevel());

            loadEnrollmentCount();
            loadCourseImage();
            checkEnrollmentStatus();
        }
    }


    private void loadEnrollmentCount() {
        Task<Integer> countTask = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                return enrollmentDAO.getCourseEnrollmentCount(course.getId());
            }
        };

        countTask.setOnSucceeded(e -> {
            int count = countTask.getValue();
            if (enrollmentCountLabel != null) {
                enrollmentCountLabel.setText(count + " students enrolled");
            }
        });

        countTask.setOnFailed(e -> {
            enrollmentCountLabel.setText("Enrollment data unavailable");
        });

        new Thread(countTask).start();
    }

    private void loadCourseImage() {
        Task<Image> imageTask = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
<<<<<<< HEAD
                String imagePath = course.getImageUrl();
                if (imagePath == null || imagePath.isBlank()) {
                    imagePath = "/images/C4Uicon.png";
                } else {
                    imagePath = "/" + imagePath;
                }
                return new Image(getClass().getResourceAsStream(imagePath));
            }
        };

        imageTask.setOnSucceeded(e -> {
            if (courseImage != null) {
                Image loadedImage = imageTask.getValue();
                if (loadedImage != null && !loadedImage.isError()) {
                    courseImage.setImage(loadedImage);
                } else {
                    courseImage.setImage(new Image(getClass().getResourceAsStream("/images/C4Uicon.png")));
                }
            }
        });
        imageTask.setOnFailed(e -> {
            if (courseImage != null) {
                courseImage.setImage(new Image(getClass().getResourceAsStream("/images/C4Uicon.png")));
            }
        });
=======
                String imageUrl = course.getImageUrl() != null ?
                        course.getImageUrl() : "src/main/resources/images/Java-Programming-Masterclass-Beginners-Experts-1024x614.webp";
                return new Image(getClass().getResourceAsStream(imageUrl));
            }
        };

        imageTask.setOnSucceeded(e ->{
            if (courseImage != null) {
                Image image = imageTask.getValue();
            }
        }
        );
        imageTask.setOnFailed(e -> {
            if (courseImage != null) {
                courseImage.setImage(new Image(getClass().getResourceAsStream("/images/default-course.png")));
            }
            });
>>>>>>> origin/main

        new Thread(imageTask).start();
    }

    private void checkEnrollmentStatus() {
        int currentUserId = SessionManager.getInstance().getCurrentUser().getUserId();

        Task<Boolean> checkTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return enrollmentDAO.isEnrolled(currentUserId, course.getId());
            }
        };

        checkTask.setOnSucceeded(e -> {
            boolean isEnrolled = checkTask.getValue();
            if (enrollButton != null) {
                if (isEnrolled) {
                    enrollButton.setText("Already Enrolled");
                    enrollButton.setDisable(true);
                    enrollButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 15; -fx-border-radius: 25; -fx-background-radius: 25; -fx-font-size: 16px; -fx-font-weight: bold;");
                } else {
                    enrollButton.setText("Enroll Now");
                    enrollButton.setDisable(false);
                    enrollButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-padding: 15; -fx-border-radius: 25; -fx-background-radius: 25; -fx-font-size: 16px; -fx-font-weight: bold;");
                }
            }
        });

        checkTask.setOnFailed(e -> {
            enrollButton.setText("Enrollment Status Unknown");
            enrollButton.setDisable(true);
        });

        new Thread(checkTask).start();
    }


    @FXML
    private void enrollCourse() {
<<<<<<< HEAD
        if (course == null) {
            return;
        };
        if (course == null ) {
            return;
        }
        if(SessionManager.getInstance().getCurrentUser()== null) {
            showAlert("Error", "Please login to enroll in a course.", Alert.AlertType.ERROR);
            return;
        }
=======
        if (course == null) return;

>>>>>>> origin/main
        enrollButton.setDisable(true);
        showLoading(true);

        int currentUserId = SessionManager.getInstance().getCurrentUser().getUserId();

        Task<Boolean> enrollTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return enrollmentDAO.enroll(currentUserId, course.getId());
            } //call to the database to enroll the user in the course
        };

        enrollTask.setOnSucceeded(e -> {
            showLoading(false);
            boolean success = enrollTask.getValue();

            if (success) {
                showAlert("Success", "Successfully enrolled in: " + course.getTitle(), Alert.AlertType.INFORMATION);
                enrollButton.setText("Already Enrolled");
                enrollButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 15; -fx-border-radius: 25; -fx-background-radius: 25; -fx-font-size: 16px; -fx-font-weight: bold;");
                loadEnrollmentCount(); // Refresh enrollment count
            } else {
                showAlert("Error", "Failed to enroll in the course. Please try again.", Alert.AlertType.ERROR);
                enrollButton.setDisable(false);
            }
        });

        enrollTask.setOnFailed(e -> {
            showLoading(false);
            showAlert("Error", "Enrollment failed: " + e.getSource().getException().getMessage(), Alert.AlertType.ERROR);
            enrollButton.setDisable(false);
        });

        new Thread(enrollTask).start();
    }

    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(show);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
<<<<<<< HEAD

=======

    public boolean isCourseAdded() {
        return courseAdded;
    }

    public boolean isCourseUpdated() {
        return courseUpdated;
    }

    // Update the enrollCourse method to set courseAdded


    // Add a method to update course
    public void updateCourse(Course updatedCourse) {
        // Logic to update course
        try {
            // Update course logic here
            courseUpdated = true;
        } catch (Exception e) {
            courseUpdated = false;
            e.printStackTrace();
        }
    }

    public void setCourseData(Course course) {
        this.course = course;
        titleLabel.setText(course.getTitle());
        descriptionLabel.setText(course.getDescription());
        priceLabel.setText("$" + String.format("%.2f", course.getPrice()));
        levelLabel.setText(course.getLevel());
        categoryLabel.setText(course.getCategory());

        // Load course image
        if (course.getImageUrl() != null && !course.getImageUrl().isEmpty()) ;
        {
            try {
                Image image = new Image(course.getImageUrl(), true);
                image.errorProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        courseImage.setImage(new Image(getClass().getResourceAsStream("/images/default-course.png")));
                    }
                });
                courseImage.setImage(image);
            } catch (Exception e) {
                System.err.println("Invalid image URL format: " + course.getImageUrl() + ". Using default.");
                courseImage.setImage(new Image(getClass().getResourceAsStream("/images/default-course.png")));
            }
        }
    }

    @FXML
    private void handleCourseClick(MouseEvent event) {
        if (course != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/coursemanagement/views/course-detail.fxml"));
                VBox courseDetailView = loader.load();

                CourseController courseController = loader.getController();
                courseController.setCourse(course); // Pass the course object to the detail controller

                Stage stage = (Stage) courseCardContainer.getScene().getWindow(); // Get the current stage
                Scene scene = new Scene(courseDetailView);
                stage.setScene(scene);
                stage.setTitle("Course Details");
            } catch (IOException e) {
                e.printStackTrace();
                // Optionally show an error alert
            }
        }
    }
>>>>>>> origin/main
}
