module course.management.system {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.j;
    requires org.slf4j;
    requires org.apache.commons.lang3;
    requires com.fasterxml.jackson.databind;
    requires bcrypt;
    requires jdk.jdi;

    exports com.coursemanagement;
    exports com.coursemanagement.controller;
    exports com.coursemanagement.model;
}