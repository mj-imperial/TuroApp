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
    $inboxId = isset($_GET['inbox_id']) ? $_GET['inbox_id'] : null;
    $userId = isset($_GET['user_id']) ? $_GET['user_id'] : null;
} elseif ($_SERVER['REQUEST_METHOD'] === 'POST' && !empty($_POST)) {
    $inboxId = $_POST['inbox_id'] ?? null;
    $userId = $_POST['user_id'] ?? null;
} else {
    $raw   = file_get_contents('php://input');
    $input = json_decode($raw, true) ?: [];
    $inboxId = $input['inbox_id'] ?? null;
    $userId = $input['user_id'] ?? null;
}

if (empty($inboxId)) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => "Invalid request: inbox_id is required"
    ]);
    exit;
}

try{
    $sql = "
        UPDATE InboxParticipant
        SET is_deleted = 1
        WHERE inbox_id = ? AND participant_id = ?
    ";
    $stmt = $conn->prepare($sql);
    if (! $stmt) {
        http_response_code(500);
        jsonResponse(['success'=>false,'message'=>'Database prepare failed'],500);
        return;
    }
    $stmt->bind_param("ss", $inboxId, $userId);
    if (! $stmt->execute()) {
        http_response_code(500);
        jsonResponse(['success'=>false,'message'=>'Database execute failed'],500);
        return;
    }

    echo json_encode([
      'success' => true,
      'message' => "Inbox hidden from user."
    ]);
    exit;
}catch (mysqli_sql_exception $e) {
    error_log("DB error in get_user.php: " . $e->getMessage());
    jsonResponse(['success'=>false,'error'=>'Internal Server Error'], 500);
}