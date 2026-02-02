// Global variables
let translationHistory = [];

// DOM Elements
const translationForm = document.getElementById('translationForm');
const sourceText = document.getElementById('sourceText');
const targetText = document.getElementById('targetText');
const translateBtn = document.getElementById('translateBtn');
const clearSourceBtn = document.getElementById('clearSource');
const copyResultBtn = document.getElementById('copyResult');
const charCount = document.getElementById('charCount');
const loadingOverlay = document.getElementById('loadingOverlay');
const errorMessage = document.getElementById('errorMessage');
const errorText = document.getElementById('errorText');
const closeError = document.getElementById('closeError');
const statusDot = document.getElementById('statusDot');
const statusText = document.getElementById('statusText');
const translationInfo = document.getElementById('translationInfo');
const translationTime = document.getElementById('translationTime');
const historySection = document.getElementById('historySection');
const historyList = document.getElementById('historyList');
const clearHistoryBtn = document.getElementById('clearHistory');

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    checkServiceHealth();
    loadHistory();
    setupEventListeners();
});

// Setup event listeners
function setupEventListeners() {
    // Form submission
    translationForm.addEventListener('submit', handleTranslation);
    
    // Character count
    sourceText.addEventListener('input', updateCharCount);
    
    // Clear source text
    clearSourceBtn.addEventListener('click', () => {
        sourceText.value = '';
        updateCharCount();
        sourceText.focus();
    });
    
    // Copy result
    copyResultBtn.addEventListener('click', copyTranslation);
    
    // Close error
    closeError.addEventListener('click', hideError);
    
    // Clear history
    clearHistoryBtn.addEventListener('click', clearHistory);
}

// Check service health
async function checkServiceHealth() {
    try {
        const response = await fetch('translate.php?action=health');
        const data = await response.json();
        
        if (data.success && data.status === 'online') {
            statusDot.classList.add('online');
            statusText.textContent = 'Service Online';
        } else {
            statusDot.classList.add('offline');
            statusText.textContent = 'Service Offline';
        }
    } catch (error) {
        statusDot.classList.add('offline');
        statusText.textContent = 'Service Unavailable';
    }
}

// Update character count
function updateCharCount() {
    const count = sourceText.value.length;
    charCount.textContent = count;
    
    if (count > 5000) {
        charCount.style.color = '#f44336';
    } else {
        charCount.style.color = '#999';
    }
}

// Handle translation
async function handleTranslation(e) {
    e.preventDefault();
    
    const text = sourceText.value.trim();
    
    if (!text) {
        showError('Please enter some text to translate');
        return;
    }
    
    if (text.length > 5000) {
        showError('Text is too long (maximum 5000 characters)');
        return;
    }
    
    showLoading();
    hideError();
    
    try {
        const response = await fetch('translate.php?action=translate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ text })
        });
        
        const data = await response.json();
        
        if (data.success) {
            targetText.value = data.data.translatedText;
            copyResultBtn.disabled = false;
            
            // Show processing time
            translationInfo.style.display = 'block';
            translationTime.textContent = `Translated in ${data.data.processingTime}ms`;
            
            // Add to history
            addToHistory({
                source: data.data.originalText,
                target: data.data.translatedText,
                timestamp: Date.now()
            });
        } else {
            showError(data.error || 'Translation failed');
            targetText.value = '';
            copyResultBtn.disabled = true;
        }
    } catch (error) {
        showError('Connection error: ' + error.message);
        targetText.value = '';
        copyResultBtn.disabled = true;
    } finally {
        hideLoading();
    }
}

// Copy translation
async function copyTranslation() {
    try {
        await navigator.clipboard.writeText(targetText.value);
        
        // Visual feedback
        const originalText = copyResultBtn.textContent;
        copyResultBtn.textContent = '✓';
        copyResultBtn.style.color = '#4caf50';
        
        setTimeout(() => {
            copyResultBtn.textContent = originalText;
            copyResultBtn.style.color = '';
        }, 2000);
    } catch (error) {
        showError('Failed to copy text');
    }
}

// Show loading
function showLoading() {
    loadingOverlay.style.display = 'flex';
    translateBtn.disabled = true;
}

// Hide loading
function hideLoading() {
    loadingOverlay.style.display = 'none';
    translateBtn.disabled = false;
}

// Show error
function showError(message) {
    errorText.textContent = message;
    errorMessage.style.display = 'flex';
}

// Hide error
function hideError() {
    errorMessage.style.display = 'none';
}

// Add to history
function addToHistory(item) {
    translationHistory.unshift(item);
    
    // Keep only last 10 items
    if (translationHistory.length > 10) {
        translationHistory = translationHistory.slice(0, 10);
    }
    
    saveHistory();
    renderHistory();
}

// Save history to localStorage
function saveHistory() {
    localStorage.setItem('translationHistory', JSON.stringify(translationHistory));
}

// Load history from localStorage
function loadHistory() {
    const saved = localStorage.getItem('translationHistory');
    if (saved) {
        translationHistory = JSON.parse(saved);
        renderHistory();
    }
}

// Render history
function renderHistory() {
    if (translationHistory.length === 0) {
        historySection.style.display = 'none';
        return;
    }
    
    historySection.style.display = 'block';
    historyList.innerHTML = '';
    
    translationHistory.forEach((item, index) => {
        const historyItem = document.createElement('div');
        historyItem.className = 'history-item';
        historyItem.innerHTML = `
            <div class="history-source">${escapeHtml(item.source)}</div>
            <div class="history-target">${escapeHtml(item.target)}</div>
            <div class="history-time">${formatTime(item.timestamp)}</div>
        `;
        
        historyItem.addEventListener('click', () => {
            sourceText.value = item.source;
            targetText.value = item.target;
            updateCharCount();
            copyResultBtn.disabled = false;
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
        
        historyList.appendChild(historyItem);
    });
}

// Clear history
function clearHistory() {
    if (confirm('Are you sure you want to clear the translation history?')) {
        translationHistory = [];
        saveHistory();
        renderHistory();
    }
}

// Format timestamp
function formatTime(timestamp) {
    const date = new Date(timestamp);
    const now = new Date();
    const diff = now - date;
    
    if (diff < 60000) {
        return 'Just now';
    } else if (diff < 3600000) {
        const minutes = Math.floor(diff / 60000);
        return `${minutes} minute${minutes > 1 ? 's' : ''} ago`;
    } else if (diff < 86400000) {
        const hours = Math.floor(diff / 3600000);
        return `${hours} hour${hours > 1 ? 's' : ''} ago`;
    } else {
        return date.toLocaleDateString();
    }
}

// Escape HTML
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}