package com.coursemanagement.util;

import com.coursemanagement.model.DatabaseConnection;
import java.sql.*;

public class DatabaseMaintenance {

    public static void reindexDatabase() {
        String[] queries = {
                "SET @count = 0;",
                "UPDATE users SET user_id = @count:= @count + 1 ORDER BY user_id;",
                "ALTER TABLE users AUTO_INCREMENT = 1;",

                "SET @count = 0;",
                "UPDATE courses SET course_id = @count:= @count + 1 ORDER BY course_id;",
                "ALTER TABLE courses AUTO_INCREMENT = 1;",

                "UPDATE enrollments e " +
                        "JOIN users u ON e.user_id = u.user_id " +
                        "JOIN courses c ON e.course_id = c.course_id " +
                        "SET e.user_id = u.user_id, e.course_id = c.course_id;"
        };

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            for (String query : queries) {
                stmt.executeUpdate(query);
            }

            conn.commit();
            System.out.println("Database reindexing completed successfully");

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();

        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void verifyDatabaseIntegrity() {
        String[] checks = {
                "SELECT COUNT(*) as orphaned FROM enrollments e " +
                        "LEFT JOIN users u ON e.user_id = u.user_id " +
                        "WHERE u.user_id IS NULL",

                "SELECT COUNT(*) as orphaned FROM enrollments e " +
                        "LEFT JOIN courses c ON e.course_id = c.course_id " +
                        "WHERE c.course_id IS NULL"
        };

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            boolean hasIssues = false;

            for (String check : checks) {
                ResultSet rs = stmt.executeQuery(check);
                if (rs.next() && rs.getInt("orphaned") > 0) {
                    hasIssues = true;
                    System.out.println("Found " + rs.getInt("orphaned") + " orphaned records");
                }
                rs.close();
            }

            if (!hasIssues) {
                System.out.println("Database integrity verified - no issues found");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}