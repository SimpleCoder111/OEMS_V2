-- 1. User & Role Management--
CREATE TABLE roles (
 id SERIAL PRIMARY KEY,
 role_name VARCHAR(20) UNIQUE NOT NULL
);

INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN'), ('ROLE_TEACHER'), ('ROLE_STUDENT');

CREATE TABLE users (
 id BIGSERIAL PRIMARY KEY,
 username VARCHAR(50) UNIQUE NOT NULL,
 password VARCHAR(255) NOT NULL,
 email VARCHAR(100) UNIQUE,
 full_name VARCHAR(100) NOT NULL, -- Supports Khmer/Chinese Unicode
 preferred_lang VARCHAR(5) DEFAULT 'en', -- 'kh', 'zh', 'en'
 status VARCHAR(20) DEFAULT 'ACTIVE',
 created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
 user_id BIGINT REFERENCES users(id),
 role_id INT REFERENCES roles(id),
 PRIMARY KEY (user_id, role_id)
);

-- 2. Academic Structure--
CREATE TABLE subjects (
 id BIGSERIAL PRIMARY KEY,
 code VARCHAR(20) UNIQUE NOT NULL,
 name VARCHAR(100) NOT NULL,
 description TEXT
);

CREATE TABLE batches (
 id BIGSERIAL PRIMARY KEY,
 name VARCHAR(50) NOT NULL,
 academic_year VARCHAR(10) NOT NULL
);

-- 3. Question Bank--
CREATE TABLE question_bank (
 id BIGSERIAL PRIMARY KEY,
 subject_id BIGINT REFERENCES subjects(id),
 content TEXT NOT NULL,
 question_type VARCHAR(20) NOT NULL, -- 'MCQ', 'TRUE_FALSE', 'DESCRIPTIVE'
 difficulty VARCHAR(10), -- 'EASY', 'MEDIUM', 'HARD'
 lang_code VARCHAR(5) NOT NULL, -- 'kh', 'zh', 'en'
 created_by BIGINT REFERENCES users(id)
);

CREATE TABLE question_options (
 id BIGSERIAL PRIMARY KEY,
 question_id BIGINT REFERENCES question_bank(id) ON DELETE CASCADE,
 option_text TEXT NOT NULL,
 is_correct BOOLEAN DEFAULT FALSE
);

-- 4. Exam Management--
CREATE TABLE exams (
 id BIGSERIAL PRIMARY KEY,
 subject_id BIGINT REFERENCES subjects(id),
 teacher_id BIGINT REFERENCES users(id),
 title VARCHAR(200) NOT NULL,
 duration_minutes INT NOT NULL,
 start_time TIMESTAMPTZ,
 end_time TIMESTAMPTZ,
 passing_score DECIMAL(5,2),
 is_published BOOLEAN DEFAULT FALSE
);

CREATE TABLE exam_questions (
 exam_id BIGINT REFERENCES exams(id),
 question_id BIGINT REFERENCES question_bank(id),
 weightage DECIMAL(5,2) DEFAULT 1.0,
 PRIMARY KEY (exam_id, question_id)
);

-- 5. Session Recovery & Results--
CREATE TABLE exam_sessions (
 id BIGSERIAL PRIMARY KEY,
 student_id BIGINT REFERENCES users(id),
 exam_id BIGINT REFERENCES exams(id),
 status VARCHAR(20) DEFAULT 'IN_PROGRESS', -- 'IN_PROGRESS', 'SUBMITTED', 'EXPIRED'
 start_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
 last_heartbeat TIMESTAMPTZ, -- Updated every minute for recovery
 progress_data JSONB, -- Stores unsaved answers as JSON
 ip_address VARCHAR(45) -- For IP Tracking security
);

CREATE TABLE exam_results (
 id BIGSERIAL PRIMARY KEY,
 student_id BIGINT REFERENCES users(id),
 exam_id BIGINT REFERENCES exams(id),
 score DECIMAL(5,2),
 completion_time_seconds INT,
 graded_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
 UNIQUE(student_id, exam_id)
);

-- 6. Indexes for Ranking Performance--
CREATE INDEX idx_exam_results_score ON exam_results (exam_id, score DESC, completion_time_seconds ASC);