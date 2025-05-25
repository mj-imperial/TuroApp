<?php
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
header('Content-Type: application/json; charset=utf-8');
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

if (empty($input['activity_id'])) {
    http_response_code(400);
    jsonResponse([ 'success' => false, 'message' => 'Invalid request: activity_id' ], 400);
}

try{
    $sql = "
    SELECT
        Q.question_id, 
        Q.question_text, 
        Q.question_image,
        QT.type_name,  
        Q.score,
        O.option_id,   
        O.option_text, 
        O.is_correct
    FROM Question Q
    JOIN Questiontype QT
        ON QT.type_id = Q.question_type_id
    LEFT JOIN Options O
        ON O.question_id = Q.question_id
    WHERE Q.activity_id = ?
    ORDER BY Q.question_id, O.option_id
    ";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param('s', $input['activity_id']);
    $stmt->execute();

    $result = $stmt->get_result();
    $rows = $result->fetch_all(MYSQLI_ASSOC);

    $stmt->close();

    $questions = [];
    foreach ($rows as $r) {
        $id = $r['question_id'];
        if (!isset($questions[$id])) {
            $questions[$id] = [
            'question_id'    => $r['question_id'],
            'question_text'  => $r['question_text'],
            'question_image' => $r['question_image'], 
            'type_name'      => $r['type_name'],
            'score'          => (int)$r['score'],
            'options'        => [],
            ];
        }

        if ($r['option_id'] !== null) {
            $questions[$id]['options'][] = [
            'option_id'   => $r['option_id'],
            'option_text' => $r['option_text'],
            'is_correct'  => (int)$r['is_correct'],
            ];
        }
    }

    echo json_encode([
        'success'   => true,
        'questions' => array_values($questions),
    ], JSON_UNESCAPED_UNICODE);

}catch (mysqli_sql_exception $e) {
    error_log("DB error in get_user.php: " . $e->getMessage());
    jsonResponse(['success'=>false,'error'=>'Internal Server Error'], 500);
}
