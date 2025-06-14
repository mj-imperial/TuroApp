<?php
header('Content-Type: application/json');
require_once __DIR__ . '/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(['success' => false, 'message' => 'Method Not Allowed']);
    exit;
}

$inboxId = $_GET['inbox_id'] ?? null;
$userId = $_GET['user_id'] ?? null;

if (!$inboxId || !$userId) {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'Missing inbox_id or user_id']);
    exit;
}

try {
    $stmt = $conn->prepare("
        SELECT 
            m.message_id, 
            m.sender_id, 
            CONCAT(us.first_name, ' ', us.last_name) AS sender_name, 
            us.profile_pic AS sender_pic,
            m.subject, 
            m.body, 
            m.timestamp
        FROM Message m
        JOIN User us ON m.sender_id = us.user_id
        WHERE m.inbox_id = ?
        ORDER BY m.timestamp ASC
    ");
    $stmt->bind_param("s", $inboxId);
    $stmt->execute();
    $result = $stmt->get_result();

    $messages = [];

    while ($row = $result->fetch_assoc()) {
        $senderId = $row['sender_id'];

        if ($senderId === $userId) {
            $stmtR = $conn->prepare("
                SELECT u.user_id, CONCAT(u.first_name, ' ', u.last_name) AS name, u.profile_pic
                FROM InboxParticipant p
                JOIN User u ON p.participant_id = u.user_id
                WHERE p.inbox_id = ? AND p.participant_id != ?
                LIMIT 1
            ");
            $stmtR->bind_param("ss", $inboxId, $userId);
        } else {
            $stmtR = $conn->prepare("
                SELECT u.user_id, CONCAT(u.first_name, ' ', u.last_name) AS name, u.profile_pic
                FROM User u
                WHERE u.user_id = ?
                LIMIT 1
            ");
            $stmtR->bind_param("s", $userId);
        }

        $stmtR->execute();
        $recipientData = $stmtR->get_result()->fetch_assoc();
        $stmtR->close();

        $messages[] = [
            'message_id' => $row['message_id'],
            'sender_id' => $senderId,
            'sender_name' => $row['sender_name'],
            'sender_pic' => $row['sender_pic'] ?? null,
            'recipient_id' => $recipientData['user_id'],
            'recipient_name' => $recipientData['name'],
            'recipient_pic' => $recipientData['profile_pic'] ?? null,
            'subject' => $row['subject'],
            'body' => $row['body'],
            'timestamp' => (int) $row['timestamp']
        ];
    }

    $stmt->close();

    echo json_encode([
        'success' => true,
        'messages' => $messages
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Server error',
        'error' => $e->getMessage()
    ]);
}
