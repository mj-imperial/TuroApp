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

if (empty($input['user_id'])) {
    http_response_code(400);
    jsonResponse([ 'success' => false, 'message' => 'Invalid request: user_id is required' ], 400);
}

try{
    $sql = "
        SELECT
            C.course_code,
            CE.event_id,
            CE.title,
            CE.description,
            CE.date,
            ET.event_type_name,
            CE.is_urgent,
            CE.location
        FROM `Calendarevent` AS CE
        INNER JOIN `Eventtype` AS ET 
            ON CE.event_type_id = ET.event_type_id
        INNER JOIN `Activity` AS A  
            ON CE.event_id = A.activity_id 
        INNER JOIN `Module` AS M  
            ON A.module_id = M.module_id
        INNER JOIN `Course` AS C 
            ON M.course_id = C.course_id
        INNER JOIN `Enrollment` AS E  
            ON C.course_id = E.course_id
        WHERE E.student_id = ?
        ORDER BY CE.date;
    ";
    $stmt = $conn->prepare($sql);
    if (! $stmt) {
        http_response_code(500);
        jsonResponse(['success'=>false,'message'=>'Database prepare failed'],500);
        return;
    }
    $stmt->bind_param('s', $input['user_id']);
    if (! $stmt->execute()) {
        http_response_code(500);
        jsonResponse(['success'=>false,'message'=>'Database execute failed'],500);
        return;
    }

    $events = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    $stmt->close();

    header('Content-Type: application/json');
    echo json_encode([
      'success' => true,
      'events' => $events
    ]);
    exit;
}catch (mysqli_sql_exception $e) {
    error_log("DB error in get_user.php: " . $e->getMessage());
    jsonResponse(['success'=>false,'error'=>'Internal Server Error'], 500);
}
