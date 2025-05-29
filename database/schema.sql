-- Course Management System Database Schema
-- Tạo cơ sở dữ liệu cho hệ thống quản lý khóa học

-- Tạo database
CREATE DATABASE IF NOT EXISTS coursemanagement;
USE coursemanagement;

-- Bảng users - Lưu thông tin người dùng (sinh viên và giảng viên)
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       role ENUM('STUDENT', 'TEACHER') NOT NULL,
                       phone VARCHAR(20),
                       address TEXT,
                       status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       INDEX idx_username (username),
                       INDEX idx_email (email),
                       INDEX idx_role (role),
                       INDEX idx_status (status)
);

-- Bảng courses - Lưu thông tin khóa học
CREATE TABLE courses (
                         course_id INT AUTO_INCREMENT PRIMARY KEY,
                         course_name VARCHAR(200) NOT NULL,
                         course_code VARCHAR(20) UNIQUE NOT NULL,
                         description TEXT,
                         teacher_id INT NOT NULL,
                         max_students INT DEFAULT 0,
                         credits INT DEFAULT 3,
                         status ENUM('ACTIVE', 'INACTIVE', 'COMPLETED', 'DELETED') DEFAULT 'ACTIVE',
                         start_date DATETIME,
                         end_date DATETIME,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                         FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE CASCADE,

                         INDEX idx_course_code (course_code),
                         INDEX idx_teacher_id (teacher_id),
                         INDEX idx_status (status),
                         INDEX idx_course_name (course_name)
);

-- Bảng enrollments - Lưu thông tin đăng ký khóa học
CREATE TABLE enrollments (
                             enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
                             student_id INT NOT NULL,
                             course_id INT NOT NULL,
                             enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             status ENUM('ACTIVE', 'COMPLETED', 'DROPPED', 'SUSPENDED') DEFAULT 'ACTIVE',
                             grade DECIMAL(4,2) DEFAULT NULL,
                             completion_date DATETIME DEFAULT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                             FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
                             FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,

                             UNIQUE KEY unique_enrollment (student_id, course_id),
                             INDEX idx_student_id (student_id),
                             INDEX idx_course_id (course_id),
                             INDEX idx_status (status)
);

-- Bảng lessons - Lưu thông tin bài học
CREATE TABLE lessons (
                         lesson_id INT AUTO_INCREMENT PRIMARY KEY,
                         course_id INT NOT NULL,
                         lesson_title VARCHAR(200) NOT NULL,
                         lesson_content TEXT,
                         lesson_order INT DEFAULT 1,
                         video_url VARCHAR(500),
                         document_url VARCHAR(500),
                         duration_minutes INT DEFAULT 0,
                         status ENUM('ACTIVE', 'INACTIVE', 'DRAFT') DEFAULT 'ACTIVE',
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                         FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,

                         INDEX idx_course_id (course_id),
                         INDEX idx_lesson_order (lesson_order),
                         INDEX idx_status (status)
);

-- Bảng assignments - Lưu thông tin bài tập
CREATE TABLE assignments (
                             assignment_id INT AUTO_INCREMENT PRIMARY KEY,
                             course_id INT NOT NULL,
                             title VARCHAR(200) NOT NULL,
                             description TEXT,
                             due_date DATETIME,
                             max_score DECIMAL(5,2) DEFAULT 100.00,
                             status ENUM('ACTIVE', 'INACTIVE', 'COMPLETED') DEFAULT 'ACTIVE',
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                             FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,

                             INDEX idx_course_id (course_id),
                             INDEX idx_due_date (due_date),
                             INDEX idx_status (status)
);

-- Bảng assignment_submissions - Lưu thông tin nộp bài tập
CREATE TABLE assignment_submissions (
                                        submission_id INT AUTO_INCREMENT PRIMARY KEY,
                                        assignment_id INT NOT NULL,
                                        student_id INT NOT NULL,
                                        submission_text TEXT,
                                        file_url VARCHAR(500),
                                        score DECIMAL(5,2) DEFAULT NULL,
                                        feedback TEXT,
                                        submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        graded_at DATETIME DEFAULT NULL,
                                        status ENUM('SUBMITTED', 'GRADED', 'LATE', 'MISSING') DEFAULT 'SUBMITTED',

                                        FOREIGN KEY (assignment_id) REFERENCES assignments(assignment_id) ON DELETE CASCADE,
                                        FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,

                                        UNIQUE KEY unique_submission (assignment_id, student_id),
                                        INDEX idx_assignment_id (assignment_id),
                                        INDEX idx_student_id (student_id),
                                        INDEX idx_status (status)
);

-- Bảng announcements - Lưu thông tin thông báo
CREATE TABLE announcements (
                               announcement_id INT AUTO_INCREMENT PRIMARY KEY,
                               course_id INT NOT NULL,
                               title VARCHAR(200) NOT NULL,
                               content TEXT NOT NULL,
                               priority ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
                               status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
                               created_by INT NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                               FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
                               FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE CASCADE,

                               INDEX idx_course_id (course_id),
                               INDEX idx_created_by (created_by),
                               INDEX idx_priority (priority),
                               INDEX idx_status (status)
);

-- Bảng attendance - Lưu thông tin điểm danh
CREATE TABLE attendance (
                            attendance_id INT AUTO_INCREMENT PRIMARY KEY,
                            course_id INT NOT NULL,
                            student_id INT NOT NULL,
                            lesson_id INT,
                            attendance_date DATE NOT NULL,
                            status ENUM('PRESENT', 'ABSENT', 'LATE', 'EXCUSED') NOT NULL,
                            notes TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                            FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
                            FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
                            FOREIGN KEY (lesson_id) REFERENCES lessons(lesson_id) ON DELETE SET NULL,

                            UNIQUE KEY unique_attendance (course_id, student_id, attendance_date),
                            INDEX idx_course_id (course_id),
                            INDEX idx_student_id (student_id),
                            INDEX idx_attendance_date (attendance_date),
                            INDEX idx_status (status)
);

-- Bảng course_materials - Lưu thông tin tài liệu khóa học
CREATE TABLE course_materials (
                                  material_id INT AUTO_INCREMENT PRIMARY KEY,
                                  course_id INT NOT NULL,
                                  title VARCHAR(200) NOT NULL,
                                  description TEXT,
                                  file_url VARCHAR(500) NOT NULL,
                                  file_type VARCHAR(50),
                                  file_size BIGINT DEFAULT 0,
                                  upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  uploaded_by INT NOT NULL,
                                  status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',

                                  FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
                                  FOREIGN KEY (uploaded_by) REFERENCES users(user_id) ON DELETE CASCADE,

                                  INDEX idx_course_id (course_id),
                                  INDEX idx_uploaded_by (uploaded_by),
                                  INDEX idx_file_type (file_type),
                                  INDEX idx_status (status)
);

-- Dữ liệu mẫu
-- Tạo tài khoản admin
INSERT INTO users (username, password, email, full_name, role) VALUES
    ('admin', 'admin1234', 'admin@coursemanagement.com', 'Administrator', 'TEACHER');

-- Tạo một số giảng viên mẫu
INSERT INTO users (username, password, email, full_name, role, phone, address) VALUES
                                                                                   ('teacher1', 'teacher1', 'teacher1@university.edu', 'Nguyễn Văn A', 'TEACHER', '0901234567', 'Hà Nội'),
                                                                                   ('teacher2', 'teacher2', 'teacher2@university.edu', 'Trần Thị B', 'TEACHER', '0901234568', 'TP. Hồ Chí Minh'),
                                                                                   ('teacher3', 'teacher3', 'teacher3@university.edu', 'Lê Văn C', 'TEACHER', '0901234569', 'Đà Nẵng');

-- Tạo một số sinh viên mẫu
INSERT INTO users (username, password, email, full_name, role, phone, address) VALUES
                                                                                   ('student1', 'student1', 'student1@student.edu', 'Hoàng Văn D', 'STUDENT', '0987654321', 'Hà Nội'),
                                                                                   ('student2', 'student2', 'student2@student.edu', 'Phạm Thị E', 'STUDENT', '0987654322', 'TP. Hồ Chí Minh'),
                                                                                   ('student3', 'student3', 'student3@student.edu', 'Vũ Văn F', 'STUDENT', '0987654323', 'Hải Phòng'),
                                                                                   ('student4', 'student4', 'student4@student.edu', 'Đặng Thị G', 'STUDENT', '0987654324', 'Cần Thơ'),
                                                                                   ('student5', 'student5', 'student5@student.edu', 'Bùi Văn H', 'STUDENT', '0987654325', 'Huế');

-- Tạo một số khóa học mẫu
INSERT INTO courses (course_name, course_code, description, teacher_id, max_students, credits, start_date, end_date) VALUES
                                                                                                                         ('Lập trình Java cơ bản', 'JAVA101', 'Khóa học giới thiệu về ngôn ngữ lập trình Java và lập trình hướng đối tượng', 2, 30, 3, '2024-09-01 08:00:00', '2024-12-15 17:00:00'),
                                                                                                                         ('Cơ sở dữ liệu', 'DB201', 'Khóa học về thiết kế và quản lý cơ sở dữ liệu', 3, 25, 3, '2024-09-01 10:00:00', '2024-12-15 17:00:00'),
                                                                                                                         ('Phát triển Web với Spring Boot', 'WEB301', 'Khóa học nâng cao về phát triển ứng dụng web với Spring Boot', 2, 20, 4, '2024-10-01 14:00:00', '2025-01-15 17:00:00'),
                                                                                                                         ('Trí tuệ nhân tạo cơ bản', 'AI101', 'Giới thiệu về trí tuệ nhân tạo và machine learning', 4, 35, 3, '2024-09-15 09:00:00', '2024-12-30 17:00:00');

-- Đăng ký khóa học cho sinh viên
INSERT INTO enrollments (student_id, course_id) VALUES
                                                    (5, 1), (6, 1), (7, 1), (8, 1), (9, 1), -- Java course
                                                    (5, 2), (6, 2), (7, 2), -- Database course
                                                    (6, 3), (7, 3), (8, 3), -- Spring Boot course
                                                    (5, 4), (8, 4), (9, 4); -- AI course

-- Tạo một số bài học mẫu
INSERT INTO lessons (course_id, lesson_title, lesson_content, lesson_order, duration_minutes) VALUES
                                                                                                  (1, 'Giới thiệu về Java', 'Tổng quan về ngôn ngữ Java và môi trường phát triển', 1, 90),
                                                                                                  (1, 'Biến và kiểu dữ liệu', 'Học về các kiểu dữ liệu cơ bản trong Java', 2, 120),
                                                                                                  (1, 'Cấu trúc điều khiển', 'If-else, switch-case, vòng lặp for, while', 3, 150),
                                                                                                  (2, 'Giới thiệu về CSDL', 'Khái niệm cơ sở dữ liệu và hệ quản trị CSDL', 1, 90),
                                                                                                  (2, 'Thiết kế CSDL quan hệ', 'ERD, normalization, khóa chính, khóa ngoại', 2, 120);

-- Tạo một số thông báo mẫu
INSERT INTO announcements (course_id, title, content, priority, created_by) VALUES
                                                                                (1, 'Chào mừng đến với khóa học Java', 'Chào mừng các bạn đến với khóa học Lập trình Java cơ bản. Hãy chuẩn bị tinh thần học tập tốt nhất!', 'HIGH', 2),
                                                                                (1, 'Thông báo về bài kiểm tra giữa kì', 'Bài kiểm tra giữa kì sẽ được tổ chức vào ngày 15/10/2024. Các bạn hãy ôn tập kỹ lưỡng.', 'MEDIUM', 2),
                                                                                (2, 'Tài liệu tham khảo', 'Các bạn có thể tham khảo thêm sách "Database System Concepts" để hiểu sâu hơn về cơ sở dữ liệu.', 'LOW', 3);

-- Views hữu ích cho ứng dụng

-- View thống kê khóa học
CREATE VIEW course_statistics AS
SELECT
    c.course_id,
    c.course_name,
    c.course_code,
    u.full_name as teacher_name,
    c.max_students,
    COUNT(e.enrollment_id) as current_students,
    c.status,
    c.start_date,
    c.end_date
FROM courses c
         LEFT JOIN users u ON c.teacher_id = u.user_id
         LEFT JOIN enrollments e ON c.course_id = e.course_id AND e.status = 'ACTIVE'
WHERE c.status != 'DELETED'
GROUP BY c.course_id, c.course_name, c.course_code, u.full_name, c.max_students, c.status, c.start_date, c.end_date;

-- View danh sách sinh viên theo khóa học
CREATE VIEW course_enrollments AS
SELECT
    c.course_id,
    c.course_name,
    c.course_code,
    u.user_id as student_id,
    u.username,
    u.full_name as student_name,
    u.email,
    e.enrollment_date,
    e.status as enrollment_status,
    e.grade
FROM courses c
         INNER JOIN enrollments e ON c.course_id = e.course_id
         INNER JOIN users u ON e.student_id = u.user_id
WHERE c.status != 'DELETED' AND u.status = 'ACTIVE'
ORDER BY c.course_name, u.full_name;

-- View thống kê sinh viên
CREATE VIEW student_statistics AS
SELECT
    u.user_id,
    u.username,
    u.full_name,
    u.email,
    COUNT(e.enrollment_id) as total_courses,
    COUNT(CASE WHEN e.status = 'ACTIVE' THEN 1 END) as active_courses,
    COUNT(CASE WHEN e.status = 'COMPLETED' THEN 1 END) as completed_courses,
    AVG(e.grade) as average_grade
FROM users u
         LEFT JOIN enrollments e ON u.user_id = e.student_id
WHERE u.role = 'STUDENT' AND u.status = 'ACTIVE'
GROUP BY u.user_id, u.username, u.full_name, u.email;


-- Stored Procedures hữu ích

DELIMITER //

-- Procedure đăng ký khóa học
CREATE PROCEDURE EnrollStudent(
    IN p_student_id INT,
    IN p_course_id INT,
    OUT p_result VARCHAR(100)
)
BEGIN
    DECLARE v_max_students INT;
    DECLARE v_current_students INT;
    DECLARE v_existing_enrollment INT;

    -- Kiểm tra sinh viên đã đăng ký chưa
    SELECT COUNT(*) INTO v_existing_enrollment
    FROM enrollments
    WHERE student_id = p_student_id AND course_id = p_course_id;

    IF v_existing_enrollment > 0 THEN
        SET p_result = 'ALREADY_ENROLLED';
    ELSE
        -- Kiểm tra số lượng tối đa
        SELECT max_students INTO v_max_students
        FROM courses
        WHERE course_id = p_course_id;

        SELECT COUNT(*) INTO v_current_students
        FROM enrollments
        WHERE course_id = p_course_id AND status = 'ACTIVE';

        IF v_max_students > 0 AND v_current_students >= v_max_students THEN
            SET p_result = 'COURSE_FULL';
        ELSE
            INSERT INTO enrollments (student_id, course_id)
            VALUES (p_student_id, p_course_id);
            SET p_result = 'SUCCESS';
        END IF;
    END IF;
END //

-- Procedure tính điểm trung bình
CREATE PROCEDURE CalculateStudentGPA(
    IN p_student_id INT,
    OUT p_gpa DECIMAL(4,2)
)
BEGIN
    SELECT AVG(grade) INTO p_gpa
    FROM enrollments
    WHERE student_id = p_student_id
      AND status = 'COMPLETED'
      AND grade IS NOT NULL;

    IF p_gpa IS NULL THEN
        SET p_gpa = 0.00;
    END IF;
END //

-- Procedure thống kê khóa học của giảng viên
CREATE PROCEDURE GetTeacherStatistics(
    IN p_teacher_id INT,
    OUT p_total_courses INT,
    OUT p_active_courses INT,
    OUT p_total_students INT
)
BEGIN
    SELECT COUNT(*) INTO p_total_courses
    FROM courses
    WHERE teacher_id = p_teacher_id AND status != 'DELETED';

    SELECT COUNT(*) INTO p_active_courses
    FROM courses
    WHERE teacher_id = p_teacher_id AND status = 'ACTIVE';

    SELECT COUNT(DISTINCT e.student_id) INTO p_total_students
    FROM courses c
             INNER JOIN enrollments e ON c.course_id = e.course_id
    WHERE c.teacher_id = p_teacher_id AND e.status = 'ACTIVE';

    IF p_total_students IS NULL THEN
        SET p_total_students = 0;
    END IF;
END //

DELIMITER ;

-- Triggers

-- Trigger cập nhật thời gian sửa đổi
DELIMITER //

CREATE TRIGGER update_user_timestamp
    BEFORE UPDATE ON users
    FOR EACH ROW
    SET NEW.updated_at = NOW() //

CREATE TRIGGER update_course_timestamp
    BEFORE UPDATE ON courses
    FOR EACH ROW
    SET NEW.updated_at = NOW() //

CREATE TRIGGER update_enrollment_timestamp
    BEFORE UPDATE ON enrollments
    FOR EACH ROW
    SET NEW.updated_at = NOW() //

DELIMITER ;

-- Indexes bổ sung cho hiệu suất
CREATE INDEX idx_courses_teacher_status ON courses(teacher_id, status);
CREATE INDEX idx_enrollments_student_status ON enrollments(student_id, status);
CREATE INDEX idx_enrollments_course_status ON enrollments(course_id, status);
CREATE INDEX idx_assignments_course_status ON assignments(course_id, status);
CREATE INDEX idx_lessons_course_order ON lessons(course_id, lesson_order);

-- Bảng audit log (tùy chọn)
CREATE TABLE audit_log (
                           log_id INT AUTO_INCREMENT PRIMARY KEY,
                           table_name VARCHAR(50) NOT NULL,
                           operation VARCHAR(10) NOT NULL,
                           record_id INT NOT NULL,
                           old_values JSON,
                           new_values JSON,
                           user_id INT,
                           timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                           INDEX idx_table_operation (table_name, operation),
                           INDEX idx_timestamp (timestamp),
                           INDEX idx_user_id (user_id)
);