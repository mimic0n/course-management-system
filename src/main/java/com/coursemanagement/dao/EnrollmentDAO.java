package com.coursemanagement.dao;

import com.coursemanagement.model.Course;
import com.coursemanagement.model.Enrollment;
import com.coursemanagement.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    private final DatabaseConnection dbConnection;

    public EnrollmentDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    // Enroll user in course
    public boolean enroll(int userId, int courseId) {
        String sql = "INSERT INTO enrollments (user_id, course_id, status, progress) VALUES (?, ?, 'ACTIVE', 0)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error enrolling user: " + e.getMessage());
        } finally {
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return false;
    }

    // Check if user is enrolled in course
    public boolean isEnrolled(int userId, int courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE user_id = ? AND course_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking enrollment: " + e.getMessage());
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return false;
    }

    // Get user's enrolled courses
    public List<Course> getUserEnrolledCourses(int userId) {
        List<Course> courses = new ArrayList<>();
        String sql = """
            SELECT c.*, e.enrollment_date, e.status, e.progress 
            FROM courses c 
            INNER JOIN enrollments e ON c.id = e.course_id 
            WHERE e.user_id = ? 
            ORDER BY e.enrollment_date DESC
            """;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setTitle(rs.getString("title"));
                course.setDescription(rs.getString("description"));
                course.setInstructor(rs.getString("instructor"));
                course.setPrice(rs.getDouble("price"));
                course.setDurationHours(rs.getInt("duration_hours"));
                course.setLevel(rs.getString("level"));
                course.setImageUrl(rs.getString("image_url"));
                course.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user enrolled courses: " + e.getMessage());
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return courses;
    }

    // Get all enrollments for a course
    public List<Enrollment> getCourseEnrollments(int courseId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = """
            SELECT e.*, u.username, u.full_name, u.email 
            FROM enrollments e 
            INNER JOIN users u ON e.user_id = u.id 
            WHERE e.course_id = ? 
            ORDER BY e.enrollment_date DESC
            """;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Enrollment enrollment = mapResultSetToEnrollment(rs);

                // Set user info
                User user = new User();
                user.setId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                enrollment.setUser(user);

                enrollments.add(enrollment);
            }
        } catch (SQLException e) {
            System.err.println("Error getting course enrollments: " + e.getMessage());
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return enrollments;
    }

    // Update enrollment progress
    public boolean updateProgress(int userId, int courseId, int progress) {
        String sql = "UPDATE enrollments SET progress = ? WHERE user_id = ? AND course_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Math.max(0, Math.min(100, progress))); // Ensure progress is between 0-100
            stmt.setInt(2, userId);
            stmt.setInt(3, courseId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating progress: " + e.getMessage());
        } finally {
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return false;
    }

    // Update enrollment status
    public boolean updateStatus(int userId, int courseId, String status) {
        String sql = "UPDATE enrollments SET status = ? WHERE user_id = ? AND course_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, userId);
            stmt.setInt(3, courseId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating enrollment status: " + e.getMessage());
        } finally {
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return false;
    }

    // Cancel enrollment (soft delete)
    public boolean cancelEnrollment(int userId, int courseId) {
        return updateStatus(userId, courseId, "CANCELLED");
    }

    // Get enrollment details
    public Enrollment getEnrollment(int userId, int courseId) {
        String sql = "SELECT * FROM enrollments WHERE user_id = ? AND course_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToEnrollment(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting enrollment details: " + e.getMessage());
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return null;
    }

    // Get enrollment statistics for admin
    public int getTotalEnrollments() {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE status = 'ACTIVE'";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total enrollments: " + e.getMessage());
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return 0;
    }

    // Get enrollment count for a specific course
    public int getCourseEnrollmentCount(int courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id = ? AND status = 'ACTIVE'";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting course enrollment count: " + e.getMessage());
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return 0;
    }

    // Helper method to map ResultSet to Enrollment object
    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(rs.getInt("id"));
        enrollment.setUserId(rs.getInt("user_id"));
        enrollment.setCourseId(rs.getInt("course_id"));
        enrollment.setEnrollmentDate(rs.getTimestamp("enrollment_date").toLocalDateTime());
        enrollment.setStatus(rs.getString("status"));
        enrollment.setProgress(rs.getInt("progress"));
        return enrollment;
    }
}