<?php
// 1) Turn off normal PHP warnings, start output buffering
ini_set('display_errors', '1');
error_reporting(E_ALL);
ob_start();

// 2) Shutdown handler to catch fatal errors
register_shutdown_function(function() {
    $err = error_get_last();
    if ($err && in_array($err['type'], [E_ERROR, E_PARSE, E_CORE_ERROR], true)) {
        // Drop any partial output
        while (ob_get_level()) ob_end_clean();
        header('Content-Type: application/json; charset=utf-8', true, 500);
        echo json_encode([
            'success' => false,
            'message' => 'Fatal error: ' . $err['message']
        ]);
    }
});

// 3) JSON helper
function jsonResponseProfile(array $data, int $status = 200): void {
    http_response_code($status);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode($data);
    ob_end_flush();
    exit;
}

// 4) Load config & DB connection
$configFile = __DIR__ . '/config.php';
if (! file_exists($configFile)) {
    jsonResponseProfile(['success'=>false,'message'=>'Config file not found'], 500);
}
require_once $configFile;

// If your DB connection is in a separate file, include it here:
// require_once __DIR__ . '/functions.php';

// 5) Verify $conn exists
if (! isset($conn) || ! ($conn instanceof mysqli)) {
    jsonResponseProfile(['success'=>false,'message'=>'Database connection not available'], 500);
}

// 6) Only allow POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponseProfile(['success'=>false,'message'=>'Method Not Allowed'], 405);
}

// 7) Validate inputs
$user_id = $_POST['user_id'] ?? '';
if (empty($user_id) || ! isset($_FILES['file'])) {
    jsonResponseProfile(['success'=>false,'message'=>'user_id and file are required'], 400);
}
$file = $_FILES['file'];
if ($file['error'] !== UPLOAD_ERR_OK) {
    jsonResponseProfile(['success'=>false,'message'=>'File upload error'], 400);
}

// 8) Validate extension & size
$ext = strtolower(pathinfo($file['name'], PATHINFO_EXTENSION));
if (! in_array($ext, ['jpg','jpeg','png'], true)) {
    jsonResponseProfile(['success'=>false,'message'=>'Invalid file type'], 400);
}
if ($file['size'] > 2 * 1024 * 1024) {  // 2 MB limit
    jsonResponseProfile(['success'=>false,'message'=>'File size exceeds 2 MB'], 400);
}

// 9) Prepare upload directory
$uploadDir = __DIR__ . '/uploads/profile_pics/';
if (! is_dir($uploadDir) && ! mkdir($uploadDir, 0755, true)) {
    jsonResponseProfile(['success'=>false,'message'=>'Failed to create upload directory'], 500);
}

// 10) Move the uploaded file
$newName    = uniqid('profile_', true) . '.' . $ext;
$targetPath = $uploadDir . $newName;
if (! move_uploaded_file($file['tmp_name'], $targetPath)) {
    jsonResponseProfile(['success'=>false,'message'=>'Failed to save file'], 500);
}

// 11) Build the public URL and update DB
$baseUrl       = 'http://10.0.2.2/turo_app/api/v1/uploads/profile_pics/';
$profilePicUrl = $baseUrl . $newName;

$stmt = $conn->prepare("
    UPDATE `User`
    SET profile_pic = ?
    WHERE user_id = ?
");
$stmt->bind_param('ss', $profilePicUrl, $user_id);
if (! $stmt->execute()) {
  error_log('MySQL error: '. $stmt->error);
  jsonResponseProfile(['success'=>false,'message'=>'Database update failed'], 500);
}

// 12) Success response
jsonResponseProfile([
    'success'          => true,
    'message'          => 'Profile picture updated',
    'profile_pic_url'  => $profilePicUrl
], 200);