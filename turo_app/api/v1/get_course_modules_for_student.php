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

if (empty($input['course_id']) || empty($input['student_id'])) {
    http_response_code(400);
    jsonResponse([ 'success' => false, 'message' => 'Invalid request: course_id, student_id' ], 400);
}

$courseId = $input['course_id'];
$studentId = $input['student_id'];

try{
    $sql = "
        SELECT 
            M.module_id,
            M.module_name,
            M.module_description,
            M.module_picture,
            MP.progress
        FROM `Module` AS M
        INNER JOIN `moduleprogress` AS MP
            ON M.module_id = MP.module_id AND MP.student_id = ?
        WHERE M.course_id = ?
    ";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param('ss', $studentId, $courseId);
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
    $modules = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    $stmt->close();

    header('Content-Type: application/json');
    echo json_encode([
      'success' => true,
      'modules' => $modules
    ]);
    exit;
}catch (mysqli_sql_exception $e) {
    error_log("DB error in get_user.php: " . $e->getMessage());
    jsonResponse(['success'=>false,'error'=>'Internal Server Error'], 500);
}