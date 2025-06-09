<?php
header('Content-Type: application/json; charset=UTF-8');

// 1) Load config (defines $conn as mysqli)
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

// 2) Only allow POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    jsonResponse(['success'=>false,'error'=>'Method Not Allowed'], 405);
}

// 3) Read incoming data (form‐encoded or JSON)
if (! empty($_POST)) {
    $input = $_POST;
} else {
    $raw   = file_get_contents('php://input');
    $input = json_decode($raw, true) ?: [];
}

// 4) Validate `action`
if (empty($input['action'])) {
    http_response_code(400);
    jsonResponse([ 'success' => false, 'message' => 'Invalid request: action missing' ], 400);
}

// 5) Dispatch
switch ($input['action']) {
    case 'resetPassword':
        if (empty($input['email']) || empty($input['new_password'])) {
            http_response_code(400);
            jsonResponse(['success' => false, 'message' => 'Email and new_password are required'], 400);
        }
        resetPassword($conn, $input['email'], $input['new_password']);
        break;

    case 'changeDefaultPassword':
        if (
            empty($input['email']) ||
            empty($input['old_password']) ||
            empty($input['new_password'])
        ) {
            http_response_code(400);
            jsonResponse([ 'success' => false, 'message' => 'Email, old_password, and new_password are required' ], 400);
        }
        changeDefaultPassword(
            $conn,
            $input['email'],
            $input['old_password'],
            $input['new_password']
        );
        break;

    default:
        http_response_code(400);
        jsonResponse([ 'success' => false, 'message' => 'Unknown action' ], 400);
}

