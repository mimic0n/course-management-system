-- Create database
CREATE DATABASE coursemanagement;
USE coursemanagement;

-- Users table
CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       role ENUM('ADMIN', 'USER') DEFAULT 'USER',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Courses table
CREATE TABLE courses (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         title VARCHAR(200) NOT NULL,
                         description TEXT,
                         instructor VARCHAR(100) NOT NULL,
                         price DECIMAL(10,2) DEFAULT 0.00,
                         duration_hours INT DEFAULT 0,
                         level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') DEFAULT 'BEGINNER',
                         image_url VARCHAR(500),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Enrollments table (thêm bảng ghi danh)
CREATE TABLE enrollments (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             user_id INT NOT NULL,
                             course_id INT NOT NULL,
                             enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'ACTIVE',
                             progress INT DEFAULT 0,
                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                             FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
                             UNIQUE KEY unique_enrollment (user_id, course_id)
);

-- Insert sample data
INSERT INTO courses (title, description, instructor, price, duration_hours, level, image_url) VALUES
                                                                                                  ('Java Programming Masterclass', 'Complete Java course from basics to advanced', 'John Smith', 99.99, 40, 'BEGINNER', 'https://via.placeholder.com/300x200'),
                                                                                                  ('Web Development with React', 'Learn modern React development', 'Jane Doe', 89.99, 35, 'INTERMEDIATE', 'https://via.placeholder.com/300x200'),
                                                                                                  ('Python Data Science', 'Data analysis and machine learning with Python', 'Mike Johnson', 129.99, 50, 'ADVANCED', 'https://via.placeholder.com/300x200');

INSERT INTO users (username, email, password, full_name, role) VALUES
                                                                   ('admin', 'admin@course.com', 'admin123', 'System Admin', 'ADMIN'),
                                                                   ('user1', 'user1@test.com', 'user123', 'Test User', 'USER');