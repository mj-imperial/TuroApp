<?php
header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    exit(json_encode(['success' => false, 'message' => 'Only POST allowed']));
}

if (! isset($_FILES['file'])) {
    http_response_code(400);
    exit(json_encode(['success' => false, 'message' => 'No file uploaded']));
}

$file = $_FILES['file'];
if ($file['error'] !== UPLOAD_ERR_OK) {
    http_response_code(500);
    exit(json_encode(['success' => false, 'message' => 'Upload error '.$file['error']]));
}

$uploadsDir = __DIR__ . '/uploads/';
if (! is_dir($uploadsDir)) {
    mkdir($uploadsDir, 0755, true);
}

$origName = basename($file['name']);
$ext      = pathinfo($origName, PATHINFO_EXTENSION);
$newName  = uniqid('lec_').'.'.$ext;
$target   = $uploadsDir . $newName;

if (! move_uploaded_file($file['tmp_name'], $target)) {
    http_response_code(500);
    exit(json_encode(['success' => false, 'message' => 'Failed to move uploaded file']));
}

$baseUrl = (isset($_SERVER['HTTPS'])?'https':'http').'://'.$_SERVER['HTTP_HOST']
         . dirname($_SERVER['PHP_SELF']);
$fileUrl = $baseUrl . '/uploads/' . $newName;

echo json_encode([
    'success'   => true,
    'file_url'   => $fileUrl,
    'file_name'  => $origName,
    'mime_type'  => $file['type']
]);
