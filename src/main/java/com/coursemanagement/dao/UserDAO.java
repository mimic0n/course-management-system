package com.coursemanagement.dao;

import com.coursemanagement.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;
import com.coursemanagement.util.PasswordHasher;

public class UserDAO {
    private final DatabaseConnection dbConnection;

    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    // Authenticate user for login
    public User authenticate(String username, String password) {
        String query = "SELECT user_id, username, password FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                System.out.println("Stored hash for user " + username + ": " + storedHash); // Debug line

                if (storedHash != null && storedHash.startsWith("$2")) {
                    if (PasswordHasher.checkPassword(password , storedHash)) {
                        User user = new User();
                        user.setUserId(rs.getInt("user_id"));
                        user.setUsername(rs.getString("username"));
                        // Set other user properties
                        return user;
                    }
                } else {
                    System.err.println("Invalid hash format for user: " + username); // Debug line
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during authentication", e);
        }
    }

    // Create new user
    public boolean create(User user) {
        String sql = "INSERT INTO Users (username, password, email, full_name, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Băm mật khẩu trước khi lưu
            String hashedPassword = PasswordHasher.hashPassword(user.getPassword());
            user.setPassword(hashedPassword); // Cập nhật mật khẩu đã băm vào đối tượng User

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword()); // Lưu mật khẩu đã băm
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole()); // "student", "teacher", "admin"

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            // Xử lý exception tốt hơn, có thể ném lại một custom exception
            return false;
        }
    }

    // Get user by ID
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return null;
    }

    // Get user by username
    public User findByUsername(String username) {
        String query = "SELECT user_id, username, email, full_name, password, role FROM users WHERE username = ?";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            rs = statement.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setFullName(rs.getString("full_name"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                return user;
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.getInstance().closeResultSet(rs);
            DatabaseConnection.getInstance().closeStatement(statement);
            DatabaseConnection.getInstance().closeConnection(connection);
        }
    }

    // Check if email exists
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return false;
    }

    // Update user
    public boolean update(User user) {
        String sql;
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            sql = "UPDATE Users SET username = ?, password = ?, email = ?, full_name = ?, role = ? WHERE user_id = ?";
        } else {
            sql = "UPDATE Users SET username = ?, email = ?, full_name = ?, role = ? WHERE user_id = ?";
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);

            int paramIndex = 1;
            stmt.setString(paramIndex++, user.getUsername());

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                String hashedPassword = PasswordHasher.hashPassword(user.getPassword());
                stmt.setString(paramIndex++, hashedPassword);
            }

            stmt.setString(paramIndex++, user.getEmail());
            stmt.setString(paramIndex++, user.getFullName());
            stmt.setString(paramIndex++, user.getRole());
            stmt.setInt(paramIndex, user.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        } finally {
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return false;
    }


    // Get all users (for admin)
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return users;
    }

    // Delete user
    public boolean delete(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        } finally {
            dbConnection.closeStatement(stmt);
            dbConnection.closeConnection(conn);
        }
        return false;
    }

    // Helper method to map ResultSet to User object
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        return user;
    }

}