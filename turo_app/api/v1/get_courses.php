<?php
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

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    jsonResponse(['success'=>false,'error'=>'Method Not Allowed'], 405);
}

if (! empty($_POST)) {
    $input = $_POST;
} else {
    $raw   = file_get_contents('php://input');
    $input = json_decode($raw, true) ?: [];
}

if (empty($input['action'])) {
    http_response_code(400);
    jsonResponse([ 'success' => false, 'message' => 'Invalid request: action missing' ], 400);
}

switch ($input['action']) {
    case 'studentCourses':
        if(empty($input['user_id'])){
            http_response_code(400);
            jsonResponse(['success' => false, 'message' => 'user_id is required'], 400);
        }
        getStudentCourses($conn, $input['user_id']);
        break;
    case 'teacherCourses':
        if(empty($input['user_id'])){
            http_response_code(400);
            jsonResponse(['success' => false, 'message' => 'user_id is required'], 400);
        }
        getTeacherCourses($conn, $input['user_id']);
        break;
}
