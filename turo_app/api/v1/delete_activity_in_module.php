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

if ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    $activityId = isset($_GET['activity_id']) ? $_GET['activity_id'] : null;
} elseif ($_SERVER['REQUEST_METHOD'] === 'POST' && !empty($_POST)) {
    $activityId = $_POST['activity_id'] ?? null;
} else {
    $raw   = file_get_contents('php://input');
    $input = json_decode($raw, true) ?: [];
    $activityId = $input['activity_id'] ?? null;
}

if (empty($activityId)) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => "Invalid request: activity_id is required"
    ]);
    exit;
}

try{
    $sql = "
        DELETE FROM `Activity` WHERE activity_id = ?;
    ";
    $stmt = $conn->prepare($sql);
    if (! $stmt) {
        http_response_code(500);
        jsonResponse(['success'=>false,'message'=>'Database prepare failed'],500);
        return;
    }

    $stmt->bind_param('s', $activityId);
    if (! $stmt->execute()) {
        http_response_code(500);
        jsonResponse(['success'=>false,'message'=>'Database execute failed'],500);
        return;
    }
    $stmt->close();

    echo json_encode([
      'success' => true,
      'message' => "Activity successfully deleted."
    ]);
}catch (mysqli_sql_exception $e) {
    error_log("DB error in get_user.php: " . $e->getMessage());
    jsonResponse(['success'=>false,'error'=>'Internal Server Error'], 500);
}