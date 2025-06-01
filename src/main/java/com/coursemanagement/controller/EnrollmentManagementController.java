package com.coursemanagement.controller;

import com.coursemanagement.dao.EnrollmentDAO;
import com.coursemanagement.model.Enrollment;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EnrollmentManagementController {

    @FXML private TableView<Enrollment> enrollmentTable;
    @FXML private TableColumn<Enrollment, Integer> enrollmentIdColumn;
    @FXML private TableColumn<Enrollment, String> studentNameColumn;
    @FXML private TableColumn<Enrollment, String> courseTitleColumn;
    @FXML private TableColumn<Enrollment, String> enrollmentDateColumn;
    @FXML private TableColumn<Enrollment, String> statusColumn;
    @FXML private TableColumn<Enrollment, Integer> progressColumn;

    private EnrollmentDAO enrollmentDAO;

    @FXML
    public void initialize() {
        enrollmentDAO = new EnrollmentDAO();
        setupTableColumns();
        loadEnrollments();
    }

    private void setupTableColumns() {
        enrollmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        studentNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUser().getFullName()));

        courseTitleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCourse().getTitle()));

        enrollmentDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getEnrollmentDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            return new SimpleStringProperty(date.format(formatter));
        });

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        progressColumn.setCellValueFactory(new PropertyValueFactory<>("progress"));
    }

    public void loadEnrollments() {
        List<Enrollment> enrollmentList = enrollmentDAO.findAllWithDetails();
        ObservableList<Enrollment> observableEnrollments = FXCollections.observableArrayList(enrollmentList);
        enrollmentTable.setItems(observableEnrollments);
    }
}