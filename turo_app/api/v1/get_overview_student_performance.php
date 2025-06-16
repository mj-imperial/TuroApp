<?php
header('Content-Type: application/json');

$configFile = __DIR__ . '/config.php';
if (!file_exists($configFile)) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => "Config file not found"
    ]);
    exit;
}
require_once $configFile;

$input = $_SERVER['REQUEST_METHOD'] === 'GET' ? $_GET : json_decode(file_get_contents('php://input'), true);
$studentId = $input['student_id'] ?? null;
$courseId = $input['course_id'] ?? null;

if (!$studentId || !$courseId) {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'Missing student_id or course_id']);
    exit;
}

try{
    $progressQuery = "
        SELECT total_points, average_score
        FROM StudentProgress
        WHERE student_id = ? AND course_id = ?
    ";
    $progressStmt = $conn->prepare($progressQuery);
    $progressStmt->bind_param('ss', $studentId, $courseId);
    $progressStmt->execute();
    $progressResult = $progressStmt->get_result();

    if ($progressResult->num_rows === 0) {
        echo json_encode(['error' => 'Student progress not found']);
        exit;
    }

    $progress = $progressResult->fetch_assoc();

    $totalAssessmentsQuery = "
        SELECT COUNT(*) AS total
        FROM Quiz q
        JOIN Activity a ON a.activity_id = q.activity_id
        JOIN Module m ON m.module_id = a.module_id
        WHERE a.activity_type = 'QUIZ'
        AND q.quiz_type_id != 4
        AND m.course_id = ?
    ";
    $totalAssessmentsStmt = $conn->prepare($totalAssessmentsQuery);
    $totalAssessmentsStmt->bind_param('s', $courseId);
    $totalAssessmentsStmt->execute();
    $totalAssessments = $totalAssessmentsStmt->get_result()->fetch_assoc()['total'];

    $completedAssessmentsQuery = "
        SELECT COUNT(DISTINCT ar.activity_id) AS completed
        FROM AssessmentResult ar
        JOIN Quiz q ON q.activity_id = ar.activity_id
        JOIN Activity a ON a.activity_id = ar.activity_id
        WHERE ar.student_id = ?
        AND ar.is_kept = TRUE
        AND q.quiz_type_id != 4
        AND a.activity_type = 'QUIZ'
    ";
    $completedStmt = $conn->prepare($completedAssessmentsQuery);
    $completedStmt->bind_param('s', $studentId);
    $completedStmt->execute();
    $completedAssessments = $completedStmt->get_result()->fetch_assoc()['completed'];

    $extremeQuizQuery = "
        SELECT a.activity_name, ar.score_percentage
        FROM AssessmentResult ar
        JOIN Activity a ON a.activity_id = ar.activity_id
        JOIN Quiz q ON q.activity_id = ar.activity_id
        JOIN Module m ON m.module_id = ar.module_id
        WHERE ar.student_id = ?
        AND ar.is_kept = TRUE
        AND q.quiz_type_id != 4
        AND m.course_id = ?
        ORDER BY ar.score_percentage ASC
    ";
    $extremeStmt = $conn->prepare($extremeQuizQuery);
    $extremeStmt->bind_param('ss', $studentId, $courseId);
    $extremeStmt->execute();
    $extremeResult = $extremeStmt->get_result();

    $lowest = $extremeResult->fetch_assoc();
    $extremeResult->data_seek($extremeResult->num_rows - 1); // move to last row
    $highest = $extremeResult->fetch_assoc();

    $moduleAvgQuery = "
        SELECT m.module_name, AVG(ar.score_percentage) AS avg_score
        FROM AssessmentResult ar
        JOIN Module m ON m.module_id = ar.module_id
        JOIN Activity a ON a.activity_id = ar.activity_id
        JOIN Quiz q ON q.activity_id = ar.activity_id
        WHERE ar.student_id = ?
        AND ar.is_kept = TRUE
        AND q.quiz_type_id != 4
        AND m.course_id = ?
        GROUP BY m.module_id
        ORDER BY avg_score ASC
    ";
    $moduleStmt = $conn->prepare($moduleAvgQuery);
    $moduleStmt->bind_param('ss', $studentId, $courseId);
    $moduleStmt->execute();
    $moduleResult = $moduleStmt->get_result();

    $lowestModule = $moduleResult->fetch_assoc();
    $moduleResult->data_seek($moduleResult->num_rows - 1);
    $highestModule = $moduleResult->fetch_assoc();

    echo json_encode([
        "overall_number_of_assessments" => (int) $totalAssessments,
        "completed_assessments" => (int) $completedAssessments,
        "total_points" => (int) $progress['total_points'],
        "average_score" => (double) $progress['average_score'],
        "lowest_assessment_quiz_name" => $lowest['activity_name'] ?? "",
        "lowest_assessment_scorePercentage" => (double) ($lowest['score_percentage'] ?? 0),
        "highest_assessment_quiz_name" => $highest['activity_name'] ?? "",
        "highest_assessment_scorePercentage" => (double) ($highest['score_percentage'] ?? 0),
        "lowest_scoring_module_name" => $lowestModule['module_name'] ?? "",
        "lowest_scoring_module_average" => (double) ($lowestModule['avg_score'] ?? 0),
        "highest_scoring_module_name" => $highestModule['module_name'] ?? "",
        "highest_scoring_module_average" => (double) ($highestModule['avg_score'] ?? 0),
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}