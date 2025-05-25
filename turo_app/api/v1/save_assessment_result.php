<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);
header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/config.php';
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
  http_response_code(405);
  exit(json_encode(['success'=>false,'message'=>'Method Not Allowed']));
}

$raw  = file_get_contents('php://input');
$data = json_decode($raw, true);
if (
  empty($data['student_id'])    ||
  empty($data['activity_id'])   ||
  ! isset($data['score_percentage']) ||
  ! isset($data['earned_points'])    ||
  empty($data['answers'])       ||
  ! is_array($data['answers'])
) {
  http_response_code(400);
  exit(json_encode(['success'=>false,'message'=>'Missing required fields']));
}

$studentId       = $conn->real_escape_string($data['student_id']);
$activityId      = $conn->real_escape_string($data['activity_id']);
$scorePercentage = (float) $data['score_percentage'];
$earnedPoints    = (int)   $data['earned_points'];
$answers         = $data['answers'];

$tierLevelId = null;
if (! empty($data['tier_level_id'])) {
  $tierLevelId = (int)$data['tier_level_id'];
}

function uuid_v4() {
  $data = random_bytes(16);
  $data[6] = chr((ord($data[6]) & 0x0f) | 0x40);
  $data[8] = chr((ord($data[8]) & 0x3f) | 0x80);
  return vsprintf('%s%s-%s-%s-%s-%s%s%s', str_split(bin2hex($data), 4));
}

$resultId = uuid_v4();

try {
  $conn->begin_transaction();

  $stmt = $conn->prepare('SELECT module_id FROM Activity WHERE activity_id = ?');
  $stmt->bind_param('s', $activityId);
  $stmt->execute();
  $stmt->bind_result($moduleId);
  if (! $stmt->fetch()) {
    throw new Exception('Invalid activity_id');
  }
  $stmt->close();

  $stmt = $conn->prepare(
    'SELECT COUNT(*) FROM AssessmentResult
     WHERE student_id = ? AND activity_id = ?'
  );
  $stmt->bind_param('ss', $studentId, $activityId);
  $stmt->execute();
  $stmt->bind_result($existing);
  $stmt->fetch();
  $stmt->close();

  $attemptNumber = $existing + 1;

  $stmt = $conn->prepare(
    'INSERT INTO AssessmentResult
      (result_id, student_id, module_id, activity_id,
        score_percentage, date_taken, attempt_number,
        tier_level_id, earned_points)
    VALUES (?, ?, ?, ?, ?, NOW(), ?, ?, ?)'
  );

  $stmt->bind_param(
    'ssssdiii',
    $resultId,
    $studentId,
    $moduleId,
    $activityId,
    $scorePercentage,
    $attemptNumber,
    $tierLevelId,
    $earnedPoints
  );
  $stmt->execute();
  $stmt->close();

  $stmt = $conn->prepare(
    'INSERT INTO AssessmentResult_Answers
       (result_id, question_id, option_id, is_correct)
     VALUES (?, ?, ?, ?)'
  );
  foreach ($answers as $ans) {
    if (
      empty($ans['question_id']) ||
      ! isset($ans['option_id']) ||
      ! isset($ans['is_correct'])
    ) {
      throw new Exception('Invalid answer payload');
    }
    $corr = $ans['is_correct'] ? 1 : 0;
    $stmt->bind_param(
      'sssi',
      $resultId,
      $ans['question_id'],
      $ans['option_id'],
      $corr
    );
    $stmt->execute();
  }
  $stmt->close();

  $stmt = $conn->prepare(
    'UPDATE Student
        SET totalPoints = totalPoints + ?
      WHERE user_id = ?'
  );
  $stmt->bind_param('is', $earnedPoints, $studentId);
  $stmt->execute();
  $stmt->close();

  $conn->commit();

  echo json_encode([
    'success'          => true,
    'message'          => 'Assessment saved',
    'result_id'        => $resultId,
    'student_id'       => $studentId,
    'activity_id'      => $activityId,
    'attempt_number'   => $attemptNumber,
    'score_percentage' => $scorePercentage,
    'earned_points'    => $earnedPoints,
    'tier_level_id'    => $tierLevelId,
    'answers'          => $answers
  ]);

} catch (Exception $e) {
  // Rollback and return error
  $conn->rollback();
  http_response_code(500);
  echo json_encode([
    'success' => false,
    'message' => $e->getMessage()
  ]);
}
