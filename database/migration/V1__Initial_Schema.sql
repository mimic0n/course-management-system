-- Bước 1: Tạo cơ sở dữ liệu nếu nó chưa tồn tại
CREATE DATABASE IF NOT EXISTS coursemanagement;

-- Bước 2: Sử dụng cơ sở dữ liệu vừa tạo
USE coursemanagement;

-- Bước 3: Tạo bảng 'users' để lưu trữ thông tin người dùng
-- Bảng này tương ứng với class 'User' của bạn
CREATE TABLE IF NOT EXISTS users (
                                     user_id INT AUTO_INCREMENT PRIMARY KEY,  -- Khóa chính, tự động tăng
                                     username VARCHAR(50) NOT NULL UNIQUE,      -- Tên đăng nhập, không được trùng
                                     email VARCHAR(100) NOT NULL UNIQUE,     -- Email, không được trùng
                                     password VARCHAR(255) NOT NULL,            -- Lưu mật khẩu đã được băm (hashed)
                                     full_name VARCHAR(100) NOT NULL,           -- Tên đầy đủ
                                     role VARCHAR(20) NOT NULL DEFAULT 'USER',  -- Vai trò (ví dụ: USER, ADMIN), mặc định là USER
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Tự động ghi lại thời gian tạo
);

-- Bước 4: Tạo bảng 'courses' để lưu trữ thông tin khóa học
-- Bảng này tương ứng với class 'Course' của bạn
CREATE TABLE IF NOT EXISTS courses (
                                       course_id INT AUTO_INCREMENT PRIMARY KEY, -- Khóa chính, tự động tăng
                                       title VARCHAR(255) NOT NULL,               -- Tiêu đề khóa học
                                       description TEXT,                          -- Mô tả chi tiết, dùng TEXT cho nội dung dài
                                       price DECIMAL(10, 2) NOT NULL DEFAULT 0.00, -- Giá tiền, DECIMAL tốt hơn cho tiền tệ
                                       level VARCHAR(50),                         -- Cấp độ (ví dụ: Beginner, Intermediate)
                                       category VARCHAR(100),                     -- Thể loại (ví dụ: Programming, Design)
                                       image_url VARCHAR(255),                    -- Đường dẫn đến hình ảnh khóa học
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Tự động ghi lại thời gian tạo
);

-- Bước 5: Tạo bảng 'enrollments' để quản lý việc đăng ký khóa học
-- Bảng này là bảng trung gian, thể hiện mối quan hệ nhiều-nhiều giữa 'users' và 'courses'
-- Tương ứng với class 'Enrollment' của bạn
CREATE TABLE IF NOT EXISTS enrollments (
                                           enrollment_id INT AUTO_INCREMENT PRIMARY KEY, -- Khóa chính của bảng này
                                           user_id INT NOT NULL,                          -- Khóa ngoại, tham chiếu đến bảng 'users'
                                           course_id INT NOT NULL,                        -- Khóa ngoại, tham chiếu đến bảng 'courses'
                                           enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Ngày đăng ký, mặc định là thời gian hiện tại
                                           status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',  -- Trạng thái (ví dụ: ACTIVE, COMPLETED, CANCELLED)
                                           progress INT NOT NULL DEFAULT 0,               -- Tiến độ hoàn thành (tính bằng %), mặc định là 0

    -- Định nghĩa các khóa ngoại để đảm bảo toàn vẹn dữ liệu
                                           FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    -- ON DELETE CASCADE: Nếu một user bị xóa, tất cả các lượt đăng ký của user đó cũng sẽ bị xóa

                                           FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    -- ON DELETE CASCADE: Nếu một khóa học bị xóa, tất cả các lượt đăng ký vào khóa học đó cũng sẽ bị xóa

    -- Đảm bảo một người dùng chỉ có thể đăng ký một khóa học một lần duy nhất
                                           UNIQUE (user_id, course_id)
);

-- Bước 6: Thêm các chỉ mục (INDEX) để tăng tốc độ truy vấn
-- Việc này rất quan trọng cho các hệ thống có nhiều dữ liệu
CREATE INDEX idx_user_id ON enrollments(user_id);
CREATE INDEX idx_course_id ON enrollments(course_id);
CREATE INDEX idx_course_category ON courses(category);
CREATE INDEX idx_course_level ON courses(level);

-- Xóa dữ liệu cũ trong các bảng để tránh trùng lặp nếu chạy lại file này
-- Thứ tự xóa rất quan trọng: phải xóa ở bảng con (có khóa ngoại) trước
DELETE FROM enrollments;
DELETE FROM courses;
DELETE FROM users;

-- Reset giá trị AUTO_INCREMENT về 1 (tùy chọn, nhưng giúp ID bắt đầu lại từ đầu)
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE courses AUTO_INCREMENT = 1;
ALTER TABLE enrollments AUTO_INCREMENT = 1;


-- =============================================
-- DỮ LIỆU MẪU CHO BẢNG `users`
-- =============================================
INSERT INTO users (username, email, password, full_name, role) VALUES
                                                                   ('admin', 'admin@course.com', 'hashed_admin_password', 'Quản Trị Viên', 'ADMIN'),
                                                                   ('thanhchien', 'thanhchien@email.com', 'hashed_password_1', 'Nguyễn Thanh Chiến', 'USER'),
                                                                   ('kimthong', 'kimthong@email.com', 'hashed_password_2', 'Huỳnh Kim Thống', 'USER'),
                                                                   ('tanbao', 'tanbao@email.com', 'hashed_password_3', 'Hồ Tấn Bảo', 'USER');


-- =============================================
-- DỮ LIỆU MẪU CHO BẢNG `courses`
-- =============================================
INSERT INTO courses (title, description, price, level, category, image_url) VALUES
                                                                                ('Lập trình Java cơ bản', 'Khóa học này cung cấp kiến thức nền tảng vững chắc về ngôn ngữ lập trình Java, từ cú pháp cơ bản đến lập trình hướng đối tượng.', 49.99, 'Beginner', 'Programming', 'images/java_basic.png'),
                                                                                ('Thiết kế UI/UX cho người mới bắt đầu', 'Tìm hiểu các nguyên tắc cơ bản của thiết kế giao diện người dùng (UI) và trải nghiệm người dùng (UX). Thực hành với các công cụ như Figma.', 79.50, 'Beginner', 'Design', 'images/ui_ux.png'),
                                                                                ('Quản lý dự án chuyên nghiệp (PMP)', 'Trang bị các kỹ năng và kiến thức cần thiết để quản lý dự án hiệu quả theo chuẩn quốc tế PMP.', 199.00, 'Advanced', 'Business', 'images/pmp.png'),
                                                                                ('Khoa học dữ liệu với Python', 'Khám phá thế giới khoa học dữ liệu. Học cách sử dụng các thư viện Python như Pandas, NumPy, và Matplotlib để phân tích và trực quan hóa dữ liệu.', 149.99, 'Intermediate', 'Data Science', 'images/data_science.png'),
                                                                                ('Marketing kỹ thuật số 101', 'Tổng quan về các kênh marketing kỹ thuật số bao gồm SEO, SEM, Social Media Marketing và Content Marketing.', 69.00, 'Beginner', 'Marketing', 'images/digital_marketing.png'),
                                                                                ('Lập trình Java nâng cao', 'Đi sâu vào các chủ đề phức tạp như Lập trình đa luồng (Multithreading), Collections Framework, và kết nối cơ sở dữ liệu với JDBC.', 99.99, 'Advanced', 'Programming', 'images/java_advanced.png');


-- =============================================
-- DỮ LIỆU MẪU CHO BẢNG `enrollments`
-- =============================================
-- Bảng này liên kết users và courses.
-- user_id và course_id phải tương ứng với các ID đã được tạo ở trên.

INSERT INTO enrollments (user_id, course_id, status, progress) VALUES
-- Người dùng 'thanhchien' (user_id=2)
(2, 1, 'ACTIVE', 50),   -- Đã đăng ký khóa 'Lập trình Java cơ bản' và hoàn thành 50%
(2, 4, 'ACTIVE', 25),   -- Đã đăng ký khóa 'Khoa học dữ liệu với Python' và hoàn thành 25%

-- Người dùng 'kimthong' (user_id=3)
(3, 2, 'COMPLETED', 100), -- Đã đăng ký và hoàn thành khóa 'Thiết kế UI/UX'
(3, 5, 'ACTIVE', 10),    -- Đã đăng ký khóa 'Marketing kỹ thuật số 101'

-- Người dùng 'tanbao' (user_id=4)
(4, 1, 'ACTIVE', 75),    -- Đã đăng ký khóa 'Lập trình Java cơ bản'
(4, 6, 'ACTIVE', 15);    -- Đã đăng ký khóa 'Lập trình Java nâng cao'