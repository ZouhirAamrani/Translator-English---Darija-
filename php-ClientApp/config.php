<?php
/**
 * Configuration file for Translation Client
 */

// REST API Configuration
define('API_BASE_URL', 'http://localhost:8080/translator/api');
define('API_TRANSLATE_ENDPOINT', API_BASE_URL . '/translator/translate');
define('API_HEALTH_ENDPOINT', API_BASE_URL . '/translator/health');

// Request timeout in seconds
define('API_TIMEOUT', 60);

// Enable error reporting for development
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Set default timezone
date_default_timezone_set('Africa/Casablanca');
?>