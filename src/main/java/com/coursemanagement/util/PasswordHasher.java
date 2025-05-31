package com.coursemanagement.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    // Number of salt rounds for BCrypt
    private static final int SALT_ROUNDS = 12;

    // Hash a password using BCrypt
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(SALT_ROUNDS));
    }

    // Verify a password against a stored hash
    public static boolean checkPassword(String plainTextPassword, String storedHash) {
        if (plainTextPassword == null || storedHash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, storedHash);
        } catch (IllegalArgumentException e) {
            System.err.println("Error checking password: " + e.getMessage());
            return false;
        }
    }
}