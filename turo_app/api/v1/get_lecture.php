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

if (empty($input['activity_id'])) {
    http_response_code(400);
    jsonResponse([ 'success' => false, 'message' => 'Invalid request: activity_id' ], 400);
}

$activityId = $input['activity_id'];

try{
    $sql = "
        SELECT 
            a.activity_name,
            a.activity_description,
            a.unlock_date,
            a.deadline_date,
            ct.content_type_name,
            l.video_url,
            l.file_url,
            l.file_mime_type,
            l.file_name,
            l.text_body
        FROM `Activity` AS a
        INNER JOIN `Lecture` AS l
            ON a.activity_id = l.activity_id
        INNER JOIN `Contenttype` AS ct
            ON l.content_type_id = ct.content_type_id
        WHERE a.activity_id = ?
    ";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param('s', $activityId);
    $stmt->execute();

    $result = $stmt->get_result();
    $lecture = $result->fetch_assoc();
    $stmt->close();

    if (! $lecture) {
        http_response_code(404);
        echo json_encode([
            'success' => false,
            'message' => 'Lecture not found'
        ]);
        exit;
    }
    header('Content-Type: application/json');
    echo json_encode($lecture);
    exit;
}catch (mysqli_sql_exception $e) {
    error_log("DB error in get_user.php: " . $e->getMessage());
    jsonResponse(['success'=>false,'error'=>'Internal Server Error'], 500);
}