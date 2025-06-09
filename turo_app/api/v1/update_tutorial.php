<?php
header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    exit(json_encode(['success' => false, 'message' => 'Method Not Allowed']));
}

$raw  = file_get_contents('php://input');
$data = json_decode($raw, true);

if (empty($_GET['activity_id']) || empty($_GET['module_id'])) {
    http_response_code(400);
    exit(json_encode(['success' => false, 'message' => 'Missing moduleId/activityId']));
}

$activityId = $_GET['activity_id'];
$moduleId = $_GET['module_id'];

$requiredFields = ['activity_name', 'activity_description', 'unlock_date', 'deadline_date', 'video_url'];
foreach ($requiredFields as $field) {
    if (!array_key_exists($field, $data)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => "Missing field: $field"]);
        exit;
    }
}

try{
    $conn->begin_transaction();

    $unlockDate   = date('Y-m-d H:i:s', strtotime($data['unlock_date']));
    $deadlineDate = null;
    if (!empty($data['deadline_date'])) {
        $deadlineDate = date('Y-m-d H:i:s', strtotime($data['deadline_date']));
    }

    $sql = "
        UPDATE Activity
        SET activity_name = ?, activity_description = ?, unlock_date = ?, deadline_date = ?
        WHERE activity_id = ? AND module_id = ?
    ";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param(
        'ssssss', 
        $data['activity_name'], 
        $data['activity_description'], 
        $unlockDate,
        $deadlineDate,
        $activityId,
        $moduleId
    );
    $stmt->execute();

    $sqlTutorial = "
        UPDATE Tutorial
        SET video_url = ?
        WHERE activity_id = ?
    ";
    $stmtTutorial = $conn->prepare($sqlTutorial);
    $stmtTutorial->bind_param('ss', $data['video_url'], $activityId);
    $stmtTutorial->execute();
    $conn->commit();

    echo json_encode([
        'success'=> true,
        'message'=> 'Tutorial successfully updated'
    ]);
}catch (Exception $e) {
    $conn->rollback();
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
}