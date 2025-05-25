<?php
header('Content-Type: application/json');
$configFile = __DIR__ . '/config.php';
if (! file_exists($configFile)) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => "Config file not found"
    ]);
    exit;
}
require_once $configFile;

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $input = $_GET;
} elseif (! empty($_POST)) {
    $input = $_POST;
} else {
    $raw   = file_get_contents('php://input');
    $input = json_decode($raw, true) ?: [];
}

if (! isset($_GET['activity_id']) || empty($_GET['activity_id'])) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => 'Missing activity_id'
    ]);
    exit;
}
$activityId = $_GET['activity_id'];

try{
    $sql = "
        SELECT 
            A.activity_id,
            M.module_name,
            A.activity_type,
            A.activity_name,
            A.activity_description,
            A.unlock_date,
            A.deadline_date,
            Q.number_of_attempts,
            QT.quiz_type_name,
            Q.time_limit,
            Q.is_passed,
            Q.number_of_questions,
            Q.overall_points
        FROM `Activity` AS A
        INNER JOIN `Quiz` AS Q
            on A.activity_id = Q.activity_id
        INNER JOIN `Quiztype` AS QT
            on Q.quiz_type_id = QT.quiz_type_id
        INNER JOIN `Module` AS M
            on M.module_id = A.module_id
        WHERE A.activity_id = ?
        LIMIT 1
    ";
    $stmt = $conn->prepare($sql);
    if (! $stmt) {
        http_response_code(500);
        jsonResponse(['success'=>false,'message'=>'Database prepare failed'],500);
        return;
    }
    $stmt->bind_param('s', $input['activity_id']);
    if (! $stmt->execute()) {
        http_response_code(500);
        jsonResponse(['success'=>false,'message'=>'Database execute failed'],500);
        return;
    }
    $result = $stmt->get_result();
    $quiz = $result->fetch_assoc();
    $stmt->close();

    if (! $quiz) {
        http_response_code(404);
        echo json_encode([
            'success' => false,
            'message' => 'Quiz not found'
        ]);
        exit;
    }

    header('Content-Type: application/json');
    echo json_encode($quiz);
    exit;
}catch (mysqli_sql_exception $e) {
    error_log("DB error in get_user.php: " . $e->getMessage());
    jsonResponse(['success'=>false,'error'=>'Internal Server Error'], 500);
}
