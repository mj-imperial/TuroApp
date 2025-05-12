<?php
require_once __DIR__ . '/config.php';
session_start();

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
  http_response_code(405);
  header('Allow: POST');
  jsonResponse(['success'=>false,'error'=>'Method Not Allowed'], 405);
}

$email = trim($_POST['email'] ?? '');
if ($email === '' || ! filter_var($email, FILTER_VALIDATE_EMAIL)) {
  jsonResponse(['success'=>false,'error'=>'Invalid email'], 400);
}

$stmt = $conn->prepare("
  SELECT user_id, email, first_name, last_name
  FROM `user`
  WHERE email = ?
  LIMIT 1
");
$stmt->bind_param('s', $email);
$stmt->execute();
$stmt->bind_result($userId, $emailFromDb, $firstName, $lastName);
if (! $stmt->fetch()) {
  jsonResponse(['success'=>false,'error'=>'No account found'], 404);
}
$stmt->close();

$code = str_pad(random_int(0,999999), 6, '0', STR_PAD_LEFT);
$hashedCode= password_hash($code, PASSWORD_DEFAULT);
$expiresAt = (new DateTime('+10 minutes'))->format('Y-m-d H:i:s');

$stmt = $conn->prepare("
  INSERT INTO password_resets (user_id, code_hash, expires_at)
  VALUES (?, ?, ?)
  ON DUPLICATE KEY UPDATE
    code_hash  = VALUES(code_hash),
    expires_at = VALUES(expires_at)
");
$stmt->bind_param('iss', $userId, $hashedCode, $expiresAt);
$stmt->execute();
$stmt->close();

$subject = "Your password reset code";
$body    = "Hello {$firstName} {$lastName},\n\nYour code is {$code}. It expires in 10 minutes.";
if (! sendEmail($emailFromDb, $subject, $body)) {
  jsonResponse(['success'=>false,'error'=>'Failed to send email'], 500);
}

jsonResponse(['success'=>true]);
