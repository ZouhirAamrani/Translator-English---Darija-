<?php
require_once 'config.php';

define('API_USERNAME', 'user');
define('API_PASSWORD', 'user123');

header('Content-Type: application/json');

/**
 * Make HTTP request to translation service
 */
function makeRequest($url, $method = 'GET', $data = null) {
    $ch = curl_init();
    
    // Add Basic Authentication
    $credentials = base64_encode(API_USERNAME . ':' . API_PASSWORD);

    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_TIMEOUT, API_TIMEOUT);
    curl_setopt($ch, CURLOPT_HTTPHEADER, [
        'Content-Type: application/json',
        'Accept: application/json',
        'Authorization: Basic ' . $credentials
    ]);
    
    if ($method === 'POST' && $data !== null) {
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
    }
    
    $response = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $error = curl_error($ch);
    
    //curl_close($ch);
    
    if ($error) {
        return [
            'success' => false,
            'error' => 'Connection error: ' . $error
        ];
    }
    
    $decodedResponse = json_decode($response, true);
    
    if ($httpCode >= 200 && $httpCode < 300) {
        return [
            'success' => true,
            'data' => $decodedResponse,
            'httpCode' => $httpCode
        ];
    } else {
        return [
            'success' => false,
            'error' => $decodedResponse['error'] ?? 'Unknown error occurred',
            'httpCode' => $httpCode
        ];
    }
}

/**
 * Handle translation request
 */
function handleTranslation() {
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        return [
            'success' => false,
            'error' => 'Invalid request method'
        ];
    }
    
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!isset($input['text']) || empty(trim($input['text']))) {
        return [
            'success' => false,
            'error' => 'Text is required'
        ];
    }
    
    $text = trim($input['text']);
    
    // Validate text length
    if (strlen($text) > 5000) {
        return [
            'success' => false,
            'error' => 'Text is too long (maximum 5000 characters)'
        ];
    }
    
    $startTime = microtime(true);
    
    $result = makeRequest(
        API_TRANSLATE_ENDPOINT,
        'POST',
        ['text' => $text]
    );
    
    $endTime = microtime(true);
    $duration = round(($endTime - $startTime) * 1000); // milliseconds
    
    if ($result['success']) {
        return [
            'success' => true,
            'data' => [
                'originalText' => $result['data']['originalText'],
                'translatedText' => $result['data']['translatedText'],
                'sourceLanguage' => $result['data']['sourceLanguage'],
                'targetLanguage' => $result['data']['targetLanguage'],
                'timestamp' => $result['data']['timestamp'],
                'processingTime' => $duration
            ]
        ];
    } else {
        return $result;
    }
}

/**
 * Check service health
 */
function checkHealth() {
    $result = makeRequest(API_HEALTH_ENDPOINT, 'GET');
    
    if ($result['success']) {
        return [
            'success' => true,
            'status' => 'online',
            'message' => $result['data']['status'] ?? 'Service is running'
        ];
    } else {
        return [
            'success' => false,
            'status' => 'offline',
            'message' => 'Service is unavailable'
        ];
    }
}

// Route handling
$action = $_GET['action'] ?? 'translate';

try {
    switch ($action) {
        case 'translate':
            $response = handleTranslation();
            break;
        
        case 'health':
            $response = checkHealth();
            break;
        
        default:
            $response = [
                'success' => false,
                'error' => 'Invalid action'
            ];
    }
    
    echo json_encode($response);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Server error: ' . $e->getMessage()
    ]);
}
?>