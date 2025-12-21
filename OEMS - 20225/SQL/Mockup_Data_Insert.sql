-- 1. Initial Roles (Skip if already inserted)--
-- INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN'), ('ROLE_TEACHER'), ('ROLE_STUDENT');

-- 2. Mock Users (Passwords are 'password' - you should use BCrypt in Spring Boot)--
INSERT INTO users (username, password, email, full_name, preferred_lang) VALUES
('admin_sok', 'password', 'sok.ly@school.kh', 'Sok Ly (សុខ លី)', 'en'),
('teacher_chen', 'password', 'chen.wei@school.kh', 'Chen Wei (陈伟)', 'zh'),
('teacher_reach', 'password', 'sophal.reach@school.kh', 'Sophal Reach (សុផល រាជ)', 'kh'),
('std_wang', 'password', 'wang.li@student.kh', 'Wang Li (王力)', 'zh'),
('std_chhay', 'password', 'chhay.lim@student.kh', 'Chhay Lim (ឆាយ លីម)', 'kh'),
('std_kim', 'password', 'kim.soth@student.kh', 'Kim Soth (គីម សុទ្ធ)', 'kh'),
('std_zhao', 'password', 'zhao.ming@student.kh', 'Zhao Ming (赵明)', 'zh'),
('std_leakena', 'password', 'leakena@student.kh', 'Leakena (លក្ខិណា)', 'kh');

-- 3. Assign Roles--
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- Admin
(2, 2), -- Teacher Chen
(3, 2), -- Teacher Reach
(4, 3), (5, 3), (6, 3), (7, 3), (8, 3); -- Students

-- 4. Subjects & Batches--
INSERT INTO subjects (code, name, description) VALUES
('ICH101', 'Chinese Intensive I (ភាសាចិនកម្រិតខ្ពស់)', 'Foundational Mandarin Chinese for Grade 10'),
('MATH10', 'Mathematics (គណិតវិទ្យា)', 'Grade 10 Algebra and Geometry');

INSERT INTO batches (name, academic_year) VALUES
('Grade 10-A', '2024-2025');

-- 5. Question Bank (Sample Chinese & Math)--
INSERT INTO question_bank (subject_id, content, question_type, difficulty, lang_code, created_by) VALUES
(1, 'What is the capital of China?', 'MCQ', 'EASY', 'en', 2),
(1, '中国的手都是哪里？', 'MCQ', 'EASY', 'zh', 2),
(2, 'Solve for x: 2x + 5 = 15', 'MCQ', 'MEDIUM', 'en', 3);

-- 6. Question Options--
INSERT INTO question_options (question_id, option_text, is_correct) VALUES
(1, 'Beijing', TRUE), (1, 'Shanghai', FALSE), (1, 'Guangzhou', FALSE),
(2, '北京', TRUE), (2, '上海', FALSE), (2, '广州', FALSE),
(3, '5', TRUE), (3, '10', FALSE), (3, '15', FALSE);

-- 7. Create an Exam--
INSERT INTO exams (subject_id, teacher_id, title, duration_minutes, start_time, end_time, passing_score, is_published) VALUES
(1, 2, 'Mid-term Chinese Language Quiz', 60, '2025-06-01 09:00:00+07', '2025-06-01 10:00:00+07', 50.00, TRUE);

-- 8. Link Questions to Exam--
INSERT INTO exam_questions (exam_id, question_id, weightage) VALUES
(1, 1, 10.0), (1, 2, 10.0);

-- 9. Mock Exam Results (For Ranking System Demo)--
-- Scores and completion times designed to test the tie-break logic--
INSERT INTO exam_results (student_id, exam_id, score, completion_time_seconds) VALUES
(4, 1, 98.50, 2700), -- Wang Li: 1st place
(5, 1, 95.00, 2400), -- Chhay Lim: 2nd place (Faster than Kim Soth)
(6, 1, 95.00, 3100), -- Kim Soth: 3rd place (Same score as Chhay, but slower)
(7, 1, 92.00, 2200), -- Zhao Ming: 4th place
(8, 1, 88.00, 3000); -- Leakena: 5th place