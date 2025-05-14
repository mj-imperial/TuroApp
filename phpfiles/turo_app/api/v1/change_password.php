<?php
// change_password.php
date_default_timezone_set('Asia/Manila');
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
    echo json_encode([
        'success' => false,
        'message' => 'Method Not Allowed'
    ]);
    exit;
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
    echo json_encode([
        'success' => false,
        'message' => 'Invalid request: action missing'
    ]);
    exit;
}

// 5) Dispatch
switch ($input['action']) {
    case 'resetPassword':
        if (empty($input['email']) || empty($input['new_password'])) {
            http_response_code(400);
            echo json_encode([
                'success' => false,
                'message' => 'Email and new_password are required'
            ]);
            exit;
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
            echo json_encode([
                'success' => false,
                'message' => 'Email, old_password, and new_password are required'
            ]);
            exit;
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
        echo json_encode([
            'success' => false,
            'message' => 'Unknown action'
        ]);
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
        echo json_encode([
            'success' => true,
            'message' => 'Password reset successfully'
        ]);
    } else {
        http_response_code(500);
        echo json_encode([
            'success' => false,
            'message' => 'Failed to reset password'
        ]);
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
        echo json_encode([
            'success' => false,
            'message' => 'User not found'
        ]);
        return;
    }
    $stmt->close();

    // 2) Verify old password
    if (! password_verify($oldPassword, $existingHash)) {
        http_response_code(401);
        echo json_encode([
            'success' => false,
            'message' => 'Old password is incorrect'
        ]);
        return;
    }

    // 3) Update to new password
    $newHash = password_hash($newPassword, PASSWORD_DEFAULT);
    $updSql  = "UPDATE `User` SET `password_hash` = ? WHERE `email` = ?";
    $updStmt = $conn->prepare($updSql);
    $updStmt->bind_param('ss', $newHash, $email);

    if ($updStmt->execute()) {
        echo json_encode([
            'success' => true,
            'message' => 'Password changed successfully'
        ]);
    } else {
        http_response_code(500);
        echo json_encode([
            'success' => false,
            'message' => 'Failed to change password'
        ]);
    }
    $updStmt->close();
}