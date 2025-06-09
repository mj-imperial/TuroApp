<?php
header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    exit(json_encode(['success' => false, 'message' => 'Method Not Allowed']));
}

$raw  = file_get_contents('php://input');
$data = json_decode($raw, true);

if (empty($data['course_id']) || empty($data['module_name'])) {
    http_response_code(400);
    exit(json_encode(['success' => false, 'message' => 'Missing required fields']));
}

$courseId = $conn->real_escape_string($data['course_id']);
$moduleName = $conn->real_escape_string($data['module_name']);
$rawDesc = $data['module_description'] ?? null;
$moduleDescription = is_string($rawDesc)
    ? $conn->real_escape_string($rawDesc)
    : null;
$moduleId = uuid_v4();

try {
    $stmt = $conn->prepare("
        SELECT 1
        FROM `Module`
        WHERE module_name = ?
        LIMIT 1
    ");
    $stmt->bind_param('s', $moduleName);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        http_response_code(409);
        exit(json_encode([
            'success' => false,
            'message' => 'Module name already exists.'
        ]));
    }
    $stmt->close();

    $stmt = $conn->prepare("
        INSERT INTO `Module`
            (module_id, course_id, module_name, module_description)
        VALUES (?, ?, ?, ?)
    ");
    $stmt->bind_param('ssss', $moduleId, $courseId, $moduleName, $moduleDescription);
    $stmt->execute();
    $stmt->close();

    echo json_encode([
        'success' => true,
        'message' => 'Module successfully created.'
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
}
