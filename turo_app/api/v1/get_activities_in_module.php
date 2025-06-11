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

if (empty($input['module_id'])) {
    http_response_code(400);
    jsonResponse([ 'success' => false, 'message' => 'Invalid request: course_id' ], 400);
}

$moduleId = $input['module_id'];

try{
    $sql = "
        SELECT
            A.module_id,
            A.activity_id,
            A.activity_type,
            A.activity_name,
            QT.quiz_type_name,
            A.activity_description,
            A.unlock_date,
            A.deadline_date
        FROM `Activity` AS A
        LEFT JOIN `Quiz` AS Q 
            ON A.activity_id = Q.activity_id
        LEFT JOIN `Quiztype` AS QT
            ON Q.quiz_type_id = QT.quiz_type_id
        WHERE module_id = ?
    ";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param('s', $moduleId);
    if (! $stmt) {
        http_response_code(500);
        jsonResponse(['success'=>false,'message'=>'Database prepare failed'],500);
        return;
    }
    if (! $stmt->execute()) {
        http_response_code(500);
        jsonResponse(['success'=>false,'message'=>'Database execute failed'],500);
        return;
    }
    $activities = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    $stmt -> close();

    header('Content-Type: application/json');
    echo json_encode([
      'success' => true,
      'activities' => $activities
    ]);
    exit;
}catch (mysqli_sql_exception $e) {
    error_log("DB error in get_user.php: " . $e->getMessage());
    jsonResponse(['success'=>false,'error'=>'Internal Server Error'], 500);
}
