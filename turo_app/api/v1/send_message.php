<?php
header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    exit(json_encode(['success' => false, 'message' => 'Method Not Allowed']));
}

$raw  = file_get_contents('php://input');
$data = json_decode($raw, true);

if (empty($_GET['user_id'])) {
    http_response_code(400);
    exit(json_encode(['success' => false, 'message' => 'Missing user_id']));
}
$userId = $_GET['user_id'];

$requiredFields = ['sender_id', 'recipient_ids', 'subject', 'body'];
foreach ($requiredFields as $field) {
    if (empty($data[$field])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => "Missing: $field"]);
        exit;
    }
}

$senderId = $data['sender_id'];
$recipients = $data['recipient_ids'];
$subject = $data['subject'];
$body = $data['body'];
$timestamp = time();

$allParticipants = array_unique(array_merge([$senderId], $recipients));
sort($allParticipants);

try {
    $conn->begin_transaction();

    // Always create a new inbox (removed checking for existing one)
    $inboxId = uuid_v4();
    $stmt = $conn->prepare("INSERT INTO Inbox (inbox_id, unread_count, timestamp) VALUES (?, 0, ?)");
    $stmt->bind_param("si", $inboxId, $timestamp);
    $stmt->execute();

    $stmt = $conn->prepare("INSERT INTO InboxParticipant (inbox_id, participant_id) VALUES (?, ?)");
    foreach ($allParticipants as $participantId) {
        $stmt->bind_param("ss", $inboxId, $participantId);
        $stmt->execute();
    }
    $stmt->close();

    // Insert message
    $messageId = uuid_v4();
    $stmt = $conn->prepare("
        INSERT INTO Message (message_id, inbox_id, sender_id, subject, body, timestamp)
        VALUES (?, ?, ?, ?, ?, ?)
    ");
    $stmt->bind_param("sssssi", $messageId, $inboxId, $senderId, $subject, $body, $timestamp);
    $stmt->execute();
    $stmt->close();

    // Update inbox timestamp
    $stmt = $conn->prepare("UPDATE Inbox SET timestamp = ? WHERE inbox_id = ?");
    $stmt->bind_param("is", $timestamp, $inboxId);
    $stmt->execute();
    $stmt->close();

    // Set message state for each participant
    $stmt = $conn->prepare("
        INSERT INTO MessageUserState (message_id, user_id, is_read, is_deleted)
        VALUES (?, ?, ?, 0)
    ");
    foreach ($allParticipants as $participantId) {
        $isRead = ($participantId === $senderId) ? 1 : 0;
        $stmt->bind_param("ssi", $messageId, $participantId, $isRead);
        $stmt->execute();
    }
    $stmt->close();

    $conn->commit();

    echo json_encode([
        'success' => true,
        'message' => 'Message sent successfully.',
    ]);
} catch (Exception $e) {
    $conn->rollback();
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
