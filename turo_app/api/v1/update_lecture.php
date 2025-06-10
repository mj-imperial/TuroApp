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

$requiredFields = [
    'activity_name', 'activity_description', 'unlock_date', 'deadline_date', 'content_type_name'];
foreach ($requiredFields as $field) {
    if (!array_key_exists($field, $data)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => "Missing field: $field"]);
        exit;
    }
}
switch ($data['content_type_name']) {
  case 'PDF/DOCS':
    $need = ['file_url','file_mime_type','file_name'];
    break;
  case 'VIDEO':
    $need = ['video_url'];
    break;
  case 'TEXT':
    $need = ['text_body'];
    break;
  default:
    http_response_code(400);
    exit(json_encode(['success'=>false,'message'=>'Invalid content_type_name']));
}
foreach ($need as $f) {
  if (empty($data[$f])) {
    http_response_code(400);
    exit(json_encode(['success'=>false,'message'=>"Missing field: $f"]));
  }
}

try{
    $conn->begin_transaction();

    $activityName = $data['activity_name'];
    $activityDescription = $data['activity_description'];
    $contentType = $data['content_type_name'];
    $videoUrl = $data['video_url'] ?? null;
    $fileUrl = $data['file_url'] ?? null;
    $fileMimeType = $data['file_mime_type'] ?? null;
    $fileName = $data['file_name'] ?? null;
    $textBody = $data['text_body'] ?? null;

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
        $activityName, 
        $activityDescription, 
        $unlockDate,
        $deadlineDate,
        $activityId,
        $moduleId
    );
    $stmt->execute();

    $oldFileUrl = null;
    $getFileSql = "SELECT file_url FROM Lecture WHERE activity_id = ?";
    $getStmt = $conn->prepare($getFileSql);
    $getStmt->bind_param("s", $activityId);
    $getStmt->execute();
    $getStmt->bind_result($oldFileUrl);
    $getStmt->fetch();
    $getStmt->close();

    if (!empty($data['file_url']) && !empty($oldFileUrl) && $oldFileUrl !== $data['file_url']) {
        $parsed = parse_url($oldFileUrl);
        $oldPath = $_SERVER['DOCUMENT_ROOT'] . $parsed['path'];

        if (file_exists($oldPath)) {
            unlink($oldPath);
        }
    }

    $stmtType = $conn->prepare("SELECT content_type_id FROM Contenttype WHERE content_type_name = ?");
    $stmtType->bind_param("s", $data['content_type_name']);
    $stmtType->execute();
    $resultType = $stmtType->get_result();
    $row = $resultType->fetch_assoc();
    $stmtType->close();
    if (!$row) {
        throw new Exception("Invalid content_type_id: " . $data['content_type_name']);
    }
    $contentTypeId = (int)$row['content_type_id'];

    $stmtLecture = $conn->prepare("
        UPDATE Lecture 
        SET content_type_id=?, video_url=?, file_url=?, file_mime_type=?, file_name=?, text_body=? 
        WHERE activity_id=?    
    ");
    $stmtLecture->bind_param(
        "ssssssi", 
        $contentTypeId, 
        $videoUrl, 
        $fileUrl, 
        $fileMimeType, 
        $fileName, 
        $textBody, 
        $activityId
    );
    $stmtLecture->execute();

    $conn->commit();

    echo json_encode(['success' => true, 'message' => 'Lecture updated successfully']);
    exit;
}catch (mysqli_sql_exception $e) {
    error_log("DB error in get_user.php: " . $e->getMessage());
    jsonResponse(['success'=>false,'error'=>'Internal Server Error'], 500);
}