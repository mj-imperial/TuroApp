<?php
date_default_timezone_set('Asia/Manila');

$dbHost    = '127.0.0.1';
$dbName    = 'turo_app';
$dbUser    = 'root';
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

define('SMTP_HOST',   'smtp.gmail.com');
define('SMTP_PORT',   587);
define('SMTP_USER',   'turoapplication40@gmail.com');
define('SMTP_PASS',   'ergi kqhi npgj iecr');
define('SMTP_SECURE', 'tls');

define('MAIL_FROM_EMAIL','no-reply@yourdomain.com');
define('MAIL_FROM_NAME', 'Turo App');

require_once '/xampp/htdocs/turo_app/api/v1/functions.php';
