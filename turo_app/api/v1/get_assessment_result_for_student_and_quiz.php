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
      AR.module_id,
      AR.score_percentage,
      AR.date_taken,
      AR.attempt_number,
      ST.tier_name,
      AR.earned_points,
      AR.is_kept
    FROM Assessmentresult AR
    LEFT JOIN Screeningtier ST 
      ON ST.tier_id = AR.tier_level_id
    WHERE AR.student_id = ? 
      AND AR.activity_id = ?
    ORDER BY AR.attempt_number
");
$stmt->bind_param('ss', $studentId, $activityId);
$stmt->execute();
$res = $stmt->get_result();

if ($res->num_rows === 0) {
    echo json_encode([
      'success' => true,
      'results' => [] 
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

$results = [];
while ($row = $res->fetch_assoc()) {
    $row['answers'] = [];
    $stmt2 = $conn->prepare("
        SELECT question_id, option_id, is_correct
        FROM Assessmentresult_answers
        WHERE result_id = ?
    ");
    $stmt2->bind_param('s', $row['result_id']);
    $stmt2->execute();
    $res2 = $stmt2->get_result();
    while ($ans = $res2->fetch_assoc()) {
        $row['answers'][] = [
            'question_id' => $ans['question_id'],
            'option_id'   => $ans['option_id'],
            'is_correct'  => (bool)$ans['is_correct']
        ];
    }
    $stmt2->close();

    $results[] = $row;
}
$stmt->close();

echo json_encode([
    'success' => true,
    'results' => $results
], JSON_UNESCAPED_UNICODE);
exit;