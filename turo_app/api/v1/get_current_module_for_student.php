<?php
header('Content-Type: application/json');

$configFile = __DIR__ . '/config.php';
if (!file_exists($configFile)) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => "Config file not found"
    ]);
    exit;
}
require_once $configFile;

$input = $_SERVER['REQUEST_METHOD'] === 'GET' ? $_GET : (
    !empty($_POST) ? $_POST : json_decode(file_get_contents('php://input'), true)
);

if (empty($input['student_id']) || empty($input['course_id'])) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => 'Missing student_id or course_id'
    ]);
    exit;
}

$studentId = $input['student_id'];
$courseId = $input['course_id'];

try {
    $sql = "
        SELECT 
            m.module_id,
            m.module_name,
            m.module_picture,
            m.module_description,
            COALESCE(mp.progress, 0) AS progress
        FROM Module AS m
        LEFT JOIN moduleprogress AS mp 
            ON mp.module_id = m.module_id AND mp.student_id = ?
        WHERE m.course_id = ?
        ORDER BY m.module_id ASC
        LIMIT 1
    ";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param('ss', $studentId, $courseId);
    $stmt->execute();

    $result = $stmt->get_result();
    $module = $result->fetch_assoc();
    $stmt->close();

    if (!$module) {
        http_response_code(404);
        echo json_encode([
            'success' => false,
            'message' => 'Module not found'
        ]);
        exit;
    }

    echo json_encode($module);
    exit;
} catch (mysqli_sql_exception $e) {
    error_log("DB error in get_current_module_for_student.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(['success' => false, 'error' => 'Internal Server Error']);
    exit;
}