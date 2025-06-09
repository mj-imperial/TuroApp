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

if (empty($input['student_id']) || empty($input['activity_id'])) {
    http_response_code(400);
    jsonResponse([ 'success' => false, 'message' => 'Invalid request: student_id or activity_id is empty' ], 400);
}

$studentId  = $input['student_id'];
$activityId = $input['activity_id'];

$stmt = $conn->prepare("
    SELECT 
      AR.result_id,
      AR.attempt_number,
      AR.score_percentage
    FROM Assessmentresult AR
    WHERE AR.student_id = ? AND AR.activity_id = ?
    ORDER BY AR.attempt_number
");

$stmt->bind_param('ss', $studentId, $activityId);
$stmt->execute();
$scores = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
$stmt->close();

header('Content-Type: application/json');
echo json_encode([
    'success' => true,
    'scores' => $scores
]);
exit;