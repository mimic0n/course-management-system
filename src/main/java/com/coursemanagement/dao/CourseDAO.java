package com.coursemanagement.dao;

import com.coursemanagement.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private final DatabaseConnection dbConnection;

    public CourseDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    //(for admin)
    public boolean create(Course course) {
        String sql = "INSERT INTO courses (title, description, instructor, price, duration_hours, level, image_url) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, course.getTitle());
            stmt.setString(2, course.getDescription());
            stmt.setDouble(4, course.getPrice());
            stmt.setString(5, course.getCategory());
            stmt.setString(6, course.getLevel());
            stmt.setString(7, course.getImageUrl());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating course: " + e.getMessage());
        } finally {
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return false;
    }

    // Update course (for admin)
    public boolean update(Course course) {
        String sql = "UPDATE courses SET title = ?, description = ?, instructor = ?, price = ?, duration_hours = ?, level = ?, image_url = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, course.getTitle());
            stmt.setString(2, course.getDescription());
            stmt.setDouble(4, course.getPrice());
            stmt.setString(5, course.getCategory());
            stmt.setString(6, course.getLevel());
            stmt.setString(7, course.getImageUrl());
            stmt.setInt(8, course.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating course: " + e.getMessage());
        } finally {
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return false;
    }

    // Delete course (for admin)
    public boolean delete(int courseId) {
        String sql = "DELETE FROM courses WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
        } finally {
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return false;
    }

    // Get all courses
    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY created_at DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all courses: " + e.getMessage());
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return courses;
    }

    // Get course by ID
    public Course findById(int id) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCourse(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding course by ID: " + e.getMessage());
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return null;
    }



    // Find courses by level
    public List<Course> findByLevel(String level) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE level = ? ORDER BY created_at DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, level);
            rs = stmt.executeQuery();

            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding courses by level: " + e.getMessage());
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return courses;
    }

    // Helper method to map ResultSet to Course object
    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("course_id"));
        course.setTitle(rs.getString("title"));
        course.setDescription(rs.getString("description"));
        course.setPrice(rs.getDouble("price"));
        course.setLevel(rs.getString("level"));
        course.setCategory(rs.getString("category"));
        course.setImageUrl(rs.getString("image_url"));
        return course;
    }
}