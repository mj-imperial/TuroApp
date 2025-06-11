<?php
header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    exit(json_encode(['success' => false, 'message' => 'Method Not Allowed']));
}

$raw = file_get_contents('php://input');
$data = json_decode($raw, true);

$quizId = $_GET['activity_id'] ?? null;
$moduleId = $_GET['module_id'] ?? null;

if (!$quizId || !$moduleId) {
    http_response_code(400);
    exit(json_encode(['success' => false, 'message' => 'Missing activity_id or module_id']));
}

$requiredFields = [
    'activity_name', 'number_of_attempts', 'quiz_type_name',
    'time_limit', 'has_answers_shown', 'number_of_questions',
    'overall_points', 'questions', 'unlock_date'
];

foreach ($requiredFields as $field) {
    if (!array_key_exists($field, $data)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => "Missing field: $field"]);
        exit;
    }
}

try {
    $conn->begin_transaction();

    $description = $data['activity_description'] ?? null;
    $unlockDate = date('Y-m-d H:i:s', strtotime($data['unlock_date']));
    $deadline = !empty($data['deadline_date']) ? date('Y-m-d H:i:s', strtotime($data['deadline_date'])) : null;

    $stmt = $conn->prepare("
        UPDATE Activity
           SET activity_name = ?,
               activity_description = ?,
               unlock_date = ?,
               deadline_date = ?
         WHERE activity_id = ?
    ");
    $stmt->bind_param("sssss", $data['activity_name'], $description, $unlockDate, $deadline, $quizId);
    $stmt->execute();
    $stmt->close();

    $stmtType = $conn->prepare("SELECT quiz_type_id FROM QuizType WHERE quiz_type_name = ?");
    $stmtType->bind_param("s", $data['quiz_type_name']);
    $stmtType->execute();
    $resType = $stmtType->get_result();
    $rowType = $resType->fetch_assoc();
    $stmtType->close();
    if (!$rowType) throw new Exception("Invalid quiz_type_name");
    $quizTypeId = (int)$rowType['quiz_type_id'];

    $hasAnswersShown = $data['has_answers_shown'] ? 1 : 0;
    $stmtQuiz = $conn->prepare("
        UPDATE Quiz
           SET number_of_attempts = ?, quiz_type_id = ?, time_limit = ?, number_of_questions = ?, overall_points = ?, has_answers_shown = ?
         WHERE activity_id = ?
    ");
    $stmtQuiz->bind_param(
        "iiiiiis",
        $data['number_of_attempts'],
        $quizTypeId,
        $data['time_limit'],
        $data['number_of_questions'],
        $data['overall_points'],
        $hasAnswersShown,
        $quizId
    );
    $stmtQuiz->execute();
    $stmtQuiz->close();

    $conn->query("DELETE FROM Options WHERE question_id IN (SELECT question_id FROM Question WHERE activity_id = '$quizId')");
    $conn->query("DELETE FROM Question WHERE activity_id = '$quizId'");

    $stmtQType = $conn->prepare("SELECT type_id FROM QuestionType WHERE type_name = ?");
    $stmtInsertQ = $conn->prepare("
        INSERT INTO Question (question_id, question_text, question_type_id, score, activity_id)
        VALUES (?, ?, ?, ?, ?)
    ");
    $stmtInsertOpt = $conn->prepare("
        INSERT INTO Options (option_id, question_id, option_text, is_correct)
        VALUES (?, ?, ?, ?)
    ");

    foreach ($data['questions'] as $q) {
        if (!isset($q['question_text'], $q['type_name'], $q['score'], $q['options'])) {
            throw new Exception("Malformed question structure");
        }

        $stmtQType->bind_param("s", $q['type_name']);
        $stmtQType->execute();
        $res = $stmtQType->get_result();
        $row = $res->fetch_assoc();
        if (!$row) throw new Exception("Invalid type_name: " . $q['type_name']);
        $qTypeId = $row['type_id'];

        $questionId = uuid_v4();

        $stmtInsertQ->bind_param("ssiis", $questionId, $q['question_text'], $qTypeId, $q['score'], $quizId);
        if (!$stmtInsertQ->execute()) throw new Exception("Failed to insert question");

        foreach ($q['options'] as $opt) {
            $optId = uuid_v4();
            $isCorrect = $opt['is_correct'] ? 1 : 0;
            $stmtInsertOpt->bind_param("sssi", $optId, $questionId, $opt['option_text'], $isCorrect);
            if (!$stmtInsertOpt->execute()) throw new Exception("Failed to insert option");
        }
    }

    $stmtQType->close();
    $stmtInsertQ->close();
    $stmtInsertOpt->close();

    $eventTypeMap = ['SHORT' => 2, 'PRACTICE' => 3, 'LONG' => 4, 'SCREENING_EXAM' => 6];
    if (!isset($eventTypeMap[$data['quiz_type_name']])) {
        throw new Exception("Invalid quiz_type_name mapping");
    }

    $eventTypeId = $eventTypeMap[$data['quiz_type_name']];
    $location = "Good Shepard High School";

    $stmtCal = $conn->prepare("
        UPDATE CalendarEvent
           SET title = ?, description = ?, date = ?, event_type_id = ?, is_urgent = ?, location = ?
         WHERE event_id = ?
    ");
    $isUrgent = 1;
    $stmtCal->bind_param(
        "sssisss",
        $data['quiz_title'],
        $description,
        $unlockDate,
        $eventTypeId,
        $isUrgent,
        $location,
        $quizId
    );
    if (!$stmtCal->execute()) throw new Exception("Failed to update CalendarEvent");
    $stmtCal->close();

    $conn->commit();
    echo json_encode(['success' => true, 'message' => 'Quiz successfully updated.']);
    exit;
} catch (Exception $e) {
    $conn->rollback();
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => $e->getMessage()]);
}
