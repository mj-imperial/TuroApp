<?php

date_default_timezone_set('Asia/Manila');

$dbHost    = 'localhost';
$dbName    = 'turo_app';
$dbUser    = '';
$dbPass    = '';
$dbCharset = 'utf8mb4';

mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

try {
    $conn = new mysqli($dbHost, $dbUser, $dbPass, $dbName);
    $conn->set_charset($dbCharset);
} catch (mysqli_sql_exception $e) {
    http_response_code(500);
    header('Content-Type: application/json; charset=UTF-8');
    echo json_encode([
        'success' => false,
        'error'   => 'Database connection failed'
    ]);
    exit;
}

?>
