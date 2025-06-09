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

if (empty($input['module_id'])) {
    http_response_code(400);
    jsonResponse([ 'success' => false, 'message' => 'Invalid request: activity_id' ], 400);
}

$moduleId = $input['module_id'];

try{
    $sql = "
        SELECT 
            m.course_id,
            m.module_name,
            m.module_description
        FROM `Module` AS m
        WHERE m.module_id = ?
    ";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param('s', $moduleId);
    $stmt->execute();

    $result = $stmt->get_result();
    $module = $result->fetch_assoc();
    $stmt->close();

    if (! $module) {
        http_response_code(404);
        echo json_encode([
            'success' => false,
            'message' => 'Module not found'
        ]);
        exit;
    }
    header('Content-Type: application/json');
    echo json_encode($module);
    exit;
}catch (mysqli_sql_exception $e) {
    error_log("DB error in get_user.php: " . $e->getMessage());
    jsonResponse(['success'=>false,'error'=>'Internal Server Error'], 500);
}