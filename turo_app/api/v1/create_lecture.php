<?php
header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/config.php';
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    exit(json_encode(['success' => false, 'message' => 'Method Not Allowed']));
}
$raw = file_get_contents('php://input');
$data = json_decode($raw, true);
if (empty($_GET['module_id'])) {
    http_response_code(400);
    exit(json_encode(['success' => false, 'message' => 'Missing moduleId']));
}
$moduleId = $_GET['module_id'];
$requiredFields = [
    'activity_name','activity_description','unlock_date','deadline_date','content_type_name'
];
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

try {
    $conn->begin_transaction();
    $stmtCheck = $conn->prepare("SELECT 1 FROM Activity WHERE module_id = ? AND activity_type = 'LECTURE' AND activity_name = ? LIMIT 1");
    $stmtCheck->bind_param("ss", $moduleId, $data['activity_name']);
    $stmtCheck->execute();
    $stmtCheck->store_result();
    if ($stmtCheck->num_rows > 0) {
        $stmtCheck->close();
        $conn->rollback();
        http_response_code(409);
        exit(json_encode(['success' => false, 'message' => 'A lecture with this name already exists in the same module.']));
    }
    $stmtCheck->close();
    $activity_id = uuid_v4();
    $unlockDate = date('Y-m-d H:i:s', strtotime($data['unlock_date']));
    $deadlineDate = null;
    if (!empty($data['deadline_date'])) {
        $deadlineDate = date('Y-m-d H:i:s', strtotime($data['deadline_date']));
    }
    $description = $data['activity_description'] ?? null;
    if ($deadlineDate === null) {
        $stmtActivity = $conn->prepare("
            INSERT INTO Activity (
                activity_id, module_id, activity_type,
                activity_name, activity_description, unlock_date
            ) VALUES (?, ?, 'LECTURE', ?, ?, ?)
        ");
        $stmtActivity->bind_param("sssss", $activity_id, $moduleId, $data['activity_name'], $description, $unlockDate);
    } else {
        $stmtActivity = $conn->prepare("
            INSERT INTO Activity (
                activity_id, module_id, activity_type,
                activity_name, activity_description,
                unlock_date, deadline_date
            ) VALUES (?, ?, 'LECTURE', ?, ?, ?, ?)
        ");
        $stmtActivity->bind_param("ssssss", $activity_id, $moduleId, $data['activity_name'], $description, $unlockDate, $deadlineDate);
    }
    if (!$stmtActivity->execute()) {
        throw new Exception("Activity insert failed: " . $stmtActivity->error);
    }
    $stmtActivity->close();
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
    if (!empty($data['video_url'])) {
        $stmtLecture = $conn->prepare("
            INSERT INTO Lecture (
                activity_id, content_type_id,
                video_url, text_body,
                file_url, file_mime_type, file_name
            ) VALUES (?, ?, ?, null, null, null, null)
        ");
        $stmtLecture->bind_param("sis", $activity_id, $contentTypeId, $data['video_url']);
    } else if (!empty($data['text_body'])) {
        $stmtLecture = $conn->prepare("
            INSERT INTO Lecture (
                activity_id, content_type_id,
                video_url, text_body,
                file_url, file_mime_type, file_name
            ) VALUES (?, ?, null, ?, null, null, null)
        ");
        $stmtLecture->bind_param("sis", $activity_id, $contentTypeId, $data['text_body']);
    } else {
        $stmtLecture = $conn->prepare("
            INSERT INTO Lecture (
                activity_id, content_type_id,
                video_url, text_body,
                file_url, file_mime_type, file_name
            ) VALUES (?, ?, null, null, ?, ?, ?)
        ");
        $stmtLecture->bind_param("sisss", $activity_id, $contentTypeId, $data['file_url'], $data['file_mime_type'], $data['file_name']);
    }
    if (!$stmtLecture->execute()) {
        throw new Exception("Lecture insert failed: " . $stmtLecture->error);
    }
    $stmtLecture->close();
    $eventDate = $unlockDate;
    $stmtInsertCalendar = $conn->prepare("
        INSERT INTO CalendarEvent (
            event_id, title, description,
            date, event_type_id,
            is_urgent, location
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
    ");
    $isUrgent = 0;
    $location = "Good Shepard High School";
    $eventTypeId = 1;
    $stmtInsertCalendar->bind_param("ssssiis", $activity_id, $data['activity_name'], $description, $eventDate, $eventTypeId, $isUrgent, $location);
    if (!$stmtInsertCalendar->execute()) {
        throw new Exception("CalendarEvent insert failed: " . $stmtInsertCalendar->error);
    }
    $stmtInsertCalendar->close();
    $conn->commit();
    echo json_encode(['success' => true, 'message' => "Lecture successfully created."]);
    exit;
} catch (Exception $e) {
    $conn->rollback();
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => $e->getMessage()]);
}
