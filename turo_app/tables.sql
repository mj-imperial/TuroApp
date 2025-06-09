-- Disable foreign key checks for the entire script
SET FOREIGN_KEY_CHECKS = 0;

-- Lookup Tables
CREATE TABLE UserRole (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO UserRole (role_id, role_name)
VALUES(1, 'STUDENT'), (2,'TEACHER'), (3, 'ADMIN');

CREATE TABLE QuestionType (
    type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO QuestionType (type_id, type_name)
VALUES(1, 'MULTIPLE_CHOICE'), (2, 'SHORT_ANSWER');


CREATE TABLE AchievementConditionType (
    condition_type_id INT AUTO_INCREMENT PRIMARY KEY,
    condition_name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO AchievementConditionType (condition_type_id, condition_name)
VALUES(1, 'POINTS'), (2, 'MODULE_COMPLETION'), (3, 'QUIZ_SCORE'), (4, 'ACTIVITY_COMPLETION'), (5, 'FIRST_ASSESSMENT'), (6, 'GRADE_ABOVE'), (7, 'BADGES_EARNED'), (8, 'LEADERBOARD_RANK');

CREATE TABLE EventType (
    event_type_id INT AUTO_INCREMENT PRIMARY KEY,
    event_type_name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO EventType (event_type_id, event_type_name)
VALUES(1, 'MODULE'), (2, 'SHORT_QUIZ'), (3, 'PRACTICE_QUIZ'), (4, 'LONG_QUIZ'), (5, 'CATCH_UP'), (6, 'SCREENING_EXAM');

CREATE TABLE ContentType (
    content_type_id INT AUTO_INCREMENT PRIMARY KEY,
    content_type_name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO ContentType (content_type_id, content_type_name)
VALUES(1, 'TEXT'), (2, 'PDF'), (3, 'VIDEO');

CREATE TABLE QuizType (
    quiz_type_id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_type_name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO QuizType (quiz_type_id, quiz_type_name)
VALUES(1, 'SHORT'), (2, 'PRACTICE'), (3, 'LONG'), (4, 'SCREENING_EXAM');

CREATE TABLE ScreeningTier (
    tier_id INT AUTO_INCREMENT PRIMARY KEY,
    tier_name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO ScreeningTier (tier_id, tier_name)
VALUES(1, 'TIER_1'), (2, 'TIER_2'), (3, 'TIER_3');

-- Main Tables

CREATE TABLE User (
    user_id VARCHAR(255) PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    profile_pic VARCHAR(2048),
    agreed_to_terms BOOLEAN NOT NULL DEFAULT FALSE,
    requires_password_change BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (role_id) REFERENCES UserRole(role_id)
);

CREATE TABLE Student (
    user_id VARCHAR(255) PRIMARY KEY,
    isCatchUp BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);

CREATE TABLE Teacher (
    user_id VARCHAR(255) PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);

CREATE TABLE Admin (
    user_id VARCHAR(255) PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);

CREATE TABLE Course (
    course_id VARCHAR(255) PRIMARY KEY,
    course_code VARCHAR(255) NOT NULL UNIQUE,
    course_name VARCHAR(255) NOT NULL,
    teacher_id VARCHAR(255),
    course_picture VARCHAR(255) NOT NULL,
    course_description TEXT,
    start_date DATE,
    end_date DATE,
    FOREIGN KEY (teacher_id) REFERENCES Teacher(user_id) ON DELETE SET NULL
);

CREATE TABLE Enrollment (
    enrollment_id VARCHAR(255) PRIMARY KEY,
    student_id VARCHAR(255) NOT NULL,
    course_id VARCHAR(255) NOT NULL,
    enrollment_date DATE NOT NULL,
    isEnrolled BOOLEAN NOT NULL DEFAULT FALSE,
    finalGrade DOUBLE NOT NULL DEFAULT 0.0,
    FOREIGN KEY (student_id) REFERENCES Student(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES Course(course_id) ON DELETE CASCADE
);

CREATE TABLE Module (
    module_id VARCHAR(255) PRIMARY KEY,
    course_id VARCHAR(255) NOT NULL,
    module_name VARCHAR(255) NOT NULL,
    module_description TEXT,
    FOREIGN KEY (course_id) REFERENCES Course(course_id) ON DELETE CASCADE
);

CREATE TABLE Activity (
    activity_id VARCHAR(255) PRIMARY KEY,
    module_id VARCHAR(255) NOT NULL,
    activity_type ENUM('TUTORIAL', 'QUIZ', 'LECTURE') NOT NULL,
    activity_name VARCHAR(255) NOT NULL,
    activity_description TEXT,
    unlock_date DATETIME NOT NULL,
    deadline_date DATETIME,
    FOREIGN KEY (module_id) REFERENCES Module(module_id) ON DELETE CASCADE
);

CREATE TABLE CalendarEvent (
    event_id VARCHAR(255) PRIMARY KEY,  
    title VARCHAR(255) NOT NULL,       
    description TEXT,
    date DATETIME NOT NULL,
    event_type_id INT NOT NULL,
    is_urgent BOOLEAN DEFAULT FALSE,
    location VARCHAR(255) NOT NULL,
    FOREIGN KEY (event_type_id) REFERENCES EventType(event_type_id),
    FOREIGN KEY (event_id) REFERENCES Activity(activity_id) ON DELETE CASCADE
);

CREATE TABLE Quiz (
    activity_id VARCHAR(255) PRIMARY KEY,
    number_of_attempts INT NOT NULL,
    quiz_type_id INT NOT NULL,
    time_limit INT NOT NULL,
    number_of_questions INT NOT NULL,
    overall_points INT NOT NULL,
    has_answers_shown BOOLEAN NOT NULL,
    FOREIGN KEY (activity_id) REFERENCES Activity(activity_id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_type_id) REFERENCES QuizType(quiz_type_id)
);

CREATE TABLE Tutorial (
    activity_id VARCHAR(255) PRIMARY KEY,
    content_type_id INT NOT NULL,
    video_url VARCHAR(2048),
    FOREIGN KEY (activity_id) REFERENCES Activity(activity_id) ON DELETE CASCADE,
    FOREIGN KEY (content_type_id) REFERENCES ContentType(content_type_id)
);

CREATE TABLE Lecture (
    activity_id VARCHAR(255) PRIMARY KEY,
    content_type_id INT NOT NULL,
    video_url VARCHAR(2048),
    pdf_url VARCHAR(2048),
    doc_url VARCHAR(2048),
    text_body TEXT,
    FOREIGN KEY (activity_id) REFERENCES Activity(activity_id) ON DELETE CASCADE,
    FOREIGN KEY (content_type_id) REFERENCES ContentType(content_type_id)
);

CREATE TABLE Question (
    question_id VARCHAR(255) PRIMARY KEY,
    question_text TEXT NOT NULL,
    question_type_id INT NOT NULL,
    score INT NOT NULL,
    activity_id VARCHAR(255),
    FOREIGN KEY (activity_id) REFERENCES Activity(activity_id) ON DELETE CASCADE,
    FOREIGN KEY (question_type_id) REFERENCES QuestionType(type_id)
);

CREATE TABLE Options (
    option_id VARCHAR(255) PRIMARY KEY,
    question_id VARCHAR(255) NOT NULL,
    option_text VARCHAR(500) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    FOREIGN KEY (question_id) REFERENCES Question(question_id) ON DELETE CASCADE
);

CREATE TABLE AssessmentResult (
    result_id VARCHAR(255) PRIMARY KEY,
    student_id VARCHAR(255) NOT NULL,
    module_id VARCHAR(255) NOT NULL,
    activity_id VARCHAR(255) NOT NULL,
    score_percentage DOUBLE NOT NULL,
    date_taken DATETIME NOT NULL,
    attempt_number INT NOT NULL,
    tier_level_id INT,
    earned_points INT NOT NULL,
    is_kept BOOLEAN NOT NULL, 
    FOREIGN KEY (student_id) REFERENCES Student(user_id) ON DELETE CASCADE,
    FOREIGN KEY (module_id) REFERENCES Module(module_id),
    FOREIGN KEY (activity_id) REFERENCES Activity(activity_id),
    FOREIGN KEY (tier_level_id) REFERENCES ScreeningTier(tier_id)
);

CREATE TABLE AssessmentResult_Answers (
    result_id VARCHAR(255) NOT NULL,
    question_id VARCHAR(255) NOT NULL,
    option_id VARCHAR(255) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    PRIMARY KEY (result_id, question_id),
    FOREIGN KEY (result_id) REFERENCES AssessmentResult(result_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES Question(question_id),
    FOREIGN KEY (option_id) REFERENCES Options(option_id)
);

CREATE TABLE StudentProgress (
    student_id VARCHAR(255) NOT NULL,
    course_id VARCHAR(255) NOT NULL,
    total_points INT NOT NULL,
    average_score DOUBLE NOT NULL,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES Student(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES Course(course_id) ON DELETE CASCADE
);

CREATE TABLE ModuleProgress (
    student_id VARCHAR(255) NOT NULL,
    module_id VARCHAR(255) NOT NULL,
    is_completed BOOLEAN NOT NULL,
    average_score DOUBLE NOT NULL,
    tier_passed_id INT,
    screening_exam_attempts INT NOT NULL DEFAULT 0,
    screening_exam_failed_count INT NOT NULL DEFAULT 0,
    PRIMARY KEY (student_id, module_id),
    FOREIGN KEY (student_id) REFERENCES Student(user_id) ON DELETE CASCADE,
    FOREIGN KEY (module_id) REFERENCES Module(module_id),
    FOREIGN KEY (tier_passed_id) REFERENCES ScreeningTier(tier_id)
);

CREATE TABLE Inbox (
  inbox_id VARCHAR(255) PRIMARY KEY,
  unread_count INT NOT NULL DEFAULT 0,
  timestamp BIGINT NOT NULL
);

CREATE TABLE Message (
  message_id VARCHAR(255) PRIMARY KEY,
  inbox_id VARCHAR(255) NOT NULL,
  sender_id VARCHAR(255) NOT NULL,
  subject VARCHAR(255),
  body TEXT NOT NULL,
  timestamp BIGINT NOT NULL,
  FOREIGN KEY (inbox_id) REFERENCES Inbox(inbox_id) ON DELETE CASCADE,
  FOREIGN KEY (sender_id) REFERENCES User(user_id)
);

CREATE TABLE InboxParticipant (
  inbox_id VARCHAR(255) NOT NULL,
  participant_id VARCHAR(255) NOT NULL,
  PRIMARY KEY (inbox_id, participant_id),
  FOREIGN KEY (inbox_id) REFERENCES Inbox(inbox_id),
  FOREIGN KEY (participant_id) REFERENCES User(user_id)
);

CREATE TABLE MessageUserState (
  message_id VARCHAR(255) NOT NULL,
  user_id VARCHAR(255) NOT NULL,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (message_id, user_id),
  FOREIGN KEY (message_id) REFERENCES Message(message_id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES User(user_id)
);


CREATE TABLE Attachment (
    attachment_id VARCHAR(255) PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(2048) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(255) NOT NULL
);

CREATE TABLE MessageAttachment (
    message_id VARCHAR(255) NOT NULL,
    attachment_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (message_id, attachment_id),
    FOREIGN KEY (message_id) REFERENCES Message(message_id) ON DELETE CASCADE,
    FOREIGN KEY (attachment_id) REFERENCES Attachment(attachment_id) ON DELETE CASCADE
);

CREATE TABLE Achievements (
    achievement_id VARCHAR(255) PRIMARY KEY,
    achievement_name VARCHAR(255) NOT NULL,
    achievement_description TEXT NOT NULL,
    achievement_image VARCHAR(2048),
    condition_type_id INT NOT NULL,
    condition_value VARCHAR(255) NOT NULL,
    is_unlocked BOOLEAN NOT NULL,
    FOREIGN KEY (condition_type_id) REFERENCES AchievementConditionType(condition_type_id)
);

CREATE TABLE Badges (
    badge_id VARCHAR(255) PRIMARY KEY,
    badge_name VARCHAR(255) NOT NULL,
    badge_description TEXT NOT NULL,
    badge_image VARCHAR(2048),
    points_required INT NOT NULL,
    is_unlocked BOOLEAN NOT NULL
);

CREATE TABLE ShopItem (
    item_id VARCHAR(255) PRIMARY KEY,
    item_name VARCHAR(255) NOT NULL,
    item_description TEXT NOT NULL,
    item_picture VARCHAR(2048) NOT NULL,
    points_required INT NOT NULL
);

CREATE TABLE Student_Achievements (
    student_id VARCHAR(255) NOT NULL,
    achievement_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (student_id, achievement_id),
    FOREIGN KEY (student_id) REFERENCES Student(user_id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES Achievements(achievement_id) ON DELETE CASCADE
);

CREATE TABLE Student_Badges (
    student_id VARCHAR(255) NOT NULL,
    badge_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (student_id, badge_id),
    FOREIGN KEY (student_id) REFERENCES Student(user_id) ON DELETE CASCADE,
    FOREIGN KEY (badge_id) REFERENCES Badges(badge_id) ON DELETE CASCADE
);

CREATE TABLE Student_ShopItem (
    student_id VARCHAR(255) NOT NULL,
    item_id VARCHAR(255) NOT NULL,
    purchase_date DATETIME NOT NULL,
    PRIMARY KEY (student_id, item_id),
    FOREIGN KEY (student_id) REFERENCES Student(user_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES ShopItem(item_id)
);

CREATE TABLE password_resets(
    user_id VARCHAR(255) PRIMARY KEY,
    code_hash VARCHAR(255) NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);

-- Re-enable foreign key checks after the entire script
SET FOREIGN_KEY_CHECKS = 1;
