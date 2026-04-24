CREATE DATABASE IF NOT EXISTS library_booking;
USE library_booking;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(80) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS rooms (
    id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(20) NOT NULL UNIQUE,
    capacity INT NOT NULL,
    location VARCHAR(100) NOT NULL,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS booking_requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    room_id INT NOT NULL,
    booking_date DATE NOT NULL,
    time_slot VARCHAR(40) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    admin_note VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_booking_room FOREIGN KEY (room_id) REFERENCES rooms(id)
);

INSERT INTO users (student_id, name, email, password_hash, role)
VALUES
('ADMIN001', 'Library Admin', 'admin@college.edu', '$2a$10$LQK2fQ7nq4UUp7QfHWi2JuSkQccRp0V2K2WUPspTQn9HAIbiVfxhC', 'ADMIN')
ON DUPLICATE KEY UPDATE name = VALUES(name);
-- Password: admin123

INSERT INTO rooms (room_number, capacity, location) VALUES
('SR-101', 6, 'Block A - Ground Floor'),
('SR-102', 4, 'Block A - First Floor'),
('SR-201', 8, 'Block B - Ground Floor'),
('SR-202', 10, 'Block B - First Floor')
ON DUPLICATE KEY UPDATE capacity = VALUES(capacity), location = VALUES(location);
