-- Migration script to drop unused tables and simplify schema
-- V2__Drop_Unused_Tables.sql

USE coursemanagement;

-- Drop unused tables
DROP TABLE IF EXISTS assignment_submissions;
DROP TABLE IF EXISTS assignments;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS course_materials;
DROP TABLE IF EXISTS audit_log;

-- Drop views that reference dropped tables
DROP VIEW IF EXISTS pending_assignments;

-- Remove unnecessary columns from existing tables
ALTER TABLE users
DROP COLUMN IF EXISTS phone,
DROP COLUMN IF EXISTS address,
DROP COLUMN IF EXISTS updated_at;

-- Simplify status enum for users (remove SUSPENDED)
ALTER TABLE users MODIFY COLUMN status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE';

-- Remove unnecessary columns from courses
ALTER TABLE courses
DROP COLUMN IF EXISTS credits,
DROP COLUMN IF EXISTS start_date,
DROP COLUMN IF EXISTS end_date,
DROP COLUMN IF EXISTS updated_at;

-- Simplify status enum for courses (remove DELETED)
ALTER TABLE courses MODIFY COLUMN status ENUM('ACTIVE', 'INACTIVE', 'COMPLETED') DEFAULT 'ACTIVE';

-- Remove unnecessary columns from enrollments
ALTER TABLE enrollments
DROP COLUMN IF EXISTS completion_date,
DROP COLUMN IF EXISTS created_at,
DROP COLUMN IF EXISTS updated_at;

-- Simplify status enum for enrollments (remove SUSPENDED)
ALTER TABLE enrollments MODIFY COLUMN status ENUM('ACTIVE', 'COMPLETED', 'DROPPED') DEFAULT 'ACTIVE';

-- Remove unnecessary columns from lessons
ALTER TABLE lessons
DROP COLUMN IF EXISTS video_url,
DROP COLUMN IF EXISTS document_url,
DROP COLUMN IF EXISTS duration_minutes,
DROP COLUMN IF EXISTS updated_at;

-- Simplify status enum for lessons (remove DRAFT)
ALTER TABLE lessons MODIFY COLUMN status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE';

-- Remove unnecessary columns from announcements
ALTER TABLE announcements
DROP COLUMN IF EXISTS priority,
DROP COLUMN IF EXISTS status,
DROP COLUMN IF EXISTS updated_at;

-- Update stored procedures to match simplified schema
DROP PROCEDURE IF EXISTS GetTeacherStatistics;

-- Recreate simplified stored procedure
DELIMITER //

CREATE PROCEDURE GetTeacherStatistics(
    IN p_teacher_id INT,
    OUT p_total_courses INT,
    OUT p_active_courses INT,
    OUT p_total_students INT
)
BEGIN
SELECT COUNT(*) INTO p_total_courses
FROM courses
WHERE teacher_id = p_teacher_id AND status IN ('ACTIVE', 'COMPLETED');

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

-- Drop unnecessary triggers
DROP TRIGGER IF EXISTS update_user_timestamp;
DROP TRIGGER IF EXISTS update_course_timestamp;
DROP TRIGGER IF EXISTS update_enrollment_timestamp;

-- Clean up any orphaned data (optional)
-- DELETE FROM announcements WHERE course_id NOT IN (SELECT course_id FROM courses);
-- DELETE FROM lessons WHERE course_id NOT IN (SELECT course_id FROM courses);
-- DELETE FROM enrollments WHERE course_id NOT IN (SELECT course_id FROM courses);

-- Add some helpful comments
ALTER TABLE users COMMENT = 'Simplified user table with basic information only';
ALTER TABLE courses COMMENT = 'Simplified course table without time constraints';
ALTER TABLE enrollments COMMENT = 'Simplified enrollment table with grade tracking';
ALTER TABLE lessons COMMENT = 'Simplified lesson table with basic content';
ALTER TABLE announcements COMMENT = 'Simplified announcement table';

-- Complete migration script for all new features
-- Run this script to update your existing database

