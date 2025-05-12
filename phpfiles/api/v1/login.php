<?php

session_start();

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    header('Allow: POST');
    header('Content-Type: application/json; charset=UTF-8');
    echo json_encode([
        'success' => false,
        'error'   => 'Method Not Allowed'
    ]);
    exit;
}

header('Content-Type: application/json; charset=UTF-8');
require_once __DIR__ . '/config.php';

$email    = trim($_POST['email']    ?? '');
$password =           $_POST['password'] ?? '';

if ($email === '' || $password === '') {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'error'   => 'Email and password are required.'
    ]);
    exit;
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'error'   => 'Invalid email format.'
    ]);
    exit;
}

try {
    $stmt = $conn->prepare("
        SELECT
            u.user_id,
            u.email,
            u.first_name,
            u.last_name,
            u.password   AS password_hash,
            u.requires_password_change,
            u.role_id,
            r.role_name
        FROM `user` AS u
        JOIN `userrole` AS r
          ON u.role_id = r.role_id
        WHERE u.email = ?
        LIMIT 1
    ");
    $stmt->bind_param('s', $email);
    $stmt->execute();
    $stmt->bind_result(
        $userId,
        $emailFromDb,
        $firstName,
        $lastName,
        $passwordHash,
        $requiresPasswordChange,
        $roleId,
        $roleName
    );

    if (! $stmt->fetch()) {
        http_response_code(401);
        echo json_encode([
            'success' => false,
            'error'   => 'Invalid credentials.'
        ]);
        exit;
    }

    $stmt->close();
} catch (mysqli_sql_exception $e) {
    error_log('DB Query error: ' . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error'   => 'Internal Server Error'
    ]);
    exit;
}

if (! password_verify($password, $passwordHash)) {
    http_response_code(401);
    echo json_encode([
        'success' => false,
        'error'   => 'Invalid credentials.'
    ]);
    exit;
}

$_SESSION['user_id'] = $userId;

echo json_encode([
    'success'                   => true,
    'user_id'                   => $userId,
    'email'                     => $emailFromDb,
    'first_name'                => $firstName,
    'last_name'                 => $lastName,
    'role_id'                   => $roleId,
    'role'                      => $roleName,
    'requires_password_change'  => (bool) $requiresPasswordChange
]);
exit;
