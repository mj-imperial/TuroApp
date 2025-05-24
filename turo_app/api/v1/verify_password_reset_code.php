<?php
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

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'error' => 'Method Not Allowed'], 405);
}

$email = trim($_POST['email'] ?? '');
$code  = trim($_POST['code']  ?? '');

if ($email === '' || $code === '') {
    jsonResponse(['success' => false, 'error' => 'Email and code are required'], 400);
}
if (! filter_var($email, FILTER_VALIDATE_EMAIL)) {
    jsonResponse(['success' => false, 'error' => 'Invalid email format'], 400);
}

$stmt = $conn->prepare("
    SELECT user_id
    FROM `user`
    WHERE email = ?
    LIMIT 1
");
$stmt->bind_param('s', $email);
$stmt->execute();
$stmt->bind_result($userId);
if (! $stmt->fetch()) {
    jsonResponse(['success' => false, 'error' => 'No account found'], 404);
}
$stmt->close();

$stmt = $conn->prepare("
    SELECT code_hash, expires_at
    FROM password_resets
    WHERE user_id = ?
    LIMIT 1
");
$stmt->bind_param('i', $userId);
$stmt->execute();
$stmt->bind_result($codeHash, $expiresAt);
if (! $stmt->fetch()) {
    jsonResponse(['success' => false, 'error' => 'No reset request found'], 404);
}
$stmt->close();

if (new DateTime() > new DateTime($expiresAt)) {
    jsonResponse(['success' => false, 'error' => 'Code has expired'], 400);
}

if (! password_verify($code, $codeHash)) {
    jsonResponse(['success' => false, 'error' => 'Invalid code'], 400);
}

jsonResponse(['success' => true, 'message' => 'Verified code']);