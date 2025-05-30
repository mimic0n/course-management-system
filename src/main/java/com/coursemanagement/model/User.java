package com.coursemanagement.model;
import java.time.LocalDateTime;
import java.util.Objects;

import javafx.beans.property.*;

public class User {
    private IntegerProperty  id;
    private StringProperty username;
    private StringProperty email;
    private StringProperty password;
    private StringProperty fullName;
    private StringProperty role;

    // Constructors
    public User() {
        this.id = new SimpleIntegerProperty();
        this.username = new SimpleStringProperty();
        this.password = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.fullName = new SimpleStringProperty();
        this.role = new SimpleStringProperty();
    }
    public User(String username, String password, String email, String fullName, String role) {
        this.id = new SimpleIntegerProperty(-1);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.email = new SimpleStringProperty(email);
        this.fullName = new SimpleStringProperty(fullName);
        this.role = new SimpleStringProperty(role);
    }

    public User(String username, String password, String email, String fullName, String role, IntegerProperty id, StringProperty username1, StringProperty email1, StringProperty password1, StringProperty fullName1, StringProperty role1) {
        this.id = id;
        this.username = username1;
        this.email = email1;
        this.password = password1;
        this.fullName = fullName1;
        this.role = role1;
    }
    public User(int id, String username, String email, String fullName, String role) {
        this.id = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(""); // Password is not updated through this constructor
        this.email = new SimpleStringProperty(email);
        this.fullName = new SimpleStringProperty(fullName);
        this.role = new SimpleStringProperty(role);
    }

    public User(int id, String username, String email, String password, String fullName, String role, LocalDateTime createdAt) {
        this.id = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(username);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
        this.fullName = new SimpleStringProperty(fullName);
        this.role = new SimpleStringProperty(role);
    }

    public User(String username, String email, String password, String fullName) {
        this.id = new SimpleIntegerProperty(-1); // Default ID for new users
        this.username = new SimpleStringProperty(username);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
        this.fullName = new SimpleStringProperty(fullName);
        this.role = new SimpleStringProperty("USER"); // Default role
    }

    // Getters for properties
    public IntegerProperty userIdProperty() {
        return id;
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public StringProperty roleProperty() {
        return role;
    }

    // Getters for values
    public int getUserId() {
        return id.get();
    }

    public String getUsername() {
        return username.get();
    }

    public String getPassword() {
        return password.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getFullName() {
        return fullName.get();
    }

    public String getRole() {
        return role.get();
    }

    // Setters
    public void setUserId(int userId) {
        this.id.set(userId);
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.get());
    }
}