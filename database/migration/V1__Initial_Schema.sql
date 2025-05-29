CREATE DATABASE coursemanagement;
USE coursemanagement;

CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       role ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE courses (
                         course_id INT AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(200) NOT NULL,
                         description TEXT,
                         instructor VARCHAR(100) NOT NULL,
                         price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                         category VARCHAR(50) NOT NULL,
                         duration INT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enrollments (
                             enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
                             user_id INT NOT NULL,
                             course_id INT NOT NULL,
                             enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'ACTIVE',
                             FOREIGN KEY (user_id) REFERENCES users(user_id),
                             FOREIGN KEY (course_id) REFERENCES courses(course_id)
);
-- Create categories table
CREATE TABLE categories (
                            category_id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            description TEXT,
                            icon VARCHAR(50),
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Update courses table
ALTER TABLE courses ADD COLUMN category_id INT;
ALTER TABLE courses ADD FOREIGN KEY (category_id) REFERENCES categories(category_id);

-- Insert default categories
INSERT INTO categories (name, description, icon) VALUES
                                                     ('Programming', 'Software development and coding courses', 'fa-code'),
                                                     ('Web Development', 'Frontend and backend web development', 'fa-globe'),
                                                     ('Data Science', 'Data analysis, machine learning, and AI', 'fa-chart-bar'),
                                                     ('Mobile Development', 'iOS and Android app development', 'fa-mobile-alt'),
                                                     ('Design', 'UI/UX design and graphic design', 'fa-paint-brush'),
                                                     ('Database', 'Database design and management', 'fa-database'),
                                                     ('Business', 'Management and entrepreneurship', 'fa-briefcase');

-- Update existing courses to use category_id
UPDATE courses SET category_id = 1 WHERE category = 'Programming';
UPDATE courses SET category_id = 2 WHERE category = 'Web Development';
UPDATE courses SET category_id = 3 WHERE category = 'Data Science';
UPDATE courses SET category_id = 4 WHERE category = 'Mobile Development';
UPDATE courses SET category_id = 5 WHERE category = 'Design';
UPDATE courses SET category_id = 6 WHERE category = 'Database';

-- Insert sample admin user
INSERT INTO users (username, password, email, full_name, role)
VALUES ('admin', 'admin123', 'admin@example.com', 'Administrator', 'ADMIN');

-- Insert sample courses
INSERT INTO courses (title, description, instructor, price, category, duration) VALUES
                                                                                    ('Java Programming Fundamentals', 'Learn Java from basics to advanced concepts', 'John Doe', 99.99, 'Programming', 40),
                                                                                    ('Web Development with React', 'Build modern web applications with React', 'Jane Smith', 149.99, 'Web Development', 35),
                                                                                    ('Data Science with Python', 'Complete guide to data science using Python', 'Bob Johnson', 199.99, 'Data Science', 50);
-- Không cần thay đổi gì, table users đã đủ
-- Chỉ cần thêm một số constraints
ALTER TABLE users ADD CONSTRAINT unique_username UNIQUE (username);
ALTER TABLE users ADD CONSTRAINT unique_email UNIQUE (email);

-- Add image column to courses table
ALTER TABLE courses ADD COLUMN image_path VARCHAR(255);
ALTER TABLE courses ADD COLUMN image_filename VARCHAR(100);