<?php
ini_set('display_errors','0');
ini_set('log_errors','1');

header('Content-Type: application/json; charset=UTF-8');
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
    http_response_code(405);
    header('Allow: POST');
    header('Content-Type: application/json; charset=UTF-8');
    jsonResponse([ 'success' => false, 'error'   => 'Method Not Allowed' ], 405);
}

$email = trim($_POST['email'] ?? '');
$password = $_POST['password'] ?? '';

if ($email === '' || $password === '') {
    http_response_code(400);
    jsonResponse([ 'success' => false, 'error'   => 'Email and password are required.' ], 400);
}

if (! filter_var($email, FILTER_VALIDATE_EMAIL)) {
    http_response_code(400);
    jsonResponse(['success' => false, 'error'   => 'Invalid email format.'], 400);
}

try {
    $stmt = $conn->prepare("
        SELECT 
            u.user_id,
            u.email,
            u.password_hash,
            u.first_name,
            u.last_name,
            u.requires_password_change,
            u.role_id,
            u.agreed_to_terms,
            u.profile_pic,
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
        $passwordHash,
        $firstName,
        $lastName,
        $requiresPasswordChange,
        $roleId,
        $agreedToTerms,
        $profilePic,
        $roleName
    );

    if (! $stmt->fetch()) {
        http_response_code(401);
        jsonResponse(['success' => false, 'error'   => 'Invalid credentials.'], 401);
    }

    $stmt->close();
} catch (mysqli_sql_exception $e) {
    error_log($e->getMessage());
    http_response_code(500);
    jsonResponse(['success' => false, 'error'   => 'Internal Server Error'], 500);
}

if (! password_verify($password, $passwordHash)) {
    http_response_code(401);
    jsonResponse(['success' => false, 'error'   => 'Invalid credentials.'], 401);
}
jsonResponse([ 
    'success' => true, 
    'user_id' => $userId, 
    'email' => $emailFromDb, 
    'first_name'  => $firstName, 
    'last_name' => $lastName, 
    'role_id'   => $roleId, 
    'agreed_to_terms' => (bool) $agreedToTerms,
    'role'  => $roleName, 
    'requires_password_change' => (bool) $requiresPasswordChange,
    'profile_pic' => $profilePic
]);