package com.coursemanagement.dao;

import com.coursemanagement.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.coursemanagement.util.PasswordHasher;

public class UserDAO {
    private final DatabaseConnection dbConnection;

    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public User authenticate(String username, String password) {
        String query = "SELECT user_id, username, email, full_name, password, role FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            System.out.println("Attempting to authenticate user: " + username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    String storedRole = rs.getString("role");
                    System.out.println("Found user. Stored hash: " + storedHash);
                    System.out.println("User role: " + storedRole);

                    if (storedHash != null) {
                        boolean passwordMatch = PasswordHasher.checkPassword(password, storedHash);
                        System.out.println("Password match result: " + passwordMatch);

                        if (passwordMatch) {
                            User user = new User();
                            user.setUserId(rs.getInt("user_id"));
                            user.setUsername(rs.getString("username"));
                            user.setEmail(rs.getString("email"));
                            user.setFullName(rs.getString("full_name"));
                            user.setPassword(storedHash);
                            user.setRole(storedRole);
                            return user;
                        }
                    }
                } else {
                    System.out.println("No user found with username: " + username);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during authentication: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error during authentication", e);
        }
        return null;

    }

    public boolean create(User user) {
        String sql = "INSERT INTO Users (username, password, email, full_name, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Get user by ID
    public User findById(int id) {
        String sql = "SELECT user_id, username, email, full_name, password, role FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Get user by username
    public User findByUsername(String username) {
        String sql = "SELECT user_id, username, email, full_name, password, role FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username " + username + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public User findByEmail(String email) {
        String sql = "SELECT user_id, username, email, full_name, password, role FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by email " + email + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Check if email exists
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence for " + email + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(User user) {
        boolean updatePassword = user.getPassword() != null && !user.getPassword().isEmpty() && !user.getPassword().startsWith("$2a$");
        String sql;
        if (updatePassword) {
            sql = "UPDATE users SET username = ?, email = ?, password = ?, full_name = ?, role = ? WHERE user_id = ?";
        } else {
            sql = "UPDATE users SET username = ?, email = ?, full_name = ?, role = ? WHERE user_id = ?";
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            stmt.setString(paramIndex++, user.getUsername());
            stmt.setString(paramIndex++, user.getEmail());

            if (updatePassword) {
                stmt.setString(paramIndex++, PasswordHasher.hashPassword(user.getPassword()));
            }
            stmt.setString(paramIndex++, user.getFullName());
            stmt.setString(paramIndex++, user.getRole());
            stmt.setInt(paramIndex, user.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user " + user.getUsername() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    // Get all users (for admin)
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, email, full_name, password, role FROM users ORDER BY username ASC"; // Hoặc ORDER BY user_id
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql); // Sử dụng PreparedStatement nếu không có tham số cũng không sao
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    // Delete user
    public boolean delete(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user ID " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to map ResultSet to User object
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        return user;
    }
    public boolean createAdminAccount(String username, String password, String email, String fullName) {
        String sql = "INSERT INTO Users (username, password, email, full_name, role) VALUES (?, ?, ?, ?, 'ADMIN')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Hash the password before storing
            String hashedPassword = PasswordHasher.hashPassword(password);
            System.out.println("Creating admin account with hash: " + hashedPassword); // Debug line

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, email);
            stmt.setString(4, fullName);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Admin account created successfully");
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error creating admin account: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

    }
    public boolean save(User user) {
        String sql = "INSERT INTO users (username, password, email, full_name, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());  // Password should already be hashed
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteByUsername(String username) {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


}