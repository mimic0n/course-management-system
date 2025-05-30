// Ví dụ trong một lớp tiện ích (ví dụ: com.coursemanagement.util.PasswordHasher.java)
// Hoặc bạn có thể thêm trực tiếp vào UserDAO nếu muốn đơn giản
package com.coursemanagement.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid password hash format: " + e.getMessage());
            return false;
        }
    }
}