package com.coursemanagement.controller;


import com.coursemanagement.model.Course;
import com.coursemanagement.model.DatabaseConnection;
import com.coursemanagement.model.User;
import com.coursemanagement.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Label welcomeLabel;
    @FXML
    private ScrollPane coursesScrollPane;
    @FXML
    private GridPane coursesGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        welcomeLabel.setText("Welcome, " + SessionManager.getInstance().getCurrentUser().getFullName() + "!");
        loadCourses();
    }

    private void loadCourses() {
        List<Course> courses = DatabaseConnection.getAllCourses();
        int column = 0;
        int row = 0;

        for (Course course : courses) {
            VBox courseCard = createCourseCard(course);
            coursesGrid.add(courseCard, column, row);

            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createCourseCard(Course course) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(280);

        // Course image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(140);
        imageView.setPreserveRatio(false);
        try {
            Image image = new Image(course.getImageUrl() != null ? course.getImageUrl() : "https://via.placeholder.com/250x140");
            imageView.setImage(image);
        } catch (Exception e) {
            Image placeholder = new Image("https://via.placeholder.com/250x140");
            imageView.setImage(placeholder);
        }

        // Course title
        Label titleLabel = new Label(course.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        titleLabel.setWrapText(true);


        // Price and duration
        Label priceLabel = new Label("$" + course.getPrice() + " • " + " hours • " + course.getLevel());
        priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #0066cc; -fx-font-weight: bold;");

        card.getChildren().addAll(imageView, titleLabel, priceLabel);

        // Click handler
        card.setOnMouseClicked(e -> openCourseDetail(course));
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "; -fx-cursor: hand;"));

        return card;
    }

    @FXML
    private void openCourseDetail(Course course) {
        try {
            // First verify if the resource exists
            URL resourceUrl = getClass().getResource("/fxml/course-detail.fxml");
            if (resourceUrl == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Resource Not Found");
                alert.setContentText("Could not find course-detail.fxml");
                alert.showAndWait();
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Scene scene = new Scene(loader.load());

            CourseController controller = loader.getController();
            controller.setCourse(course);

            Stage stage = new Stage();
            stage.setTitle("Course Details");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open course details");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void openProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Profile Management");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void initData(User currentUser) {
        if (currentUser != null) {
            try {
                // Update welcome message
                welcomeLabel.setText("Welcome, " + currentUser.getFullName() + "!");

                // Load courses
                loadCourses();

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to load data");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        } else {
            // Handle case where no user is logged in
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No user logged in");
            alert.setContentText("Please log in to access this page.");
            alert.showAndWait();

            // Redirect to login
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                stage.setScene(scene);
            }
            catch ( IOException e) {
                e.printStackTrace();
            }
        }
    }
}