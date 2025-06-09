<?php
header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    exit(json_encode(['success' => false, 'message' => 'Method Not Allowed']));
}

$raw  = file_get_contents('php://input');
$data = json_decode($raw, true);

if (empty($_GET['module_id'])) {
    http_response_code(400);
    exit(json_encode(['success' => false, 'message' => 'Missing moduleId']));
}
$moduleId = $_GET['module_id'];

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
if (!is_array($data['questions']) || count($data['questions']) !== (int)$data['number_of_questions']) {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'number_of_questions does not match actual questions count']);
    exit;
}

try {
    $conn->begin_transaction();

    $stmtCheck = $conn->prepare("
        SELECT 1
          FROM Activity
         WHERE module_id = ?
           AND activity_type = 'QUIZ'
           AND activity_name = ?
        LIMIT 1
    ");
    $stmtCheck->bind_param("ss", $moduleId, $data['activity_name']);
    $stmtCheck->execute();
    $stmtCheck->store_result();
    if ($stmtCheck->num_rows > 0) {
        $stmtCheck->close();
        $conn->rollback();
        http_response_code(409);
        exit(json_encode([
            'success' => false,
            'message' => 'A quiz with this name already exists in the same module.'
        ]));
    }
    $stmtCheck->close();

    $activity_id  = uuid_v4();
    $unlockDate   = date('Y-m-d H:i:s', strtotime($data['unlock_date']));
    $deadlineDate = null;
    if (!empty($data['deadline_date'])) {
        $deadlineDate = date('Y-m-d H:i:s', strtotime($data['deadline_date']));
    }
    $description = isset($data['activity_description']) ? $data['activity_description'] : null;

    if ($deadlineDate === null) {
        $stmtActivity = $conn->prepare(
            "INSERT INTO Activity (
                activity_id,
                module_id,
                activity_type,
                activity_name,
                activity_description,
                unlock_date,
                deadline_date
            ) VALUES (?, ?, 'QUIZ', ?, ?, ?, NULL)"
        );
        $stmtActivity->bind_param(
            "sssss s",
            $activity_id,
            $moduleId,
            $data['activity_name'],
            $description,
            $unlockDate,
            $unlockDate
        );
    } else {
        $stmtActivity = $conn->prepare(
            "INSERT INTO Activity (
                activity_id,
                module_id,
                activity_type,
                activity_name,
                activity_description,
                unlock_date,
                deadline_date
            ) VALUES (?, ?, 'QUIZ', ?, ?, ?, ?)"
        );
        $stmtActivity->bind_param(
            "ssssss",
            $activity_id,
            $moduleId,
            $data['activity_name'],
            $description,
            $unlockDate,
            $deadlineDate
        );
    }
    if (!$stmtActivity->execute()) {
        throw new Exception("Activity insert failed: " . $stmtActivity->error);
    }
    $stmtActivity->close();

    $stmtType = $conn->prepare("
        SELECT quiz_type_id
          FROM QuizType
         WHERE quiz_type_name = ?
    ");
    $stmtType->bind_param("s", $data['quiz_type_name']);
    $stmtType->execute();
    $resultType = $stmtType->get_result();
    $row = $resultType->fetch_assoc();
    $stmtType->close();

    if (!$row) {
        throw new Exception("Invalid quiz_type_name: " . $data['quiz_type_name']);
    }
    $quizTypeId = (int)$row['quiz_type_id'];

    $stmtQuiz = $conn->prepare("
        INSERT INTO Quiz (
            activity_id,
            number_of_attempts,
            quiz_type_id,
            time_limit,
            number_of_questions,
            overall_points,
            has_answers_shown
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
    ");
    $hasAnswersShown = $data['has_answers_shown'] ? 1 : 0;
    $stmtQuiz->bind_param(
        "siiiiii",
        $activity_id,
        $data['number_of_attempts'],
        $quizTypeId,
        $data['time_limit'],
        $data['number_of_questions'],
        $data['overall_points'],
        $hasAnswersShown
    );
    if (!$stmtQuiz->execute()) {
        throw new Exception("Quiz insert failed: " . $stmtQuiz->error);
    }
    $stmtQuiz->close();

    $stmtSelectQuestionType = $conn->prepare("
        SELECT type_id
          FROM QuestionType
         WHERE type_name = ?
    ");
    $stmtInsertQuestion = $conn->prepare("
        INSERT INTO Question (
            question_id,
            question_text,
            question_type_id,
            score,
            activity_id
        ) VALUES (?, ?, ?, ?, ?)
    ");
    $stmtInsertOption = $conn->prepare("
        INSERT INTO Options (
            option_id,
            question_id,
            option_text,
            is_correct
        ) VALUES (?, ?, ?, ?)
    ");

    foreach ($data['questions'] as $q) {
        if (
            !isset($q['question_text'], $q['type_name'], $q['score'], $q['options'])
            || !is_array($q['options'])
        ) {
            throw new Exception("Malformed question data");
        }

        $stmtSelectQuestionType->bind_param("s", $q['type_name']);
        $stmtSelectQuestionType->execute();
        $resQType = $stmtSelectQuestionType->get_result();
        $qTypeRow = $resQType->fetch_assoc();
        if (!$qTypeRow) {
            throw new Exception("Invalid question type: " . $q['type_name']);
        }
        $questionTypeId = (int)$qTypeRow['type_id'];

        $questionId = uuid_v4();

        $stmtInsertQuestion->bind_param(
            "ssiis",
            $questionId,
            $q['question_text'],
            $questionTypeId,
            $q['score'],
            $activity_id
        );
        if (!$stmtInsertQuestion->execute()) {
            throw new Exception("Question insert failed: " . $stmtInsertQuestion->error);
        }

        foreach ($q['options'] as $opt) {
            if (!isset($opt['option_text'], $opt['is_correct'])) {
                throw new Exception("Malformed option data");
            }
            $optionId = uuid_v4();
            $isCorrect = $opt['is_correct'] ? 1 : 0;
            $stmtInsertOption->bind_param(
                "sssi",
                $optionId,
                $questionId,
                $opt['option_text'],
                $isCorrect
            );
            if (!$stmtInsertOption->execute()) {
                throw new Exception("Option insert failed: " . $stmtInsertOption->error);
            }
        }
    }

    $stmtSelectQuestionType->close();
    $stmtInsertQuestion->close();
    $stmtInsertOption->close();

    $mapping = [
        'SHORT' => 2,
        'PRACTICE' => 3,
        'LONG' => 4,
        'SCREENING_EXAM' => 6
    ];
    $quizTypeName = $data['quiz_type_name'];
    if (!isset($mapping[$quizTypeName])) {
        throw new Exception("No calendar event type mapping for quiz_type_name: $quizTypeName");
    }
    $eventTypeId = $mapping[$quizTypeName];

    $eventDate = $unlockDate;

    $stmtInsertCalendar = $conn->prepare("
        INSERT INTO CalendarEvent (
            event_id,
            title,
            description,
            date,
            event_type_id,
            is_urgent,
            location
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
    ");
    $isUrgent = 1;
    $location = "Good Shepard High School";
    $stmtInsertCalendar->bind_param(
        "ssssiss",
        $activity_id,
        $data['activity_name'],
        $description,
        $eventDate,
        $eventTypeId,
        $isUrgent,
        $location
    );
    if (!$stmtInsertCalendar->execute()) {
        throw new Exception("CalendarEvent insert failed: " . $stmtInsertCalendar->error);
    }
    $stmtInsertCalendar->close();

    $conn->commit();

    echo json_encode([
        'success' => true,
        'message' => "Quiz successfully created."
    ]);
    exit;
} catch (Exception $e) {
    $conn->rollback();
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
}
