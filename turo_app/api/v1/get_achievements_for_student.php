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

if (empty($input['student_id'])) {
    http_response_code(400);
    jsonResponse([ 'success' => false, 'message' => 'Invalid request: student_id' ], 400);
}

$studentId = $input['student_id'];

try{
    $sql = "
        SELECT
            sa.achievement_id,
            sa.is_unlocked,
            a.achievement_name,
            a.achievement_description,
            a.achievement_image,
            act.condition_name,
            a.condition_value
        FROM `student_achievements` AS sa
        INNER JOIN `achievements` AS a
            ON sa.achievement_id = a.achievement_id
        INNER JOIN `achievementconditiontype` AS act
            ON a.condition_type_id = act.condition_type_id
        WHERE sa.student_id = ?
    ";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param('s', $studentId);
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
    $achievements = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    $stmt->close();

    header('Content-Type: application/json');
    echo json_encode([
      'success' => true,
      'achievements' => $achievements
    ]);
    exit;
}catch (mysqli_sql_exception $e) {
    error_log("DB error in get_user.php: " . $e->getMessage());
    jsonResponse(['success'=>false,'error'=>'Internal Server Error'], 500);
}