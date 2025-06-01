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

    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(rs.getInt("enrollment_id"));
        enrollment.setUserId(rs.getInt("user_id"));
        enrollment.setCourseId(rs.getInt("course_id"));
        enrollment.setEnrollmentDate(rs.getTimestamp("enrollment_date").toLocalDateTime());
        enrollment.setStatus(rs.getString("status"));
        enrollment.setProgress(rs.getInt("progress"));
        return enrollment;
    }

    public boolean enroll(int userId, int courseId) {
        String sql = "INSERT INTO enrollments (user_id, course_id) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        conn = com.coursemanagement.model.DatabaseConnection.getConnection();
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
                course.setPrice(rs.getDouble("price"));
                course.setLevel(rs.getString("level"));
                course.setImageUrl(rs.getString("image_url"));
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
                user.setUserId(rs.getInt("user_id"));
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

    public List<Enrollment> findAllWithDetails() {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = """
            SELECT
                e.enrollment_id, e.enrollment_date, e.status, e.progress,
                u.user_id, u.username, u.full_name,
                c.course_id, c.title
            FROM
                enrollments e
            LEFT JOIN
                users u ON e.user_id = u.user_id
            LEFT JOIN
                courses c ON e.course_id = c.course_id
            ORDER BY
                e.enrollment_date DESC
            """;
        try( Connection conn = dbConnection.getConnection();
             Statement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()){
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));

                Course course = new Course();
                course.setId(rs.getInt("course_id"));
                course.setTitle(rs.getString("title"));

                Enrollment enrollment = new Enrollment();
                enrollment.setId(rs.getInt("enrollment_id"));
                enrollment.setEnrollmentDate(rs.getTimestamp("enrollment_date").toLocalDateTime());
                enrollment.setStatus(rs.getString("status"));
                enrollment.setProgress(rs.getInt("progress"));

                enrollment.setUser(user);
                enrollment.setCourse(course);
                enrollments.add(enrollment);
            }

        } catch (SQLException e) {
          throw new RuntimeException(e);
        } ;
        return enrollments;
    }
}