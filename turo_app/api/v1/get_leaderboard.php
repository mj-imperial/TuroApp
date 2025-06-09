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

if (empty($input['student_id'])) {
    http_response_code(400);
    echo json_encode([
      'success' => false,
      'message' => 'Invalid request: student_id is required'
    ]);
    exit;
}

$studentId = $input['student_id'];

try {
    $sql = "
      SELECT SP.course_id
      FROM `Studentprogress` AS SP
      WHERE SP.student_id = ?
      LIMIT 1
    ";
    $stmt = $conn->prepare($sql);
    if (! $stmt) {
        throw new Exception("Prepare failed (step A): " . $conn->error);
    }
    $stmt->bind_param('s', $studentId);
    $stmt->execute();
    $result = $stmt->get_result(); 
    $row    = $result->fetch_assoc(); 
    $stmt->close();

    if (! $row) {
        http_response_code(404);
        echo json_encode([
          'success' => false,
          'message' => 'No course found for this student_id'
        ]);
        exit;
    }

    $courseId = $row['course_id']; 

    $sql = "
      SELECT
        U.first_name,
        U.last_name,
        C.course_name,
        U.profile_pic,
        SP.total_points,
        SP.average_score
      FROM `Studentprogress` AS SP
      INNER JOIN `User` AS U
        ON U.user_id = SP.student_id
      INNER JOIN `Course` AS C
        ON C.course_id = SP.course_id
      WHERE SP.course_id = ?
    ";
    $stmt = $conn->prepare($sql);
    if (! $stmt) {
        throw new Exception("Prepare failed (step B): " . $conn->error);
    }
    $stmt->bind_param('s', $courseId);
    $stmt->execute();
    $progresses = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    $stmt->close();

    echo json_encode([
      'success'    => true,
      'progresses' => $progresses
    ]);
    exit;

} catch (Exception $e) {
    error_log("DB error in get_user.php: " . $e->getMessage());

    http_response_code(500);
    echo json_encode([
      'success' => false,
      'message' => 'Internal Server Error'
    ]);
    exit;
}
