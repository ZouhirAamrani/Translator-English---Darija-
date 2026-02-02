<?php
require_once 'config.php';
?>
<!DOCTYPE html>
<html lang="en" dir="ltr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>English to Darija Translator</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>
    <div class="container">
        <!-- Header -->
        <header class="header">
            <h1>🇲🇦 English to Darija Translator</h1>
            <p class="subtitle">Powered by AI - Translate English to Moroccan Arabic</p>
            <div class="status-indicator">
                <span class="status-dot" id="statusDot"></span>
                <span class="status-text" id="statusText">Checking service...</span>
            </div>
        </header>

        <!-- Translation Form -->
        <div class="translator-wrapper">
            <form id="translationForm" class="translation-form">
                
                <!-- Source Language Section -->
                <div class="language-section">
                    <div class="language-header">
                        <label class="language-label">English</label>
                        <button type="button" class="clear-btn" id="clearSource" title="Clear text">
                            ✕
                        </button>
                    </div>
                    <textarea 
                        id="sourceText" 
                        name="sourceText" 
                        class="text-input" 
                        placeholder="Enter English text here..."
                        rows="8"
                        required
                    ></textarea>
                    <div class="char-count">
                        <span id="charCount">0</span> characters
                    </div>
                </div>

                <!-- Translate Button -->
                <div class="translate-action">
                    <button type="submit" class="translate-btn" id="translateBtn">
                        <span class="btn-text">Translate</span>
                        <span class="btn-icon">→</span>
                    </button>
                </div>

                <!-- Target Language Section -->
                <div class="language-section">
                    <div class="language-header">
                        <label class="language-label">Moroccan Darija (الدارجة)</label>
                        <button type="button" class="copy-btn" id="copyResult" title="Copy translation" disabled>
                            📋
                        </button>
                    </div>
                    <textarea 
                        id="targetText" 
                        class="text-input" 
                        placeholder="Translation will appear here..."
                        rows="8"
                        readonly
                        dir="rtl"
                    ></textarea>
                    <div class="translation-info" id="translationInfo" style="display: none;">
                        <span id="translationTime"></span>
                    </div>
                </div>

            </form>

            <!-- Loading Indicator -->
            <div class="loading-overlay" id="loadingOverlay" style="display: none;">
                <div class="spinner"></div>
                <p>Translating...</p>
            </div>

            <!-- Error Message -->
            <div class="error-message" id="errorMessage" style="display: none;">
                <span class="error-icon">⚠️</span>
                <span class="error-text" id="errorText"></span>
                <button class="error-close" id="closeError">✕</button>
            </div>
        </div>

        <!-- Translation History -->
        <div class="history-section" id="historySection" style="display: none;">
            <h3>Recent Translations</h3>
            <div class="history-list" id="historyList"></div>
            <button class="clear-history-btn" id="clearHistory">Clear History</button>
        </div>

        <!-- Footer -->
        <footer class="footer">
            <p>Built with ❤️ for Morocco | Using Google Gemini AI</p>
        </footer>
    </div>

    <script src="js/script.js"></script>
</body>
</html>