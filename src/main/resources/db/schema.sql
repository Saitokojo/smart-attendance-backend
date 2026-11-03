-- ====================================================================
-- SMART & SECURE ATTENDANCE SYSTEM - DATABASE INITIALIZATION SCRIPT
-- Auto-executed by Docker on container startup (/docker-entrypoint-initdb.d/)
-- ====================================================================

-- 1. ROLES TABLE
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL, -- 'STUDENT', 'TEACHER', 'ADMIN'
    description TEXT
);

-- 2. USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    role_id INT NOT NULL REFERENCES roles(id),
    student_code VARCHAR(50) UNIQUE,
    employee_code VARCHAR(50) UNIQUE,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(30),
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(30) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. DEVICES TABLE (Device Binding)
CREATE TABLE IF NOT EXISTS devices (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_uuid VARCHAR(255) UNIQUE NOT NULL,
    device_name VARCHAR(100),
    platform VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    registered_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. ROOMS TABLE (Geofence & Multi-floor)
CREATE TABLE IF NOT EXISTS rooms (
    id SERIAL PRIMARY KEY,
    room_code VARCHAR(50) UNIQUE NOT NULL,
    building_name VARCHAR(100) NOT NULL,
    floor_number INT NOT NULL DEFAULT 1,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    geofence_radius DECIMAL(5, 2) DEFAULT 40.00,
    status VARCHAR(30) DEFAULT 'ACTIVE'
);

-- 5. CLASSES TABLE (with Invite Code)
CREATE TABLE IF NOT EXISTS classes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    teacher_id BIGINT NOT NULL REFERENCES users(id),
    invite_code VARCHAR(10) UNIQUE NOT NULL,
    is_invite_open BOOLEAN DEFAULT TRUE,
    invite_expires_at TIMESTAMP WITH TIME ZONE NULL,
    max_students INT DEFAULT 60,
    status VARCHAR(30) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6. CLASS_STUDENTS TABLE (Enrollment)
CREATE TABLE IF NOT EXISTS class_students (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    student_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(30) DEFAULT 'ENROLLED',
    enrolled_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_student_class UNIQUE(class_id, student_id)
);

-- 7. CLASS_SCHEDULES TABLE
CREATE TABLE IF NOT EXISTS class_schedules (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    room_id INT NOT NULL REFERENCES rooms(id),
    day_of_week INT NOT NULL, -- 1: Monday, 7: Sunday
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(30) DEFAULT 'ACTIVE'
);

-- 8. ATTENDANCE_SESSIONS TABLE
CREATE TABLE IF NOT EXISTS attendance_sessions (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT NOT NULL REFERENCES classes(id),
    teacher_id BIGINT NOT NULL REFERENCES users(id),
    room_id INT NOT NULL REFERENCES rooms(id),
    schedule_id BIGINT REFERENCES class_schedules(id),
    session_date DATE NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE,
    late_threshold_minutes INT DEFAULT 15,
    qr_secret_salt VARCHAR(100) NOT NULL,
    status VARCHAR(30) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP WITH TIME ZONE
);

-- 9. ATTENDANCE_RECORDS TABLE (7-Factor Verification)
CREATE TABLE IF NOT EXISTS attendance_records (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES attendance_sessions(id),
    student_id BIGINT NOT NULL REFERENCES users(id),
    status VARCHAR(30) NOT NULL, -- 'PRESENT', 'LATE', 'ABSENT', 'EXCUSED', 'N/A'
    check_in_time TIMESTAMP WITH TIME ZONE,
    client_latitude DECIMAL(10, 8),
    client_longitude DECIMAL(11, 8),
    client_ip VARCHAR(50),
    device_id BIGINT REFERENCES devices(id),
    biometric_verified BOOLEAN DEFAULT FALSE,
    wifi_ip_verified BOOLEAN DEFAULT FALSE,
    location_verified BOOLEAN DEFAULT FALSE,
    qr_verified BOOLEAN DEFAULT FALSE,
    is_manual_override BOOLEAN DEFAULT FALSE,
    override_by BIGINT REFERENCES users(id),
    override_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_session_student UNIQUE(session_id, student_id)
);

-- 10. LEAVE_REQUESTS TABLE
CREATE TABLE IF NOT EXISTS leave_requests (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES users(id),
    class_id BIGINT NOT NULL REFERENCES classes(id),
    session_id BIGINT REFERENCES attendance_sessions(id),
    date_from DATE NOT NULL,
    date_to DATE NOT NULL,
    reason VARCHAR(100) NOT NULL,
    description TEXT,
    attachment_path VARCHAR(255),
    status VARCHAR(30) DEFAULT 'PENDING',
    reviewed_by BIGINT REFERENCES users(id),
    reviewed_at TIMESTAMP WITH TIME ZONE,
    review_comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 11. TEACHER_ABSENCE_REPORTS TABLE
CREATE TABLE IF NOT EXISTS teacher_absence_reports (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES users(id),
    session_id BIGINT NOT NULL REFERENCES attendance_sessions(id),
    reported_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    client_latitude DECIMAL(10, 8),
    client_longitude DECIMAL(11, 8),
    description TEXT,
    status VARCHAR(30) DEFAULT 'PENDING',
    reviewed_by BIGINT REFERENCES users(id),
    reviewed_at TIMESTAMP WITH TIME ZONE,
    review_comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 12. ATTENDANCE_DISPUTES TABLE
CREATE TABLE IF NOT EXISTS attendance_disputes (
    id BIGSERIAL PRIMARY KEY,
    attendance_record_id BIGINT NOT NULL REFERENCES attendance_records(id),
    student_id BIGINT NOT NULL REFERENCES users(id),
    reason TEXT NOT NULL,
    evidence_path VARCHAR(255),
    status VARCHAR(30) DEFAULT 'PENDING',
    reviewed_by BIGINT REFERENCES users(id),
    reviewed_at TIMESTAMP WITH TIME ZONE,
    review_comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 13. AUDIT_LOGS TABLE (Immutable Record)
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    old_value JSONB,
    new_value JSONB,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 14. NOTIFICATIONS TABLE
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- INDEXES
CREATE INDEX IF NOT EXISTS idx_classes_invite_code ON classes(invite_code);
CREATE INDEX IF NOT EXISTS idx_attendance_records_session ON attendance_records(session_id);
CREATE INDEX IF NOT EXISTS idx_attendance_records_student ON attendance_records(student_id);
CREATE INDEX IF NOT EXISTS idx_attendance_sessions_class ON attendance_sessions(class_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);

-- ====================================================================
-- INITIAL SEED DATA
-- ====================================================================

-- Insert Default Roles
INSERT INTO roles (id, name, description) VALUES
(1, 'STUDENT', 'University Student Role'),
(2, 'TEACHER', 'Lecturer / Professor Role'),
(3, 'ADMIN', 'System Administrator Role')
ON CONFLICT (id) DO NOTHING;

-- Insert Sample Rooms (Build Bright University - Sample Campus)
INSERT INTO rooms (id, room_code, building_name, floor_number, latitude, longitude, geofence_radius) VALUES
(1, 'ROOM-402', 'Building B', 4, 11.55640000, 104.92820000, 40.00),
(2, 'ROOM-201', 'Building A', 2, 11.55650000, 104.92810000, 40.00)
ON CONFLICT (id) DO NOTHING;

-- Insert Sample Administrator Account (Password: admin123 hashed via bcrypt)
INSERT INTO users (id, role_id, employee_code, name, email, phone, password_hash, status) VALUES
(1, 3, 'ADMIN-001', 'System Administrator', 'admin@bbu.edu.kh', '012345678', '$2a$10$w8LqUa245H4F3U7G8p2VYe1.c9vK8hGgT1lWqFm/x4j5Z6k7l8m9n', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;
