package com.coursemanagement.model;

import java.time.LocalDateTime;

public class Course {
    private int id;
    private String title;
    private String description;
    private String instructor;
    private double price;
    private int durationHours;
    private String level;
    private String imageUrl;
    private LocalDateTime createdAt;

    // Constructors
    public Course() {
    }

    public Course(String title, String description, String instructor, double price, int durationHours, String level) {
        this.title = title;
        this.description = description;
        this.instructor = instructor;
        this.price = price;
        this.durationHours = durationHours;
        this.level = level;
    }
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getDurationHours() { return durationHours; }
    public void setDurationHours(int durationHours) { this.durationHours = durationHours; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", instructor='" + instructor + '\'' +
                ", price=" + price +
                '}';
    }

}