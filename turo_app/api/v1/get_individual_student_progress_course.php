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

try {
    $moduleStmt = $conn->prepare("
        SELECT module_id, module_name 
        FROM Module 
        WHERE course_id = ?
    ");
    $moduleStmt->bind_param("s", $courseId);
    $moduleStmt->execute();
    $modulesResult = $moduleStmt->get_result();

    $modules = [];

    while ($module = $modulesResult->fetch_assoc()) {
        $moduleId = $module['module_id'];
        $moduleName = $module['module_name'];

        $quizStmt = $conn->prepare("
            SELECT A.activity_id, A.activity_name
            FROM Activity A
            JOIN Quiz Q ON A.activity_id = Q.activity_id
            WHERE A.module_id = ? AND Q.quiz_type_id != 4
        ");
        $quizStmt->bind_param("s", $moduleId);
        $quizStmt->execute();
        $quizResult = $quizStmt->get_result();

        $quizScores = [];

        while ($quiz = $quizResult->fetch_assoc()) {
            $activityId = $quiz['activity_id'];
            $activityName = $quiz['activity_name'];

            $scoreStmt = $conn->prepare("
                SELECT 
                    MAX(score_percentage) AS highest_score,
                    MIN(score_percentage) AS lowest_score
                FROM AssessmentResult
                WHERE student_id = ? AND activity_id = ?
            ");
            $scoreStmt->bind_param("ss", $studentId, $activityId);
            $scoreStmt->execute();
            $scoreSummary = $scoreStmt->get_result()->fetch_assoc();

            $latestStmt = $conn->prepare("
                SELECT score_percentage 
                FROM AssessmentResult 
                WHERE student_id = ? AND activity_id = ?
                ORDER BY date_taken DESC 
                LIMIT 1
            ");
            $latestStmt->bind_param("ss", $studentId, $activityId);
            $latestStmt->execute();
            $latestResult = $latestStmt->get_result()->fetch_assoc();

            if ($scoreSummary && $latestResult) {
                $quizScores[] = [
                    'activity_id' => $activityId,
                    'activity_name' => $activityName,
                    'highest_score_percentage' => floatval($scoreSummary['highest_score']),
                    'lowest_score_percentage' => floatval($scoreSummary['lowest_score']),
                    'latest_score_percentage' => floatval($latestResult['score_percentage']),
                ];
            }
        }

        $modules[] = [
            'module_id' => $moduleId,
            'module_name' => $moduleName,
            'quiz_scores' => $quizScores
        ];
    }

    echo json_encode([
        'success' => true,
        'modules' => $modules
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}