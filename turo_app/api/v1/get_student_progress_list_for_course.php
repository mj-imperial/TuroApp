<?php
require_once __DIR__ . '/config.php';

header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(['success' => false, 'message' => 'Method Not Allowed']);
    exit;
}

$courseId = $_GET['course_id'] ?? '';
if (!$courseId) {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'Missing course_id']);
    exit;
}

try {
    $stmt = $conn->prepare("SELECT course_name FROM Course WHERE course_id = ? LIMIT 1");
    $stmt->bind_param('s', $courseId);
    $stmt->execute();
    $courseResult = $stmt->get_result()->fetch_assoc();
    $courseName = $courseResult['course_name'] ?? '';
    $stmt->close();

    $stmt = $conn->prepare("
        SELECT COUNT(*) AS total
        FROM Activity a
        JOIN Quiz q ON q.activity_id = a.activity_id
        JOIN QuizType qt ON qt.quiz_type_id = q.quiz_type_id
        WHERE a.module_id IN (
            SELECT module_id FROM Module WHERE course_id = ?
        )
        AND qt.quiz_type_name != 'SCREENING_EXAM'
    ");
    $stmt->bind_param('s', $courseId);
    $stmt->execute();
    $totalAssessments = $stmt->get_result()->fetch_assoc()['total'] ?? 0;
    $stmt->close();

    $stmt = $conn->prepare("
        SELECT u.user_id, CONCAT(u.first_name, ' ', u.last_name) AS student_name, u.profile_pic
        FROM Enrollment e
        JOIN Student s ON s.user_id = e.student_id
        JOIN User u ON u.user_id = s.user_id
        WHERE e.course_id = ? AND e.isEnrolled = TRUE
    ");
    $stmt->bind_param('s', $courseId);
    $stmt->execute();
    $students = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    $stmt->close();

    $progressList = [];

    foreach ($students as $student) {
        $studentId = $student['user_id'];

        $stmt = $conn->prepare("
            SELECT COUNT(DISTINCT ar.activity_id) AS completed
            FROM AssessmentResult ar
            JOIN Quiz q ON q.activity_id = ar.activity_id
            JOIN QuizType qt ON qt.quiz_type_id = q.quiz_type_id
            WHERE ar.student_id = ?
              AND ar.module_id IN (
                  SELECT module_id FROM Module WHERE course_id = ?
              )
              AND qt.quiz_type_name != 'SCREENING_EXAM'
        ");
        $stmt->bind_param('ss', $studentId, $courseId);
        $stmt->execute();
        $completed = $stmt->get_result()->fetch_assoc()['completed'] ?? 0;
        $stmt->close();

        $stmt = $conn->prepare("
            SELECT total_points, average_score
            FROM StudentProgress
            WHERE student_id = ? AND course_id = ?
            LIMIT 1
        ");
        $stmt->bind_param('ss', $studentId, $courseId);
        $stmt->execute();
        $progress = $stmt->get_result()->fetch_assoc();
        $stmt->close();

        $progressList[] = [
            'student_id' => $studentId,
            'student_name' => $student['student_name'],
            'course_name' => $courseName,
            'profile_pic' => $student['profile_pic'],
            'completed_assessments' => (int)$completed,
            'total_points' => (int)($progress['total_points'] ?? 0),
            'average_score' => (float)($progress['average_score'] ?? 0.0)
        ];
    }

    echo json_encode([
        'success' => true,
        'overall_number_of_assessments' => (int)$totalAssessments,
        'progresses' => $progressList
    ]);
} catch (Exception $e) {
    error_log("Error in get_student_progress_list_for_course.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Internal Server Error']);
}
