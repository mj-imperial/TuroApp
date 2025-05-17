<?php
require_once __DIR__ . '/../../vendor/autoload.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

function sendEmail(string $to, string $subject, string $body): bool {
    $mail = new PHPMailer(true);
    try {
        $mail->isSMTP();
        $mail->Host       = SMTP_HOST;
        $mail->SMTPAuth   = true;
        $mail->Username   = SMTP_USER;
        $mail->Password   = SMTP_PASS;
        $mail->SMTPSecure = SMTP_SECURE;
        $mail->Port       = SMTP_PORT;

        $mail->setFrom(MAIL_FROM_EMAIL, MAIL_FROM_NAME);
        $mail->addAddress($to);
        $mail->isHTML(false);
        $mail->Subject = $subject;
        $mail->Body    = $body;

        $mail->send();
        return true;
    } catch (Exception $e) {
        error_log("Mail error to {$to}: " . $mail->ErrorInfo);
        return false;
    }
}

function jsonResponse(array $data, int $status = 200): void {
    http_response_code($status);
    header('Content-Type: application/json; charset=UTF-8');
    echo json_encode($data);
    exit;
}

    /**
 * Resets the user's password without verifying the old one.
 */
function resetPassword(mysqli $conn, string $email, string $newPassword) {
    $hashed = password_hash($newPassword, PASSWORD_DEFAULT);
    $sql    = "UPDATE `User` SET `password_hash` = ? WHERE `email` = ?";
    $stmt   = $conn->prepare($sql);
    $stmt->bind_param('ss', $hashed, $email);

    
    if ($stmt->execute()) {
        jsonResponse([ 'success' => true, 'message' => 'Password reset successfully' ]);
    } else {
        http_response_code(500);
        jsonResponse([ 'success' => false, 'message' => 'Failed to reset password' ], 500);
    }
    $stmt->close();
}

/**
 * Changes the user's default password, verifying the old password first.
 */
function changeDefaultPassword(
    mysqli $conn,
    string $email,
    string $oldPassword,
    string $newPassword
) {
    // 1) Fetch existing hash
    $sql  = "SELECT `password_hash` FROM `User` WHERE `email` = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param('s', $email);
    $stmt->execute();
    $stmt->bind_result($existingHash);

    if (! $stmt->fetch()) {
        http_response_code(404);
        jsonResponse([ 'success' => false, 'message' => 'User not found' ], 400);
    }
    $stmt->close();

    // 2) Verify old password
    if (! password_verify($oldPassword, $existingHash)) {
        http_response_code(401);
        jsonResponse(['success' => false, 'message' => 'Old password is incorrect'], 401);
    }

    // 3) Update to new password
    $newHash = password_hash($newPassword, PASSWORD_DEFAULT);
    $updSql  = "UPDATE `User` SET `password_hash` = ?, `requires_password_change` = 0 WHERE `email` = ?";
    $updStmt = $conn->prepare($updSql);
    $updStmt->bind_param('ss', $newHash, $email);

    if ($updStmt->execute()) {
        jsonResponse([ 'success' => true, 'message' => 'Password changed successfully' ]);
    } else {
        http_response_code(500);
        jsonResponse(['success' => false, 'message' => 'Failed to change password']);
    }
    $updStmt->close();
}
