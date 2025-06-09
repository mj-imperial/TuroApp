<?php
header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    exit(json_encode(['success' => false, 'message' => 'Method Not Allowed']));
}

$raw  = file_get_contents('php://input');
$data = json_decode($raw, true);

if (
    empty($data['student_id']) ||
    empty($data['activity_id']) ||
    ! isset($data['score_percentage']) ||
    ! isset($data['earned_points']) ||
    empty($data['answers']) ||
    ! is_array($data['answers'])
) {
    http_response_code(400);
    exit(json_encode(['success' => false, 'message' => 'Missing required fields']));
}

$studentId       = $conn->real_escape_string($data['student_id']);
$activityId      = $conn->real_escape_string($data['activity_id']);
$scorePercentage = (float) $data['score_percentage'];
$earnedPoints    = (int)   $data['earned_points'];
$answers         = $data['answers'];

$tierLevelId = null;
if (! empty($data['tier_level_id'])) {
    $tierLevelId = (int) $data['tier_level_id'];
}

$resultId = uuid_v4();

try {
    $conn->begin_transaction();

    //
    // 1) Fetch module_id for this activity_id
    //
    $stmt = $conn->prepare('SELECT module_id FROM Activity WHERE activity_id = ?');
    $stmt->bind_param('s', $activityId);
    $stmt->execute();
    $stmt->bind_result($moduleId);
    if (! $stmt->fetch()) {
        throw new Exception('Invalid activity_id');
    }
    $stmt->close();

    //
    // 2) Count existing attempts → attempt_number
    //
    $stmt = $conn->prepare(
        'SELECT COUNT(*) 
           FROM AssessmentResult
          WHERE student_id = ? AND activity_id = ?'
    );
    $stmt->bind_param('ss', $studentId, $activityId);
    $stmt->execute();
    $stmt->bind_result($existing);
    $stmt->fetch();
    $stmt->close();

    $attemptNumber = $existing + 1;

    //
    // 3) Look up the existing “kept” (highest) row, if any
    //
    $stmt = $conn->prepare(
        'SELECT result_id, score_percentage
           FROM AssessmentResult
          WHERE student_id = ? 
            AND activity_id = ? 
            AND is_kept = 1
          ORDER BY score_percentage DESC
          LIMIT 1'
    );
    $stmt->bind_param('ss', $studentId, $activityId);
    $stmt->execute();
    $stmt->bind_result($oldKeptResultId, $oldKeptScore);
    $hasOldKept = $stmt->fetch(); // true if we found a kept row
    $stmt->close();

    //
    // 4) Decide if the new submission is the new best for this activity
    //
    $newIsKept = 0;
    if (! $hasOldKept || $scorePercentage > $oldKeptScore) {
        $newIsKept = 1;
        if ($hasOldKept) {
            // Demote the old kept row
            $upd = $conn->prepare(
                'UPDATE AssessmentResult
                    SET is_kept = 0
                  WHERE result_id = ?'
            );
            $upd->bind_param('s', $oldKeptResultId);
            $upd->execute();
            $upd->close();
        }
    }

    //
    // 5) Insert the new AssessmentResult, including is_kept
    //
    $stmt = $conn->prepare(
        'INSERT INTO AssessmentResult
           (result_id, student_id, module_id, activity_id,
            score_percentage, date_taken, attempt_number,
            tier_level_id, earned_points, is_kept)
         VALUES (?, ?, ?, ?, ?, NOW(), ?, ?, ?, ?)'
    );
    $stmt->bind_param(
        'ssssdiiii',
        $resultId,
        $studentId,
        $moduleId,
        $activityId,
        $scorePercentage,
        $attemptNumber,
        $tierLevelId,
        $earnedPoints,
        $newIsKept
    );
    $stmt->execute();
    $stmt->close();

    //
    // 6) Insert each answer into AssessmentResult_Answers
    //
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

    //
    // 7) Compute new average across all kept scores for this student
    //
    $stmt = $conn->prepare(
        'SELECT AVG(score_percentage)
           FROM AssessmentResult
          WHERE student_id = ? 
            AND is_kept = 1'
    );
    $stmt->bind_param('s', $studentId);
    $stmt->execute();
    $stmt->bind_result($newAverageAcrossActivities);
    $stmt->fetch();
    $stmt->close();

    //
    // 8) Fetch course_id via the module table
    //    (Assumes you have a Module table with fields module_id, course_id)
    //
    $stmt = $conn->prepare('SELECT course_id FROM Module WHERE module_id = ?');
    $stmt->bind_param('s', $moduleId);
    $stmt->execute();
    $stmt->bind_result($courseId);
    if (! $stmt->fetch()) {
        throw new Exception('Invalid module_id');
    }
    $stmt->close();

    //
    // 9) Upsert into StudentProgress
    //    – Check if a row already exists for (student_id, course_id)
    //
    $stmt = $conn->prepare(
        'SELECT total_points 
           FROM StudentProgress 
          WHERE student_id = ? 
            AND course_id = ?'
    );
    $stmt->bind_param('ss', $studentId, $courseId);
    $stmt->execute();
    $stmt->bind_result($existingPoints);
    $rowExists = $stmt->fetch();
    $stmt->close();

    if ($rowExists) {
        // Update the existing row
        $newTotalPoints = $existingPoints + $earnedPoints;
        $upd = $conn->prepare(
            'UPDATE StudentProgress
                SET total_points   = ?,
                    average_score = ?
              WHERE student_id = ?
                AND course_id  = ?'
        );
        $upd->bind_param(
            'idss',
            $newTotalPoints,
            $newAverageAcrossActivities,
            $studentId,
            $courseId
        );
        $upd->execute();
        $upd->close();
    } else {
        // Insert a fresh row
        $ins = $conn->prepare(
            'INSERT INTO StudentProgress
               (student_id, course_id, total_points, average_score)
             VALUES (?, ?, ?, ?)'
        );
        $ins->bind_param(
            'ssid',
            $studentId,
            $courseId,
            $earnedPoints,
            $newAverageAcrossActivities
        );
        $ins->execute();
        $ins->close();
    }

    $stmt = $conn->prepare(
    'UPDATE student_badges AS sb
     JOIN badges AS b ON sb.badge_id = b.badge_id
     SET sb.is_unlocked = 1
     WHERE sb.student_id = ?
       AND b.points_required <= ?
       AND sb.is_unlocked = 0'
    );
    $stmt->bind_param('si', $studentId, $newTotalPoints);
    $stmt->execute();
    $stmt->close();

    $conn->commit();

    echo json_encode([
        'success'                => true,
        'message'                => 'Assessment saved',
        'result_id'              => $resultId,
        'student_id'             => $studentId,
        'activity_id'            => $activityId,
        'attempt_number'         => $attemptNumber,
        'score_percentage'       => $scorePercentage,
        'earned_points'          => $earnedPoints,
        'tier_level_id'          => $tierLevelId,
        'is_kept'                => $newIsKept,
        'average_across_quizzes' => $newAverageAcrossActivities,
        'course_id'              => $courseId,
        'answers'                => $answers
    ]);

} catch (Exception $e) {
    $conn->rollback();
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
}