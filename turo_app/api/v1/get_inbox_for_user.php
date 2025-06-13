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

$input = $_SERVER['REQUEST_METHOD'] === 'GET' ? $_GET : json_decode(file_get_contents('php://input'), true);
$userId = $input['user_id'] ?? null;

if (!$userId) {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'Missing user_id']);
    exit;
}

try {
    $stmt = $conn->prepare("
        SELECT i.inbox_id,
               i.timestamp AS inbox_timestamp,
               (
                   SELECT m.subject
                   FROM Message m
                   WHERE m.inbox_id = i.inbox_id
                   ORDER BY m.timestamp DESC
                   LIMIT 1
               ) AS last_message_subject,
                (
                SELECT m.message_id
                FROM Message m
                WHERE m.inbox_id = i.inbox_id
                ORDER BY m.timestamp DESC
                LIMIT 1
                ) AS latest_message_id,
               (
                   SELECT m.body
                   FROM Message m
                   WHERE m.inbox_id = i.inbox_id
                   ORDER BY m.timestamp DESC
                   LIMIT 1
               ) AS last_message_preview,
               (
                   SELECT COUNT(*)
                   FROM MessageUserState mus
                   JOIN Message m ON mus.message_id = m.message_id
                   WHERE m.inbox_id = i.inbox_id
                     AND mus.user_id = ?
                     AND mus.is_read = FALSE
               ) AS unread_count
        FROM InboxParticipant ip
        JOIN Inbox i ON ip.inbox_id = i.inbox_id
        WHERE ip.participant_id = ? AND ip.is_deleted = 0
        ORDER BY i.timestamp DESC
    ");
    $stmt->bind_param('ss', $userId, $userId);
    $stmt->execute();
    $result = $stmt->get_result();

    $inboxes = [];
    while ($row = $result->fetch_assoc()) {
        $inboxId = $row['inbox_id'];

        $participantsStmt = $conn->prepare("
            SELECT u.user_id, CONCAT(u.first_name, ' ', u.last_name) AS name, u.profile_pic
            FROM InboxParticipant ip
            JOIN User u ON u.user_id = ip.participant_id
            WHERE ip.inbox_id = ? AND ip.participant_id != ?
        ");
        $participantsStmt->bind_param("ss", $inboxId, $userId);
        $participantsStmt->execute();
        $participantsResult = $participantsStmt->get_result();

        $participants = [];
        while ($user = $participantsResult->fetch_assoc()) {
            $participants[] = $user;
        }
        $participantsStmt->close();

        $row['participants'] = $participants;
        $inboxes[] = $row;
    }

    echo json_encode([
        'success' => true,
        'inboxes' => $inboxes
    ]);
    exit;
} catch (mysqli_sql_exception $e) {
    error_log("DB error in get_inbox_for_user.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Internal server error']);
}
