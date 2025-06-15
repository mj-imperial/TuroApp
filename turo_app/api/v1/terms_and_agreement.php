<?php
header('Content-Type: application/json; charset=utf-8');
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

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    jsonResponse(['success' => false, 'error' => 'Method Not Allowed'], 405);
}

$input = [];
if (!empty($_POST)) {
    $input = $_POST;
} else {
    $raw = file_get_contents('php://input');
    $input = json_decode($raw, true) ?: [];
}

if (!isset($input['user_id']) || !isset($input['agreed_to_terms'])) {
    http_response_code(400);
    jsonResponse(['success' => false, 'message' => 'user_id and agreed_to_terms are required'], 400);
}

$userId = $input['user_id'];
$agreedToTerms = filter_var($input['agreed_to_terms'], FILTER_VALIDATE_BOOLEAN); // 💡 ensures true/false

changeAgreementStatus($conn, $userId, $agreedToTerms);

function changeAgreementStatus(mysqli $conn, string $userId, bool $agreedToTerms) {
    $agreedInt = $agreedToTerms ? 1 : 0;

    $stmt = $conn->prepare("
        UPDATE `User`
        SET `agreed_to_terms` = ?
        WHERE `user_id` = ?
    ");
    $stmt->bind_param('is', $agreedInt, $userId);

    if ($stmt->execute()) {
        jsonResponse([
            'success' => true,
            'message' => 'Terms Agreement changed successfully',
            'rows_affected' => $stmt->affected_rows
        ]);
    } else {
        http_response_code(500);
        jsonResponse(['success' => false, 'message' => 'Failed to change Terms Agreement']);
    }

    $stmt->close();
}