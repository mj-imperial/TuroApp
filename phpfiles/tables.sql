-- User Roles
CREATE TYPE UserRole AS ENUM ('STUDENT', 'TEACHER', 'ADMIN');
CREATE TABLE UserRole (
    role_id INT PRIMARY KEY,
    role_name UserRole NOT NULL UNIQUE
);


-- Question Types
CREATE TYPE QuestionType AS ENUM ('MULTIPLE_CHOICE', 'SHORT_ANSWER');
CREATE TABLE QuestionType (
    type_id INT PRIMARY KEY,
    type_name QuestionType NOT NULL UNIQUE
);

-- Achievement Condition Types
CREATE TYPE AchievementConditionType AS ENUM ('POINTS', 'MODULE_COMPLETION', 'QUIZ_SCORE', 'ACTIVITY_COMPLETION', 'FIRST_ASSESSMENT', 'GRADE_ABOVE', 'BADGES_EARNED', 'LEADERBOARD_RANK');
CREATE TABLE AchievementConditionType (
    condition_type_id INT PRIMARY KEY,
    condition_name AchievementConditionType NOT NULL UNIQUE
);

-- Event Types
CREATE TYPE EventType AS ENUM ('MODULE', 'SHORT_QUIZ', 'PRACTICE_QUIZ', 'LONG_QUIZ', 'CATCH_UP');
CREATE TABLE EventType (
    event_type_id INT PRIMARY KEY,
    event_type_name EventType NOT NULL UNIQUE
);

-- Content Types
CREATE TYPE ContentType AS ENUM ('TEXT', 'PDF', 'VIDEO');
CREATE TABLE ContentType (
    content_type_id INT PRIMARY KEY,
    content_type_name ContentType NOT NULL UNIQUE
);

-- Quiz Types
CREATE TYPE QuizType AS ENUM ('SHORT', 'PRACTICE', 'LONG');
CREATE TABLE QuizType (
    quiz_type_id INT PRIMARY KEY,
    quiz_type_name QuizType NOT NULL UNIQUE
);

-- Screening Tiers
CREATE TYPE ScreeningTier AS ENUM ('TIER_1', 'TIER_2', 'TIER_3');
CREATE TABLE ScreeningTier (
    tier_id INT PRIMARY KEY,
    tier_name ScreeningTier NOT NULL UNIQUE
);

-- Main Tables 

CREATE TABLE User (
    user_id VARCHAR(255) PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role UserRole NOT NULL,  
    profile_pic VARCHAR(2048),
    agreed_to_terms BOOLEAN NOT NULL DEFAULT FALSE,
    requires_password_change BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE Student (
    user_id VARCHAR(255) PRIMARY KEY,
    averageScore DOUBLE NOT NULL DEFAULT 0.0,
    isCatchUp BOOLEAN NOT NULL DEFAULT FALSE,
    totalPoints INT NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE Teacher (
    user_id VARCHAR(255) PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE Admin (
    user_id VARCHAR(255) PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE Course (
    course_id VARCHAR(255) PRIMARY KEY,
    course_code VARCHAR(255) NOT NULL UNIQUE,
    course_name VARCHAR(255) NOT NULL,
    teacher_id VARCHAR(255) NOT NULL,
    course_description TEXT,
    start_date DATE,
    end_date DATE,
    FOREIGN KEY (teacher_id) REFERENCES Teacher(user_id)
);

CREATE TABLE Enrollment (
    enrollment_id VARCHAR(255) PRIMARY KEY,
    student_id VARCHAR(255) NOT NULL,
    course_id VARCHAR(255) NOT NULL,
    enrollment_date DATE NOT NULL,
    isEnrolled BOOLEAN NOT NULL DEFAULT FALSE,
    finalGrade DOUBLE NOT NULL DEFAULT 0.0,
    FOREIGN KEY (student_id) REFERENCES Student(user_id),
    FOREIGN KEY (course_id) REFERENCES Course(course_id)
);

CREATE TABLE Module (
    module_id VARCHAR(255) PRIMARY KEY,
    course_id VARCHAR(255) NOT NULL,
    module_name VARCHAR(255) NOT NULL,
    module_description TEXT,
    FOREIGN KEY (course_id) REFERENCES Course(course_id)
);

CREATE TABLE Activity (
    activity_id VARCHAR(255) PRIMARY KEY,
    module_id VARCHAR(255) NOT NULL,
    activity_type ActivityType NOT NULL,  
    activity_name VARCHAR(255) NOT NULL,
    activity_description TEXT,
    unlock_date DATETIME NOT NULL,
    deadline_date DATETIME,
    event_id VARCHAR(255),
    FOREIGN KEY (module_id) REFERENCES Module(module_id),
    FOREIGN KEY (event_id) REFERENCES CalendarEvent(event_id)
);



CREATE TABLE Quiz (
    activity_id VARCHAR(255) PRIMARY KEY,
    number_of_attempts INT NOT NULL,
    quiz_type QuizType NOT NULL,  
    time_limit INT NOT NULL,
    is_passed BOOLEAN NOT NULL,
    FOREIGN KEY (activity_id) REFERENCES Activity(activity_id)
);

CREATE TABLE Tutorial (
    activity_id VARCHAR(255) PRIMARY KEY,
    content_type ContentType NOT NULL,  
    video_url VARCHAR(2048),
    FOREIGN KEY (activity_id) REFERENCES Activity(activity_id)
);

CREATE TABLE Lecture (
    activity_id VARCHAR(255) PRIMARY KEY,
    content_type ContentType NOT NULL,  
    video_url VARCHAR(2048),
    FOREIGN KEY (activity_id) REFERENCES Activity(activity_id)
);

CREATE TABLE ScreeningExam (
    activity_id VARCHAR(255) PRIMARY KEY,
    tier_passed ScreeningTier NOT NULL,  
    is_Passed BOOLEAN NOT NULL,
     FOREIGN KEY (activity_id) REFERENCES Activity(activity_id)
);



CREATE TABLE Question (
    question_id VARCHAR(255) PRIMARY KEY,
    question_text TEXT NOT NULL,
    question_image VARCHAR(2048),
    question_type QuestionType NOT NULL,  
    score INT NOT NULL,
    activity_id VARCHAR(255),
    FOREIGN KEY (activity_type) REFERENCES QuestionType(type_id),
    FOREIGN KEY (activity_id) REFERENCES Activity(activity_id)
);

CREATE TABLE Options (
    option_id VARCHAR(255) PRIMARY KEY,
    question_id VARCHAR(255) NOT NULL,
    option_text VARCHAR(255) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    FOREIGN KEY (question_id) REFERENCES Question(question_id)
);

CREATE TABLE Answers (
    question_id VARCHAR(255) NOT NULL,
    question_type QuestionType NOT NULL,  
    option_id VARCHAR(255),
    selected_answer TEXT,
    PRIMARY KEY (question_id, question_type),
    FOREIGN KEY (question_id) REFERENCES Question(question_id)
);

CREATE TABLE AssessmentResult (
    result_id VARCHAR(255) PRIMARY KEY,
    student_id VARCHAR(255) NOT NULL,
    module_id VARCHAR(255) NOT NULL,
    activity_id VARCHAR(255) NOT NULL,
    score_percentage DOUBLE NOT NULL,
    date_taken DATETIME NOT NULL,
    attempt_number INT NOT NULL,
    tier_level ScreeningTier,  
    earned_points INT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES Student(user_id),
    FOREIGN KEY (module_id) REFERENCES Module(module_id),
    FOREIGN KEY (activity_id) REFERENCES Activity(activity_id)
);

CREATE TABLE AssessmentResult_Answers (
    result_id VARCHAR(255) NOT NULL,
    question_id VARCHAR(255) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    FOREIGN KEY (result_id) REFERENCES AssessmentResult(result_id),
    FOREIGN KEY (question_id) REFERENCES Question(question_id),
    PRIMARY KEY (result_id, question_id)
);


CREATE TABLE StudentProgress (
    student_id VARCHAR(255) NOT NULL,
    course_id VARCHAR(255) NOT NULL,
    total_points INT NOT NULL,
    average_score DOUBLE NOT NULL,
    leaderboard_rank INT NOT NULL,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES Student(user_id),
    FOREIGN KEY (course_id) REFERENCES Course(course_id)
);

CREATE TABLE ModuleProgress (
    student_id VARCHAR(255) NOT NULL,
    module_id VARCHAR(255) NOT NULL,
    is_completed BOOLEAN NOT NULL,
    average_score DOUBLE NOT NULL,
    tier_passed ScreeningTier,  
    screening_exam_attempts INT NOT NULL DEFAULT 0,
    screening_exam_failed_count INT NOT NULL DEFAULT 0,
    PRIMARY KEY (student_id, module_id),
    FOREIGN KEY (student_id) REFERENCES Student(user_id),
    FOREIGN KEY (module_id) REFERENCES Module(module_id)
);



CREATE TABLE Message (
    message_id VARCHAR(255) PRIMARY KEY,
    sender_id VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    timestamp BIGINT NOT NULL,
    is_read BOOLEAN NOT NULL,
    is_mass BOOLEAN NOT NULL,
    delete_for_user BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (sender_id) REFERENCES User(user_id)
);

CREATE TABLE MessageRecipient (
    message_id VARCHAR(255) NOT NULL,
    recipient_id VARCHAR(255) NOT NULL,
    is_cc BOOLEAN NOT NULL,
    FOREIGN KEY (message_id) REFERENCES Message(message_id),
    FOREIGN KEY (recipient_id) REFERENCES User(user_id),
    PRIMARY KEY (message_id, recipient_id, is_cc)
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
    FOREIGN KEY (message_id) REFERENCES Message(message_id),
    FOREIGN KEY (attachment_id) REFERENCES Attachment(attachment_id),
    PRIMARY KEY (message_id, attachment_id)
);

CREATE TABLE Inbox (
    inbox_id VARCHAR(255) PRIMARY KEY,
    last_message_id VARCHAR(255),
    unread_count INT NOT NULL,
    timestamp BIGINT NOT NULL,
    FOREIGN KEY (last_message_id) REFERENCES Message(message_id)
);

CREATE TABLE InboxParticipant (
    inbox_id VARCHAR(255) NOT NULL,
    participant_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (inbox_id) REFERENCES Inbox(inbox_id),
    FOREIGN KEY (participant_id) REFERENCES User(user_id),
    PRIMARY KEY (inbox_id, participant_id)
);


CREATE TABLE Achievements (
    achievement_id VARCHAR(255) PRIMARY KEY,
    achievement_name VARCHAR(255) NOT NULL,
    achievement_description TEXT NOT NULL,
    achievement_image VARCHAR(2048),
    condition_type AchievementConditionType NOT NULL,  
    condition_value VARCHAR(255) NOT NULL,
    is_unlocked BOOLEAN NOT NULL,
    FOREIGN KEY (condition_type) REFERENCES AchievementConditionType(condition_type_id)
);

CREATE TABLE Badges (
    badge_id VARCHAR(255) PRIMARY KEY,
    badge_name VARCHAR(255) NOT NULL,
    badge_description TEXT NOT NULL,
    badge_image VARCHAR(2048),
    points_required INT NOT NULL,
    is_unlocked BOOLEAN NOT NULL
);

CREATE TABLE Leaderboard (
    course_id VARCHAR(255) NOT NULL,
    student_id VARCHAR(255) NOT NULL,
    total_points INT NOT NULL,
    ranking INT NOT NULL,
    last_updated DATETIME NOT NULL,
    PRIMARY KEY (course_id, student_id),
    FOREIGN KEY (course_id) REFERENCES Course(course_id),
    FOREIGN KEY (student_id) REFERENCES Student(user_id)
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
    FOREIGN KEY (student_id) REFERENCES Student(user_id),
    FOREIGN KEY (achievement_id) REFERENCES Achievements(achievement_id),
    PRIMARY KEY (student_id, achievement_id)
);

CREATE TABLE Student_Badges (
    student_id VARCHAR(255) NOT NULL,
    badge_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES Student(user_id),
    FOREIGN KEY (badge_id) REFERENCES Badges(badge_id),
    PRIMARY KEY (student_id, badge_id)
);

CREATE TABLE Student_ShopItem (
    student_id VARCHAR(255) NOT NULL,
    item_id VARCHAR(255) NOT NULL,
    purchase_date DATETIME NOT NULL,
    FOREIGN KEY (student_id) REFERENCES Student(user_id),
    FOREIGN KEY (item_id) REFERENCES ShopItem(item_id),
    PRIMARY KEY (student_id, item_id)
);

CREATE TABLE CalendarEvent (
    event_id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    date DATETIME NOT NULL,
    event_type EventType NOT NULL,  
    is_urgent BOOLEAN NOT NULL DEFAULT FALSE,
    location VARCHAR(255) NOT NULL,
    FOREIGN KEY (event_type) REFERENCES EventType(event_type_id)
);
