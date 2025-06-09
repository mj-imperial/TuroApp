<?php
header('Content-Type: application/json');
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

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $input = $_GET;
} elseif (! empty($_POST)) {
    $input = $_POST;
} else {
    $raw   = file_get_contents('php://input');
    $input = json_decode($raw, true) ?: [];
}

if (empty($input['student_id'])) {
    http_response_code(400);
    jsonResponse([ 'success' => false, 'message' => 'Invalid request: student_id' ], 400);
}

$studentId = $input['student_id'];

try{
    $sql = "
        SELECT
            sb.badge_id,
            sb.is_unlocked,
            b.badge_name,
            b.badge_description,
            b.badge_image,
            b.points_required,
            sp.total_points
        FROM `Student_badges` AS sb
        INNER JOIN `Badges` AS b
            ON sb.badge_id = b.badge_id
        INNER JOIN `Studentprogress` AS sp
            on sb.student_id = sp.student_id
        WHERE sb.student_id = ?
    ";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param('s', $studentId);
    if (! $stmt) {
        http_response_code(500);
        jsonResponse(['success'=>false,'message'=>'Database prepare failed'],500);
        return;
    }
    if (! $stmt->execute()) {
        http_response_code(500);
        jsonResponse(['success'=>false,'message'=>'Database execute failed'],500);
        return;
    }
    $badges = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    $stmt->close();

    header('Content-Type: application/json');
    echo json_encode([
      'success' => true,
      'badges' => $badges
    ]);
    exit;
}catch (mysqli_sql_exception $e) {
    error_log("DB error in get_user.php: " . $e->getMessage());
    jsonResponse(['success'=>false,'error'=>'Internal Server Error'], 500);
}