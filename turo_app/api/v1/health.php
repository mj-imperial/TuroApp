<?php
header('Content-Type: application/json');
echo json_encode(['status'=>'OK','time'=>date('c')]);
