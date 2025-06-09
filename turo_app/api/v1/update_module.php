<?php
header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    exit(json_encode(['success' => false, 'message' => 'Method Not Allowed']));
}

$raw  = file_get_contents('php://input');
$data = json_decode($raw, true);

if (empty($_GET['module_id'])) {
    http_response_code(400);
    exit(json_encode(['success' => false, 'message' => 'Missing moduleId']));
}
$moduleId = $_GET['module_id'];

$requiredFields = ['module_name', 'module_description'];
foreach ($requiredFields as $field) {
    if (!array_key_exists($field, $data)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => "Missing field: $field"]);
        exit;
    }
}

try{
    $conn->begin_transaction();

    $stmtCheck = $conn->prepare("
        SELECT 1
        FROM Module
        WHERE module_name = ? AND module_id != ?
        LIMIT 1
    ");
    $stmtCheck->bind_param("ss", $data['module_name'], $moduleId);
    $stmtCheck->execute();
    $stmtCheck->store_result();
    if ($stmtCheck->num_rows > 0) {
        $stmtCheck->close();
        $conn->rollback();
        http_response_code(409);
        exit(json_encode([
            'success' => false,
            'message' => 'A quiz with this name already exists in the same module.'
        ]));
    }
    $stmtCheck->close();

    $sql = "
        UPDATE Module
        SET module_name = ?, module_description = ?
        WHERE module_id = ?;
    ";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param('sss', $data['module_name'], $data['module_description'], $moduleId);
    $stmt->execute();
    $conn->commit();

    echo json_encode(['success'=> true,'message'=> 'Module successfully']);
}catch (Exception $e) {
    $conn->rollback();
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
}