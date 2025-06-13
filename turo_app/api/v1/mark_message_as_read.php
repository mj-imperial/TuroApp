<?php
header('Content-Type: application/json');

$configFile = __DIR__ . '/config.php';
if (!file_exists($configFile)) {
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Config file not found']);
    exit;
}
require_once $configFile;

$input = $_SERVER['REQUEST_METHOD'] === 'POST' ? ($_POST ?: json_decode(file_get_contents('php://input'), true)) : [];

$messageId = $input['message_id'] ?? null;
$userId = $input['user_id'] ?? null;

if (!$messageId || !$userId) {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'Missing message_id or user_id']);
    exit;
}

try {
    $stmt = $conn->prepare("
        UPDATE MessageUserState
        SET is_read = TRUE
        WHERE message_id = ? AND user_id = ?
    ");
    if (!$stmt) {
        throw new Exception("Prepare failed: " . $conn->error);
    }

    $stmt->bind_param("ss", $messageId, $userId);
    $success = $stmt->execute();
    $stmt->close();

    if ($success && $conn->affected_rows > 0) {
        echo json_encode(['success' => true, 'message' => 'Message marked as read']);
    } else {
        echo json_encode(['success' => false, 'message' => 'No update occurred']);
    }

    exit;
} catch (Exception $e) {
    error_log("Error marking message as read: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Internal server error']);
    exit;
}
