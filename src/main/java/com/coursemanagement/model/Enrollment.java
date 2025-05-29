package com.coursemanagement.model;

import java.time.LocalDateTime;

public class Enrollment {
    private int id;
    private int userId;
    private int courseId;
    private LocalDateTime enrollmentDate;
    private String status;
    private int progress;

    // Navigation properties
    private User user;
    private Course course;

    // Constructors
    public Enrollment() {}

    public Enrollment(int userId, int courseId) {
        this.userId = userId;
        this.courseId = courseId;
        this.status = "ACTIVE";
        this.progress = 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDateTime enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    @Override
    public String toString() {
        return "Enrollment{" +
                "id=" + id +
                ", userId=" + userId +
                ", courseId=" + courseId +
                ", status='" + status + '\'' +
                ", progress=" + progress +
                '}';
    }
}