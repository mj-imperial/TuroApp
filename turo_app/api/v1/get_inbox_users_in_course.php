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

if (empty($input['action'])) {
    http_response_code(400);
    jsonResponse([ 'success' => false, 'message' => 'Invalid request: action missing' ], 400);
}

switch ($input['action']) {
    case 'student':
        if (empty($input['user_id'])) {
            http_response_code(400);
            jsonResponse(['success' => false, 'message' => 'user_id is required'], 400);
        }
        $responseData = getStudentEmails($conn, $input['user_id']);
        jsonResponse(['success' => true, 'courses' => $responseData]);
        break;

    case 'teacher':
        if (empty($input['user_id'])) {
            http_response_code(400);
            jsonResponse(['success' => false, 'message' => 'user_id is required'], 400);
        }
        $responseData = getTeacherEmails($conn, $input['user_id']);
        jsonResponse(['success' => true, 'courses' => $responseData]);
        break;

    default:
        http_response_code(400);
        jsonResponse([ 'success' => false, 'message' => 'Unknown action' ], 400);
        break;
}

function getStudentEmails(mysqli $conn, string $userId) {
    $stmt = $conn->prepare("
        SELECT 
            c.course_id,
            c.course_name,
            
            u_teacher.user_id AS teacher_id,
            u_teacher.email AS teacher_email,
            CONCAT(u_teacher.first_name, ' ', u_teacher.last_name) AS teacher_name,
            u_teacher.profile_pic AS teacher_pic,

            u_student.user_id AS student_id,
            CONCAT(u_student.first_name, ' ', u_student.last_name) AS student_name,
            u_student.email AS student_email,
            u_student.profile_pic AS student_pic

        FROM Enrollment e
        JOIN Course c ON e.course_id = c.course_id
        JOIN User u_teacher ON c.teacher_id = u_teacher.user_id
        JOIN Enrollment e2 ON e2.course_id = c.course_id
        JOIN User u_student ON e2.student_id = u_student.user_id
        WHERE e.student_id = ?
    ");
    $stmt->bind_param("s", $userId);
    $stmt->execute();
    $result = $stmt->get_result();

    $groupedCourses = [];

    while ($row = $result->fetch_assoc()) {
        $courseId = $row['course_id'];

        if (!isset($groupedCourses[$courseId])) {
            $groupedCourses[$courseId] = [
                'course_name'    => $row['course_name'],
                'teacher_id'     => $row['teacher_id'],
                'teacher_name'   => $row['teacher_name'],
                'teacher_email'  => $row['teacher_email'],
                'teacher_pic'    => $row['teacher_pic'],
                'students'       => []
            ];
        }

        $groupedCourses[$courseId]['students'][] = [
            'user_id'        => $row['student_id'],
            'student_name'   => $row['student_name'],
            'student_email'  => $row['student_email'],
            'student_pic'    => $row['student_pic']
        ];
    }

    return array_values($groupedCourses);
}

function getTeacherEmails(mysqli $conn, string $userId) {
    $stmt = $conn->prepare("
        SELECT 
            c.course_id,
            c.course_name,

            u_teacher.user_id AS teacher_id,
            u_teacher.email AS teacher_email,
            CONCAT(u_teacher.first_name, ' ', u_teacher.last_name) AS teacher_name,
            u_teacher.profile_pic AS teacher_pic,

            u_student.user_id AS student_id,
            CONCAT(u_student.first_name, ' ', u_student.last_name) AS student_name,
            u_student.email AS student_email,
            u_student.profile_pic AS student_pic

        FROM Course c
        JOIN User u_teacher ON c.teacher_id = u_teacher.user_id
        JOIN Enrollment e ON e.course_id = c.course_id
        JOIN User u_student ON e.student_id = u_student.user_id
        WHERE c.teacher_id = ?
    ");
    $stmt->bind_param("s", $userId);
    $stmt->execute();
    $result = $stmt->get_result();

    $groupedCourses = [];

    while ($row = $result->fetch_assoc()) {
        $courseId = $row['course_id'];

        if (!isset($groupedCourses[$courseId])) {
            $groupedCourses[$courseId] = [
                'course_name'    => $row['course_name'],
                'teacher_id'     => $row['teacher_id'],
                'teacher_name'   => $row['teacher_name'],
                'teacher_email'  => $row['teacher_email'],
                'teacher_pic'    => $row['teacher_pic'],
                'students'       => []
            ];
        }

        $groupedCourses[$courseId]['students'][] = [
            'user_id'        => $row['student_id'],
            'student_name'   => $row['student_name'],
            'student_email'  => $row['student_email'],
            'student_pic'    => $row['student_pic']
        ];
    }

    return array_values($groupedCourses);
}

