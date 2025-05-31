package com.coursemanagement.model;

import javafx.beans.property.*;

public class Course {
    private IntegerProperty CourseID;
    private StringProperty title;
    private StringProperty description;
    private DoubleProperty price;
    private StringProperty level;
    private StringProperty category;
    private StringProperty imageUrl;

    // Constructors
    public Course(int CourseID, String title, String description, double price, String level, String category, String imageUrl) {
        this.CourseID = new SimpleIntegerProperty(CourseID);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.price = new SimpleDoubleProperty(price);
        this.level = new SimpleStringProperty(level);
        this.category = new SimpleStringProperty(category);
        this.imageUrl = new SimpleStringProperty(imageUrl);
    }

    public Course(String courseName, String description, double price, String level, String category, String imageUrl) {
        this.CourseID = new SimpleIntegerProperty(-1);
        this.title = new SimpleStringProperty(courseName);
        this.description = new SimpleStringProperty(description);
        this.price = new SimpleDoubleProperty(price);
        this.level = new SimpleStringProperty(level);
        this.category = new SimpleStringProperty(category);
        this.imageUrl = new SimpleStringProperty(imageUrl);
    }

    public Course() {
        this.CourseID = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.price = new SimpleDoubleProperty();
        this.level = new SimpleStringProperty();
        this.category = new SimpleStringProperty();
        this.imageUrl = new SimpleStringProperty();
    }

    // Property accessors
    public IntegerProperty idProperty() {
        return CourseID;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public StringProperty levelProperty() {
        return level;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public StringProperty imageUrlProperty() {
        return imageUrl;
    }

    // Getters
    public int getId() {
        return CourseID.get();
    }

    public String getTitle() {
        return title.get();
    }

    public String getDescription() {
        return description.get();
    }

    public double getPrice() {
        return price.get();
    }

    public String getLevel() {
        return level.get();
    }

    public String getCategory() {
        return category.get();
    }

    public String getImageUrl() {
        return imageUrl.get();
    }

    // Setters
    public void setId(int id) {
        this.CourseID.set(id);
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public void setLevel(String level) {
        this.level.set(level);
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl.set(imageUrl);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + CourseID.get() +
                ", title='" + title.get() + '\'' +
                ", description='" + description.get() + '\'' +
                ", price=" + price.get() +
                ", level='" + level.get() + '\'' +
                ", category='" + category.get() + '\'' +
                ", imageUrl='" + imageUrl.get() + '\'' +
                '}';
    }
}