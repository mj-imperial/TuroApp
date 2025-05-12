<?php

require_once __DIR__ . '/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'error' => 'Method Not Allowed'], 405);
}

$email = trim($_POST['email']       ?? '');
$oldPassword =  $_POST['oldPassword'] ?? '';
$newPassword =  $_POST['newPassword'] ?? '';

if ($email === '' || $newPassword === '') {
    jsonResponse(['success' => false, 'error' => 'Email and new password are required'], 400);
}
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    jsonResponse(['success' => false, 'error' => 'Invalid email format'], 400);
}

$stmt = $conn->prepare("
    SELECT user_id, password AS current_hash, requires_password_change
    FROM `user`
    WHERE email = ?
    LIMIT 1
");
$stmt->bind_param('s', $email);
$stmt->execute();
$stmt->bind_result($userId, $currentHash, $requiresChange);
if (! $stmt->fetch()) {
    jsonResponse(['success' => false, 'error' => 'No account found'], 404);
}
$stmt->close();

if (! (bool)$requiresChange) {
    if ($oldPassword === '' || ! password_verify($oldPassword, $currentHash)) {
        jsonResponse(['success' => false, 'error' => 'Current password is incorrect'], 401);
    }
}

$newHash = password_hash($newPassword, PASSWORD_DEFAULT);
$stmt = $conn->prepare("
    UPDATE `user`
    SET password = ?, requires_password_change = 0
    WHERE user_id = ?
");
$stmt->bind_param('si', $newHash, $userId);
$stmt->execute();
$stmt->close();

$stmt = $conn->prepare("
    DELETE FROM password_resets
    WHERE user_id = ?
");
$stmt->bind_param('i', $userId);
$stmt->execute();
$stmt->close();

jsonResponse(['success' => true]);
