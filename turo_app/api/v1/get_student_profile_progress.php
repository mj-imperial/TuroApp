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

if (! isset($_GET['student_id']) || empty($_GET['student_id'])) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => 'Missing student_id'
    ]);
    exit;
}
$studentId = $_GET['student_id'];

try{
    $sql = "
        SELECT 
            c.course_name,
            sp.total_points, 
            sp.average_score
        FROM `Studentprogress` AS sp
        INNER JOIN `Course` AS c
            ON sp.course_id = c.course_id
        WHERE sp.student_id = ?
    ";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param('s', $studentId);
    $stmt->execute();

    $result = $stmt->get_result();
    $progress = $result->fetch_assoc();
    $stmt->close();

    if (! $progress) {
        http_response_code(404);
        echo json_encode([
            'success' => false,
            'message' => 'User Progress not found'
        ]);
        exit;
    }
    header('Content-Type: application/json');
    echo json_encode($progress);
    exit;
}catch (mysqli_sql_exception $e) {
    error_log("DB error in get_user.php: " . $e->getMessage());
    jsonResponse(['success'=>false,'error'=>'Internal Server Error'], 500);
}