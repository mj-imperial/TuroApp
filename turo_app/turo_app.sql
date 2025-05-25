-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 25, 2025 at 02:07 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `turo_app`
--

-- --------------------------------------------------------

--
-- Table structure for table `achievementconditiontype`
--

CREATE TABLE `achievementconditiontype` (
  `condition_type_id` int(11) NOT NULL,
  `condition_name` enum('POINTS','MODULE_COMPLETION','QUIZ_SCORE','ACTIVITY_COMPLETION','FIRST_ASSESSMENT','GRADE_ABOVE','BADGES_EARNED','LEADERBOARD_RANK') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `achievements`
--

CREATE TABLE `achievements` (
  `achievement_id` varchar(255) NOT NULL,
  `achievement_name` varchar(255) NOT NULL,
  `achievement_description` text NOT NULL,
  `achievement_image` varchar(2048) DEFAULT NULL,
  `condition_type_id` int(11) NOT NULL,
  `condition_value` varchar(255) NOT NULL,
  `is_unlocked` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `activity`
--

CREATE TABLE `activity` (
  `activity_id` varchar(255) NOT NULL,
  `module_id` varchar(255) NOT NULL,
  `activity_type` enum('TUTORIAL','QUIZ','LECTURE','SCREENING_EXAM') NOT NULL,
  `activity_name` varchar(255) NOT NULL,
  `activity_description` text DEFAULT NULL,
  `unlock_date` datetime NOT NULL,
  `deadline_date` datetime DEFAULT NULL,
  `event_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `activity`
--

INSERT INTO `activity` (`activity_id`, `module_id`, `activity_type`, `activity_name`, `activity_description`, `unlock_date`, `deadline_date`, `event_id`) VALUES
('M101A1', 'M1011', 'LECTURE', 'Lecture 1 - All About Sets', 'Activities and Lectures to help you learn about sets', '2025-05-21 11:22:34', '2025-06-05 17:22:34', 'M101A1E1'),
('M101A2', 'M1011', 'TUTORIAL', 'Tutorial 1 - Additional Resources', 'Here are some additional resources for your learning', '2025-05-21 11:22:34', NULL, 'M101A1E2'),
('M101A3', 'M1011', 'QUIZ', 'Short Quiz 1', 'Short Quiz 1 on Sets', '2025-05-22 11:52:14', '2025-05-27 17:52:14', 'M101A3E1'),
('M101A4', 'M1011', 'QUIZ', 'Long Quiz 1', 'Long Quiz 1 on Sets', '2025-05-22 11:52:14', '2025-06-01 17:52:14', 'M101A4E2');

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `user_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `answers`
--

CREATE TABLE `answers` (
  `question_id` varchar(255) NOT NULL,
  `option_id` varchar(255) DEFAULT NULL,
  `selected_answer` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `assessmentresult`
--

CREATE TABLE `assessmentresult` (
  `result_id` varchar(255) NOT NULL,
  `student_id` varchar(255) NOT NULL,
  `module_id` varchar(255) NOT NULL,
  `activity_id` varchar(255) NOT NULL,
  `score_percentage` double NOT NULL,
  `date_taken` datetime NOT NULL,
  `attempt_number` int(11) NOT NULL,
  `tier_level_id` int(11) DEFAULT NULL,
  `earned_points` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `assessmentresult_answers`
--

CREATE TABLE `assessmentresult_answers` (
  `result_id` varchar(255) NOT NULL,
  `question_id` varchar(255) NOT NULL,
  `is_correct` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `attachment`
--

CREATE TABLE `attachment` (
  `attachment_id` varchar(255) NOT NULL,
  `file_name` varchar(255) NOT NULL,
  `file_url` varchar(2048) NOT NULL,
  `file_size` bigint(20) NOT NULL,
  `mime_type` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `badges`
--

CREATE TABLE `badges` (
  `badge_id` varchar(255) NOT NULL,
  `badge_name` varchar(255) NOT NULL,
  `badge_description` text NOT NULL,
  `badge_image` varchar(2048) DEFAULT NULL,
  `points_required` int(11) NOT NULL,
  `is_unlocked` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `calendarevent`
--

CREATE TABLE `calendarevent` (
  `event_id` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `date` datetime NOT NULL,
  `event_type_id` int(11) NOT NULL,
  `is_urgent` tinyint(1) NOT NULL DEFAULT 0,
  `location` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `calendarevent`
--

INSERT INTO `calendarevent` (`event_id`, `title`, `description`, `date`, `event_type_id`, `is_urgent`, `location`) VALUES
('M101A3E1', 'Short Quiz 1', 'We will be taking our Short Quiz 1', '2025-05-23 11:43:14', 2, 1, 'Good Shepard High School'),
('M101A4E2', 'Long Quiz 1', 'We will be taking our Long quiz', '2025-05-23 11:43:14', 4, 1, 'Good Shepard High School');

-- --------------------------------------------------------

--
-- Table structure for table `contenttype`
--

CREATE TABLE `contenttype` (
  `content_type_id` int(11) NOT NULL,
  `content_type_name` enum('TEXT','PDF','VIDEO') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `contenttype`
--

INSERT INTO `contenttype` (`content_type_id`, `content_type_name`) VALUES
(1, 'TEXT'),
(2, 'PDF'),
(3, 'VIDEO');

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

CREATE TABLE `course` (
  `course_id` varchar(255) NOT NULL,
  `course_code` varchar(255) NOT NULL,
  `course_name` varchar(255) NOT NULL,
  `teacher_id` varchar(255) NOT NULL,
  `course_description` text DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `course_picture` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `course`
--

INSERT INTO `course` (`course_id`, `course_code`, `course_name`, `teacher_id`, `course_description`, `start_date`, `end_date`, `course_picture`) VALUES
('2349023', 'MATH101', 'Math', 'T2012943', 'Basic Math', '2025-05-18', '2025-06-30', 'https://cdn.prod.website-files.com/6744bdb342b0a7660e7b7c7d/67df5face1f96bebc07f8f2b_3b23b533-c408-4380-bce6-0820b89131e9_math-on-board.jpeg');

-- --------------------------------------------------------

--
-- Table structure for table `enrollment`
--

CREATE TABLE `enrollment` (
  `enrollment_id` varchar(255) NOT NULL,
  `student_id` varchar(255) NOT NULL,
  `course_id` varchar(255) NOT NULL,
  `enrollment_date` date NOT NULL,
  `isEnrolled` tinyint(1) NOT NULL DEFAULT 0,
  `finalGrade` double NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `enrollment`
--

INSERT INTO `enrollment` (`enrollment_id`, `student_id`, `course_id`, `enrollment_date`, `isEnrolled`, `finalGrade`) VALUES
('31349823', '202210383', '2349023', '2025-05-18', 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `eventtype`
--

CREATE TABLE `eventtype` (
  `event_type_id` int(11) NOT NULL,
  `event_type_name` enum('MODULE','SHORT_QUIZ','PRACTICE_QUIZ','LONG_QUIZ','CATCH_UP') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `eventtype`
--

INSERT INTO `eventtype` (`event_type_id`, `event_type_name`) VALUES
(1, 'MODULE'),
(2, 'SHORT_QUIZ'),
(3, 'PRACTICE_QUIZ'),
(4, 'LONG_QUIZ'),
(5, 'CATCH_UP');

-- --------------------------------------------------------

--
-- Table structure for table `inbox`
--

CREATE TABLE `inbox` (
  `inbox_id` varchar(255) NOT NULL,
  `last_message_id` varchar(255) DEFAULT NULL,
  `unread_count` int(11) NOT NULL,
  `timestamp` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `inboxparticipant`
--

CREATE TABLE `inboxparticipant` (
  `inbox_id` varchar(255) NOT NULL,
  `participant_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `leaderboard`
--

CREATE TABLE `leaderboard` (
  `course_id` varchar(255) NOT NULL,
  `student_id` varchar(255) NOT NULL,
  `total_points` int(11) NOT NULL,
  `ranking` int(11) NOT NULL,
  `last_updated` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `lecture`
--

CREATE TABLE `lecture` (
  `activity_id` varchar(255) NOT NULL,
  `content_type_id` int(11) NOT NULL,
  `video_url` varchar(2048) DEFAULT NULL,
  `pdf_url` varchar(2048) DEFAULT NULL,
  `doc_url` varchar(2048) DEFAULT NULL,
  `text_body` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `message`
--

CREATE TABLE `message` (
  `message_id` varchar(255) NOT NULL,
  `sender_id` varchar(255) NOT NULL,
  `subject` varchar(255) NOT NULL,
  `body` text NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `is_read` tinyint(1) NOT NULL,
  `is_mass` tinyint(1) NOT NULL,
  `delete_for_user` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `messageattachment`
--

CREATE TABLE `messageattachment` (
  `message_id` varchar(255) NOT NULL,
  `attachment_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `messagerecipient`
--

CREATE TABLE `messagerecipient` (
  `message_id` varchar(255) NOT NULL,
  `recipient_id` varchar(255) NOT NULL,
  `is_cc` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `module`
--

CREATE TABLE `module` (
  `module_id` varchar(255) NOT NULL,
  `course_id` varchar(255) NOT NULL,
  `module_name` varchar(255) NOT NULL,
  `module_description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `module`
--

INSERT INTO `module` (`module_id`, `course_id`, `module_name`, `module_description`) VALUES
('M1011', '2349023', 'Module 1 - Sets', 'Introduction to Sets, Venn Diagrams, and Set Operations'),
('M1012', '2349023', 'Module 2 - Real Numbers (Integers)', 'Absolute Values, Integers and Integer Operations');

-- --------------------------------------------------------

--
-- Table structure for table `moduleprogress`
--

CREATE TABLE `moduleprogress` (
  `student_id` varchar(255) NOT NULL,
  `module_id` varchar(255) NOT NULL,
  `is_completed` tinyint(1) NOT NULL,
  `average_score` double NOT NULL,
  `tier_passed_id` int(11) DEFAULT NULL,
  `screening_exam_attempts` int(11) NOT NULL DEFAULT 0,
  `screening_exam_failed_count` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `options`
--

CREATE TABLE `options` (
  `option_id` varchar(255) NOT NULL,
  `question_id` varchar(255) NOT NULL,
  `option_text` varchar(255) NOT NULL,
  `is_correct` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `options`
--

INSERT INTO `options` (`option_id`, `question_id`, `option_text`, `is_correct`) VALUES
('M101A3Q10O1', 'M101A3Q10', 'True', 1),
('M101A3Q10O2', 'M101A3Q10', 'False', 0),
('M101A3Q1O1', 'M101A3Q1', '{1,2}', 0),
('M101A3Q1O2', 'M101A3Q1', '{3,4}', 1),
('M101A3Q1O3', 'M101A3Q1', '{5,6}', 0),
('M101A3Q1O4', 'M101A3Q1', '{1,2,3,4,5,6}', 0),
('M101A3Q2O1', 'M101A3Q2', '{{a},{b},{a,b}}', 0),
('M101A3Q2O2', 'M101A3Q2', '{∅,{a},{b},{a,b}}', 1),
('M101A3Q2O3', 'M101A3Q2', '{{a,b}}', 0),
('M101A3Q2O4', 'M101A3Q2', '{∅,{a,b}}', 0),
('M101A3Q3O1', 'M101A3Q3', '{1,3,5}', 1),
('M101A3Q3O2', 'M101A3Q3', '{2,4}', 0),
('M101A3Q3O3', 'M101A3Q3', '{1,2,3,4,5}', 0),
('M101A3Q3O4', 'M101A3Q3', '∅', 0),
('M101A3Q4O1', 'M101A3Q4', 'Two sets are equal if and only if they have exactly the same elements.', 0),
('M101A3Q4O2', 'M101A3Q4', 'The order of elements matters in a set.', 1),
('M101A3Q4O3', 'M101A3Q4', 'Repetition of elements in the roster method doesn’t change the set.', 0),
('M101A3Q4O4', 'M101A3Q4', 'Every element of the empty set vacuously satisfies any given property.', 0),
('M101A3Q5O1', 'M101A3Q5', 'True', 1),
('M101A3Q5O2', 'M101A3Q5', 'False', 0),
('M101A3Q6O1', 'M101A3Q6', 'True', 1),
('M101A3Q6O2', 'M101A3Q6', 'False', 0),
('M101A3Q7O1', 'M101A3Q7', 'True', 1),
('M101A3Q7O2', 'M101A3Q7', 'False', 0),
('M101A3Q8O1', 'M101A3Q8', 'True', 0),
('M101A3Q8O2', 'M101A3Q8', 'False', 1),
('M101A3Q9O1', 'M101A3Q9', 'True', 1),
('M101A3Q9O2', 'M101A3Q9', 'False', 0);

-- --------------------------------------------------------

--
-- Table structure for table `password_resets`
--

CREATE TABLE `password_resets` (
  `user_id` varchar(255) NOT NULL,
  `code_hash` varchar(255) NOT NULL,
  `expires_at` datetime NOT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `requested_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `password_resets`
--

INSERT INTO `password_resets` (`user_id`, `code_hash`, `expires_at`, `created_at`, `requested_at`) VALUES
('202210383', '$2y$10$s3Xa/ujIi75zipRXUfcKmuyRLgaIHnI5aY.krEFAcxuaWIDYMO20a', '2025-05-19 13:33:29', '2025-05-14 22:40:23', '2025-05-19 13:23:29');

-- --------------------------------------------------------

--
-- Table structure for table `question`
--

CREATE TABLE `question` (
  `question_id` varchar(255) NOT NULL,
  `question_text` text NOT NULL,
  `question_image` varchar(2048) DEFAULT NULL,
  `question_type_id` int(11) NOT NULL,
  `score` int(11) NOT NULL,
  `activity_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `question`
--

INSERT INTO `question` (`question_id`, `question_text`, `question_image`, `question_type_id`, `score`, `activity_id`) VALUES
('M101A3Q1', 'Let 𝐴 = { 1 , 2 , 3 , 4 } and 𝐵 = { 3 , 4 , 5 , 6 }. What is 𝐴 ∩ 𝐵?', NULL, 1, 1, 'M101A3'),
('M101A3Q10', 'True or False\r\nFor any sets 𝐴 and 𝐵, ∣ 𝐴 ∪ 𝐵 ∣ = ∣ 𝐴 ∣ + ∣ 𝐵 ∣ − ∣ 𝐴 ∩ 𝐵 ∣.', NULL, 1, 1, 'M101A3'),
('M101A3Q2', 'Which of the following represents the power set of { 𝑎 , 𝑏 }?', NULL, 1, 1, 'M101A3'),
('M101A3Q3', 'If 𝑈 = { 1 , 2 , 3 , 4 , 5 } is the universal set and 𝐴 = { 2 , 4 }, what is the complement 𝐴𝑐?', NULL, 1, 1, 'M101A3'),
('M101A3Q4', 'Which statement is false?', NULL, 1, 1, 'M101A3'),
('M101A3Q5', 'True or False\r\nThe empty set ∅ is a subset of every set.', NULL, 1, 1, 'M101A3'),
('M101A3Q6', 'True or False\r\n{1,2}⊆{1,2,3} but { 1 , 2 , 3 } ⊈ { 1 , 2 }.', NULL, 1, 1, 'M101A3'),
('M101A3Q7', 'True or False\r\nIf 𝐴 ⊂ 𝐵 (proper subset), then ∣ 𝐴 ∣ < ∣ 𝐵 ∣.', NULL, 1, 1, 'M101A3'),
('M101A3Q8', 'True or False\r\nThe union of two disjoint sets is always the empty set.', NULL, 1, 1, 'M101A3'),
('M101A3Q9', 'True or False\r\n{x:x is an even integer} is an infinite set.', NULL, 1, 1, 'M101A3');

-- --------------------------------------------------------

--
-- Table structure for table `questiontype`
--

CREATE TABLE `questiontype` (
  `type_id` int(11) NOT NULL,
  `type_name` enum('MULTIPLE_CHOICE','SHORT_ANSWER') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `questiontype`
--

INSERT INTO `questiontype` (`type_id`, `type_name`) VALUES
(1, 'MULTIPLE_CHOICE'),
(2, 'SHORT_ANSWER');

-- --------------------------------------------------------

--
-- Table structure for table `quiz`
--

CREATE TABLE `quiz` (
  `activity_id` varchar(255) NOT NULL,
  `number_of_attempts` int(11) NOT NULL,
  `quiz_type_id` int(11) NOT NULL,
  `time_limit` int(11) NOT NULL,
  `is_passed` tinyint(1) DEFAULT NULL,
  `number_of_questions` int(11) NOT NULL,
  `overall_points` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `quiz`
--

INSERT INTO `quiz` (`activity_id`, `number_of_attempts`, `quiz_type_id`, `time_limit`, `is_passed`, `number_of_questions`, `overall_points`) VALUES
('M101A3', 2, 1, 1800, NULL, 10, 10),
('M101A4', 1, 3, 3000, NULL, 10, 10);

-- --------------------------------------------------------

--
-- Table structure for table `quiztype`
--

CREATE TABLE `quiztype` (
  `quiz_type_id` int(11) NOT NULL,
  `quiz_type_name` enum('SHORT','PRACTICE','LONG') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `quiztype`
--

INSERT INTO `quiztype` (`quiz_type_id`, `quiz_type_name`) VALUES
(1, 'SHORT'),
(2, 'PRACTICE'),
(3, 'LONG');

-- --------------------------------------------------------

--
-- Table structure for table `screeningexam`
--

CREATE TABLE `screeningexam` (
  `activity_id` varchar(255) NOT NULL,
  `tier_passed_id` int(11) NOT NULL,
  `is_Passed` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `screeningtier`
--

CREATE TABLE `screeningtier` (
  `tier_id` int(11) NOT NULL,
  `tier_name` enum('TIER_1','TIER_2','TIER_3') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `shopitem`
--

CREATE TABLE `shopitem` (
  `item_id` varchar(255) NOT NULL,
  `item_name` varchar(255) NOT NULL,
  `item_description` text NOT NULL,
  `item_picture` varchar(2048) NOT NULL,
  `points_required` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `user_id` varchar(255) NOT NULL,
  `averageScore` double NOT NULL DEFAULT 0,
  `isCatchUp` tinyint(1) NOT NULL DEFAULT 0,
  `totalPoints` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`user_id`, `averageScore`, `isCatchUp`, `totalPoints`) VALUES
('202210383', 0, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `studentprogress`
--

CREATE TABLE `studentprogress` (
  `student_id` varchar(255) NOT NULL,
  `course_id` varchar(255) NOT NULL,
  `total_points` int(11) NOT NULL,
  `average_score` double NOT NULL,
  `leaderboard_rank` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student_achievements`
--

CREATE TABLE `student_achievements` (
  `student_id` varchar(255) NOT NULL,
  `achievement_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student_badges`
--

CREATE TABLE `student_badges` (
  `student_id` varchar(255) NOT NULL,
  `badge_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student_shopitem`
--

CREATE TABLE `student_shopitem` (
  `student_id` varchar(255) NOT NULL,
  `item_id` varchar(255) NOT NULL,
  `purchase_date` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `teacher`
--

CREATE TABLE `teacher` (
  `user_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `teacher`
--

INSERT INTO `teacher` (`user_id`) VALUES
('T2012943');

-- --------------------------------------------------------

--
-- Table structure for table `tutorial`
--

CREATE TABLE `tutorial` (
  `activity_id` varchar(255) NOT NULL,
  `content_type_id` int(11) NOT NULL,
  `video_url` varchar(2048) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `user_id` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role_id` int(11) NOT NULL,
  `profile_pic` varchar(512) DEFAULT NULL,
  `agreed_to_terms` tinyint(1) NOT NULL DEFAULT 0,
  `requires_password_change` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_id`, `first_name`, `last_name`, `email`, `password_hash`, `role_id`, `profile_pic`, `agreed_to_terms`, `requires_password_change`) VALUES
('202210383', 'Test', 'Imperial', 'codegeassislife@gmail.com', '$2y$10$sNBgLs3q4BxEk1Na2xLkwulpRUQSOj71YShSYMQk8wLiLcbRRTSoi', 1, 'http://10.0.2.2/turo_app/api/v1/uploads/profile_pics/profile_682ac032e435b3.42709459.jpg', 1, 0),
('202211373', 'Test', 'Imperial', 'imperialmj542@gmail.com', '$2y$10$lJ5IqHkxhS61dn7FA7wKS.SHbNGw16NRhHH66QtMR3hivwJQvM9Rq', 1, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSFCkHR8ZebRAxt-45SHEh0QBetYHdeE9cSEw&s', 0, 1),
('T2012943', 'Teacher1', 'TeacherLastName', 'turoapplication40@gmail.com', '$2y$10$bcFSRphuRATGFPr5F0Okm.vWQKD/1H2K.wy6Wt/buMDX1r56fXriG', 2, 'https://img.gta5-mods.com/q95/images/final-fantasy-vii-cloud-strife-jp-voice/371e38-26df6e6724dd714619e35b29e7455df1.jpg', 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `userrole`
--

CREATE TABLE `userrole` (
  `role_id` int(11) NOT NULL,
  `role_name` enum('STUDENT','TEACHER','ADMIN') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `userrole`
--

INSERT INTO `userrole` (`role_id`, `role_name`) VALUES
(1, 'STUDENT'),
(2, 'TEACHER'),
(3, 'ADMIN');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `achievementconditiontype`
--
ALTER TABLE `achievementconditiontype`
  ADD PRIMARY KEY (`condition_type_id`),
  ADD UNIQUE KEY `condition_name` (`condition_name`);

--
-- Indexes for table `achievements`
--
ALTER TABLE `achievements`
  ADD PRIMARY KEY (`achievement_id`),
  ADD KEY `condition_type_id` (`condition_type_id`);

--
-- Indexes for table `activity`
--
ALTER TABLE `activity`
  ADD PRIMARY KEY (`activity_id`),
  ADD KEY `module_id` (`module_id`),
  ADD KEY `idx_activity_event_id` (`event_id`);

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `answers`
--
ALTER TABLE `answers`
  ADD PRIMARY KEY (`question_id`),
  ADD KEY `option_id` (`option_id`);

--
-- Indexes for table `assessmentresult`
--
ALTER TABLE `assessmentresult`
  ADD PRIMARY KEY (`result_id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `module_id` (`module_id`),
  ADD KEY `activity_id` (`activity_id`),
  ADD KEY `tier_level_id` (`tier_level_id`);

--
-- Indexes for table `assessmentresult_answers`
--
ALTER TABLE `assessmentresult_answers`
  ADD PRIMARY KEY (`result_id`,`question_id`),
  ADD KEY `question_id` (`question_id`);

--
-- Indexes for table `attachment`
--
ALTER TABLE `attachment`
  ADD PRIMARY KEY (`attachment_id`);

--
-- Indexes for table `badges`
--
ALTER TABLE `badges`
  ADD PRIMARY KEY (`badge_id`);

--
-- Indexes for table `calendarevent`
--
ALTER TABLE `calendarevent`
  ADD PRIMARY KEY (`event_id`),
  ADD KEY `event_type_id` (`event_type_id`);

--
-- Indexes for table `contenttype`
--
ALTER TABLE `contenttype`
  ADD PRIMARY KEY (`content_type_id`),
  ADD UNIQUE KEY `content_type_name` (`content_type_name`);

--
-- Indexes for table `course`
--
ALTER TABLE `course`
  ADD PRIMARY KEY (`course_id`),
  ADD UNIQUE KEY `course_code` (`course_code`),
  ADD KEY `teacher_id` (`teacher_id`);

--
-- Indexes for table `enrollment`
--
ALTER TABLE `enrollment`
  ADD PRIMARY KEY (`enrollment_id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `course_id` (`course_id`);

--
-- Indexes for table `eventtype`
--
ALTER TABLE `eventtype`
  ADD PRIMARY KEY (`event_type_id`),
  ADD UNIQUE KEY `event_type_name` (`event_type_name`);

--
-- Indexes for table `inbox`
--
ALTER TABLE `inbox`
  ADD PRIMARY KEY (`inbox_id`),
  ADD KEY `last_message_id` (`last_message_id`);

--
-- Indexes for table `inboxparticipant`
--
ALTER TABLE `inboxparticipant`
  ADD PRIMARY KEY (`inbox_id`,`participant_id`),
  ADD KEY `participant_id` (`participant_id`);

--
-- Indexes for table `leaderboard`
--
ALTER TABLE `leaderboard`
  ADD PRIMARY KEY (`course_id`,`student_id`),
  ADD KEY `student_id` (`student_id`);

--
-- Indexes for table `lecture`
--
ALTER TABLE `lecture`
  ADD PRIMARY KEY (`activity_id`),
  ADD KEY `content_type_id` (`content_type_id`);

--
-- Indexes for table `message`
--
ALTER TABLE `message`
  ADD PRIMARY KEY (`message_id`),
  ADD KEY `sender_id` (`sender_id`);

--
-- Indexes for table `messageattachment`
--
ALTER TABLE `messageattachment`
  ADD PRIMARY KEY (`message_id`,`attachment_id`),
  ADD KEY `attachment_id` (`attachment_id`);

--
-- Indexes for table `messagerecipient`
--
ALTER TABLE `messagerecipient`
  ADD PRIMARY KEY (`message_id`,`recipient_id`,`is_cc`),
  ADD KEY `recipient_id` (`recipient_id`);

--
-- Indexes for table `module`
--
ALTER TABLE `module`
  ADD PRIMARY KEY (`module_id`),
  ADD KEY `course_id` (`course_id`);

--
-- Indexes for table `moduleprogress`
--
ALTER TABLE `moduleprogress`
  ADD PRIMARY KEY (`student_id`,`module_id`),
  ADD KEY `module_id` (`module_id`),
  ADD KEY `tier_passed_id` (`tier_passed_id`);

--
-- Indexes for table `options`
--
ALTER TABLE `options`
  ADD PRIMARY KEY (`option_id`),
  ADD KEY `question_id` (`question_id`);

--
-- Indexes for table `password_resets`
--
ALTER TABLE `password_resets`
  ADD PRIMARY KEY (`user_id`),
  ADD KEY `idx_expires_at` (`expires_at`);

--
-- Indexes for table `question`
--
ALTER TABLE `question`
  ADD PRIMARY KEY (`question_id`),
  ADD KEY `activity_id` (`activity_id`),
  ADD KEY `question_type_id` (`question_type_id`);

--
-- Indexes for table `questiontype`
--
ALTER TABLE `questiontype`
  ADD PRIMARY KEY (`type_id`),
  ADD UNIQUE KEY `type_name` (`type_name`);

--
-- Indexes for table `quiz`
--
ALTER TABLE `quiz`
  ADD PRIMARY KEY (`activity_id`),
  ADD KEY `quiz_type_id` (`quiz_type_id`);

--
-- Indexes for table `quiztype`
--
ALTER TABLE `quiztype`
  ADD PRIMARY KEY (`quiz_type_id`),
  ADD UNIQUE KEY `quiz_type_name` (`quiz_type_name`);

--
-- Indexes for table `screeningexam`
--
ALTER TABLE `screeningexam`
  ADD PRIMARY KEY (`activity_id`),
  ADD KEY `tier_passed_id` (`tier_passed_id`);

--
-- Indexes for table `screeningtier`
--
ALTER TABLE `screeningtier`
  ADD PRIMARY KEY (`tier_id`),
  ADD UNIQUE KEY `tier_name` (`tier_name`);

--
-- Indexes for table `shopitem`
--
ALTER TABLE `shopitem`
  ADD PRIMARY KEY (`item_id`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `studentprogress`
--
ALTER TABLE `studentprogress`
  ADD PRIMARY KEY (`student_id`,`course_id`),
  ADD KEY `course_id` (`course_id`);

--
-- Indexes for table `student_achievements`
--
ALTER TABLE `student_achievements`
  ADD PRIMARY KEY (`student_id`,`achievement_id`),
  ADD KEY `achievement_id` (`achievement_id`);

--
-- Indexes for table `student_badges`
--
ALTER TABLE `student_badges`
  ADD PRIMARY KEY (`student_id`,`badge_id`),
  ADD KEY `badge_id` (`badge_id`);

--
-- Indexes for table `student_shopitem`
--
ALTER TABLE `student_shopitem`
  ADD PRIMARY KEY (`student_id`,`item_id`),
  ADD KEY `item_id` (`item_id`);

--
-- Indexes for table `teacher`
--
ALTER TABLE `teacher`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `tutorial`
--
ALTER TABLE `tutorial`
  ADD PRIMARY KEY (`activity_id`),
  ADD KEY `content_type_id` (`content_type_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `role_id` (`role_id`);

--
-- Indexes for table `userrole`
--
ALTER TABLE `userrole`
  ADD PRIMARY KEY (`role_id`),
  ADD UNIQUE KEY `role_name` (`role_name`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `achievementconditiontype`
--
ALTER TABLE `achievementconditiontype`
  MODIFY `condition_type_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `contenttype`
--
ALTER TABLE `contenttype`
  MODIFY `content_type_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `eventtype`
--
ALTER TABLE `eventtype`
  MODIFY `event_type_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `questiontype`
--
ALTER TABLE `questiontype`
  MODIFY `type_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `quiztype`
--
ALTER TABLE `quiztype`
  MODIFY `quiz_type_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `screeningtier`
--
ALTER TABLE `screeningtier`
  MODIFY `tier_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `userrole`
--
ALTER TABLE `userrole`
  MODIFY `role_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `achievements`
--
ALTER TABLE `achievements`
  ADD CONSTRAINT `achievements_ibfk_1` FOREIGN KEY (`condition_type_id`) REFERENCES `achievementconditiontype` (`condition_type_id`);

--
-- Constraints for table `activity`
--
ALTER TABLE `activity`
  ADD CONSTRAINT `activity_ibfk_1` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);

--
-- Constraints for table `admin`
--
ALTER TABLE `admin`
  ADD CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `answers`
--
ALTER TABLE `answers`
  ADD CONSTRAINT `answers_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `question` (`question_id`),
  ADD CONSTRAINT `answers_ibfk_2` FOREIGN KEY (`option_id`) REFERENCES `options` (`option_id`);

--
-- Constraints for table `assessmentresult`
--
ALTER TABLE `assessmentresult`
  ADD CONSTRAINT `assessmentresult_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`user_id`),
  ADD CONSTRAINT `assessmentresult_ibfk_2` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`),
  ADD CONSTRAINT `assessmentresult_ibfk_3` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`activity_id`),
  ADD CONSTRAINT `assessmentresult_ibfk_4` FOREIGN KEY (`tier_level_id`) REFERENCES `screeningtier` (`tier_id`);

--
-- Constraints for table `assessmentresult_answers`
--
ALTER TABLE `assessmentresult_answers`
  ADD CONSTRAINT `assessmentresult_answers_ibfk_1` FOREIGN KEY (`result_id`) REFERENCES `assessmentresult` (`result_id`),
  ADD CONSTRAINT `assessmentresult_answers_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `question` (`question_id`);

--
-- Constraints for table `calendarevent`
--
ALTER TABLE `calendarevent`
  ADD CONSTRAINT `calendarevent_ibfk_1` FOREIGN KEY (`event_type_id`) REFERENCES `eventtype` (`event_type_id`),
  ADD CONSTRAINT `calendarevent_ibfk_2` FOREIGN KEY (`event_id`) REFERENCES `activity` (`event_id`);

--
-- Constraints for table `course`
--
ALTER TABLE `course`
  ADD CONSTRAINT `course_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`user_id`);

--
-- Constraints for table `enrollment`
--
ALTER TABLE `enrollment`
  ADD CONSTRAINT `enrollment_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`user_id`),
  ADD CONSTRAINT `enrollment_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`);

--
-- Constraints for table `inbox`
--
ALTER TABLE `inbox`
  ADD CONSTRAINT `inbox_ibfk_1` FOREIGN KEY (`last_message_id`) REFERENCES `message` (`message_id`);

--
-- Constraints for table `inboxparticipant`
--
ALTER TABLE `inboxparticipant`
  ADD CONSTRAINT `inboxparticipant_ibfk_1` FOREIGN KEY (`inbox_id`) REFERENCES `inbox` (`inbox_id`),
  ADD CONSTRAINT `inboxparticipant_ibfk_2` FOREIGN KEY (`participant_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `leaderboard`
--
ALTER TABLE `leaderboard`
  ADD CONSTRAINT `leaderboard_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`),
  ADD CONSTRAINT `leaderboard_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `student` (`user_id`);

--
-- Constraints for table `lecture`
--
ALTER TABLE `lecture`
  ADD CONSTRAINT `lecture_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`activity_id`),
  ADD CONSTRAINT `lecture_ibfk_2` FOREIGN KEY (`content_type_id`) REFERENCES `contenttype` (`content_type_id`);

--
-- Constraints for table `message`
--
ALTER TABLE `message`
  ADD CONSTRAINT `message_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `messageattachment`
--
ALTER TABLE `messageattachment`
  ADD CONSTRAINT `messageattachment_ibfk_1` FOREIGN KEY (`message_id`) REFERENCES `message` (`message_id`),
  ADD CONSTRAINT `messageattachment_ibfk_2` FOREIGN KEY (`attachment_id`) REFERENCES `attachment` (`attachment_id`);

--
-- Constraints for table `messagerecipient`
--
ALTER TABLE `messagerecipient`
  ADD CONSTRAINT `messagerecipient_ibfk_1` FOREIGN KEY (`message_id`) REFERENCES `message` (`message_id`),
  ADD CONSTRAINT `messagerecipient_ibfk_2` FOREIGN KEY (`recipient_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `module`
--
ALTER TABLE `module`
  ADD CONSTRAINT `module_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`);

--
-- Constraints for table `moduleprogress`
--
ALTER TABLE `moduleprogress`
  ADD CONSTRAINT `moduleprogress_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`user_id`),
  ADD CONSTRAINT `moduleprogress_ibfk_2` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`),
  ADD CONSTRAINT `moduleprogress_ibfk_3` FOREIGN KEY (`tier_passed_id`) REFERENCES `screeningtier` (`tier_id`);

--
-- Constraints for table `options`
--
ALTER TABLE `options`
  ADD CONSTRAINT `options_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `question` (`question_id`);

--
-- Constraints for table `password_resets`
--
ALTER TABLE `password_resets`
  ADD CONSTRAINT `fk_password_resets_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `question`
--
ALTER TABLE `question`
  ADD CONSTRAINT `question_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`activity_id`),
  ADD CONSTRAINT `question_ibfk_2` FOREIGN KEY (`question_type_id`) REFERENCES `questiontype` (`type_id`);

--
-- Constraints for table `quiz`
--
ALTER TABLE `quiz`
  ADD CONSTRAINT `quiz_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`activity_id`),
  ADD CONSTRAINT `quiz_ibfk_2` FOREIGN KEY (`quiz_type_id`) REFERENCES `quiztype` (`quiz_type_id`);

--
-- Constraints for table `screeningexam`
--
ALTER TABLE `screeningexam`
  ADD CONSTRAINT `screeningexam_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`activity_id`),
  ADD CONSTRAINT `screeningexam_ibfk_2` FOREIGN KEY (`tier_passed_id`) REFERENCES `screeningtier` (`tier_id`);

--
-- Constraints for table `student`
--
ALTER TABLE `student`
  ADD CONSTRAINT `student_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `studentprogress`
--
ALTER TABLE `studentprogress`
  ADD CONSTRAINT `studentprogress_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`user_id`),
  ADD CONSTRAINT `studentprogress_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`);

--
-- Constraints for table `student_achievements`
--
ALTER TABLE `student_achievements`
  ADD CONSTRAINT `student_achievements_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`user_id`),
  ADD CONSTRAINT `student_achievements_ibfk_2` FOREIGN KEY (`achievement_id`) REFERENCES `achievements` (`achievement_id`);

--
-- Constraints for table `student_badges`
--
ALTER TABLE `student_badges`
  ADD CONSTRAINT `student_badges_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`user_id`),
  ADD CONSTRAINT `student_badges_ibfk_2` FOREIGN KEY (`badge_id`) REFERENCES `badges` (`badge_id`);

--
-- Constraints for table `student_shopitem`
--
ALTER TABLE `student_shopitem`
  ADD CONSTRAINT `student_shopitem_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`user_id`),
  ADD CONSTRAINT `student_shopitem_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `shopitem` (`item_id`);

--
-- Constraints for table `teacher`
--
ALTER TABLE `teacher`
  ADD CONSTRAINT `teacher_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `tutorial`
--
ALTER TABLE `tutorial`
  ADD CONSTRAINT `tutorial_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`activity_id`),
  ADD CONSTRAINT `tutorial_ibfk_2` FOREIGN KEY (`content_type_id`) REFERENCES `contenttype` (`content_type_id`);

--
-- Constraints for table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `user_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `userrole` (`role_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
