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
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    jsonResponse(['success'=>false,'error'=>'Method Not Allowed'], 405);
}
session_start();
session_unset();
session_destroy();
echo json_encode([
  'success' => true,
  'message' => 'Logged out'
]);