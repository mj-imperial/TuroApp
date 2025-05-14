<?php
session_start();
require_once __DIR__ . '/config.php';
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    jsonResponse(['success'=>false,'error'=>'Method Not Allowed'], 405);
}

$userId = $_SESSION['user_id'] 
       ?? trim($_GET['user_id'] ?? '');
if ($userId === '') {
    jsonResponse(['success'=>false,'error'=>'Missing user_id'], 400);
}

try {
    $stmt = $conn->prepare("
        SELECT u.*, r.role_name AS roleName
        FROM `User` u
        JOIN `UserRole` r
          ON u.role_id = r.role_id
        WHERE u.user_id = ?
        LIMIT 1
    ");
    $stmt->bind_param('s', $userId);
    $stmt->execute();

    $result = $stmt->get_result();
    $user = $result->fetch_assoc();
    if (! $user) {
        jsonResponse(['success'=>false,'error'=>'User not found'], 404);
    }

    jsonResponse([
      'success' => true,
      'user'    => $user
    ]);

} catch (mysqli_sql_exception $e) {
    error_log("DB error in get_user.php: " . $e->getMessage());
    jsonResponse(['success'=>false,'error'=>'Internal Server Error'], 500);
}
