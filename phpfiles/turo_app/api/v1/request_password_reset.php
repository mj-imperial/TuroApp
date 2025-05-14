<?php
require_once '/xampp/htdocs/turo_app/api/v1/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'error' => 'Method Not Allowed'], 405);
}

$email = trim($_POST['email'] ?? '');
if ($email === '' || ! filter_var($email, FILTER_VALIDATE_EMAIL)) {
    jsonResponse(['success' => false, 'error' => 'Valid email is required'], 400);
}

$stmt = $conn->prepare("
    SELECT user_id
    FROM `User`
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

$code      = str_pad(random_int(0, 999999), 6, '0', STR_PAD_LEFT);
$codeHash  = password_hash($code, PASSWORD_DEFAULT);
$expiresAt = (new DateTime('+10 minutes'))->format('Y-m-d H:i:s');

$upsert = $conn->prepare("
    INSERT INTO password_resets (user_id, code_hash, expires_at)
    VALUES (?, ?, ?)
    ON DUPLICATE KEY UPDATE
      code_hash  = VALUES(code_hash),
      expires_at = VALUES(expires_at)
");
$upsert->bind_param('iss', $userId, $codeHash, $expiresAt);
$upsert->execute();
$upsert->close();

$subject = "Your Password Reset Code";
$body    = "Hello,\n\nYour password reset code is: {$code}\nThis code expires in 10 minutes.\n\n– The Turo App Team";

if (! sendEmail($email, $subject, $body)) {
    jsonResponse(['success' => false, 'error' => 'Failed to send email'], 500);
}

jsonResponse(['success' => true]);
