-- Create database
USE coursemanagement;

-- Users table
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(60) NOT NULL, -- Kích thước 60 ký tự cho BCrypt hash
                       email VARCHAR(100) NOT NULL UNIQUE,
                       full_name VARCHAR(100),
                       role VARCHAR(20) DEFAULT 'student' NOT NULL, -- 'student', 'teacher', 'admin'
                       registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Courses table
CREATE TABLE courses (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         title VARCHAR(255) NOT NULL,
                         description TEXT,
                         instructor VARCHAR(100) NOT NULL,
                         price DECIMAL(10,2) DEFAULT 0.00,
                         category VARCHAR(50) DEFAULT 'General',
                         level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') DEFAULT 'BEGINNER',
                         image_url VARCHAR(255),
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
INSERT INTO courses (title, description, instructor, price, level, image_url) VALUES
                                                                                                  ('Java Programming Masterclass', 'Complete Java course from basics to advanced', 'John Smith', 99.99, 'BEGINNER', 'src/main/resources/images/Java-Programming-Masterclass-Beginners-Experts-1024x614.webp'),
                                                                                                  ('Web Development with React', 'Learn modern React development', 'Jane Doe', 89.99, 'INTERMEDIATE', 'src/main/resources/images/pythom-data-science.jpg'),
                                                                                                  ('Python Data Science', 'Data analysis and machine learning with Python', 'Mike Johnson', 129.99, 'ADVANCED', 'react-web-development-0254.webp');

INSERT INTO users (username, email, password, full_name, role) VALUES
                                                                   ('admin', 'admin@course.com', 'admin123', 'System Admin', 'ADMIN'),
                                                                   ('user1', 'user1@test.com', 'user123', 'Test User', 'USER');