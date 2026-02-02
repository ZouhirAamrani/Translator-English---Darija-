// DOM Elements
const sourceText = document.getElementById('sourceText');
const targetText = document.getElementById('targetText');
const translateBtn = document.getElementById('translateBtn');
const clearBtn = document.getElementById('clearBtn');
const copyBtn = document.getElementById('copyBtn');
const charCount = document.getElementById('charCount');
const loadingOverlay = document.getElementById('loadingOverlay');
const errorAlert = document.getElementById('errorAlert');
const errorText = document.getElementById('errorText');
const closeError = document.getElementById('closeError');
const statusDot = document.getElementById('statusDot');
const statusText = document.getElementById('statusText');
const translationMeta = document.getElementById('translationMeta');
const processingTime = document.getElementById('processingTime');
const historySection = document.getElementById('historySection');
const historyList = document.getElementById('historyList');
const clearHistoryBtn = document.getElementById('clearHistoryBtn');

// State
let translationHistory = [];
const MAX_CHAR_LIMIT = 5000;

// Initialize
document.addEventListener('DOMContentLoaded', () => {
  checkServiceHealth();
  loadHistory();
  setupEventListeners();
});

// Listen for messages from background script
chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
  if (request.action === 'translateText') {
    sourceText.value = request.text;
    updateCharCount();
    handleTranslate();
  }
});

// Setup event listeners
function setupEventListeners() {
  // Translate button
  translateBtn.addEventListener('click', handleTranslate);
  
  // Enter key to translate (Ctrl+Enter)
  sourceText.addEventListener('keydown', (e) => {
    if (e.ctrlKey && e.key === 'Enter') {
      handleTranslate();
    }
  });
  
  // Character count
  sourceText.addEventListener('input', updateCharCount);
  
  // Clear button
  clearBtn.addEventListener('click', () => {
    sourceText.value = '';
    targetText.value = '';
    updateCharCount();
    copyBtn.disabled = true;
    translationMeta.style.display = 'none';
    sourceText.focus();
  });
  
  // Copy button
  copyBtn.addEventListener('click', copyTranslation);
  
  // Close error
  closeError.addEventListener('click', hideError);
  
  // Clear history
  clearHistoryBtn.addEventListener('click', clearHistory);
}

// Check service health
async function checkServiceHealth() {
  try {
    const response = await chrome.runtime.sendMessage({ 
      action: 'checkHealth' 
    });
    
    if (response.success && response.status === 'online') {
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
  
  const charCountElement = charCount.parentElement;
  if (count > MAX_CHAR_LIMIT) {
    charCountElement.classList.add('warning');
  } else {
    charCountElement.classList.remove('warning');
  }
}

// Handle translation
async function handleTranslate() {
  const text = sourceText.value.trim();
  
  if (!text) {
    showError('Please enter some text to translate');
    return;
  }
  
  if (text.length > MAX_CHAR_LIMIT) {
    showError(`Text is too long (maximum ${MAX_CHAR_LIMIT} characters)`);
    return;
  }
  
  showLoading();
  hideError();
  
  try {
    const response = await chrome.runtime.sendMessage({
      action: 'translate',
      text: text
    });
    
    if (response.success) {
      targetText.value = response.data.translatedText;
      copyBtn.disabled = false;
      
      // Show processing time
      translationMeta.style.display = 'block';
      processingTime.textContent = `Translated in ${response.data.processingTime}ms`;
      
      // Add to history
      addToHistory({
        source: response.data.originalText,
        target: response.data.translatedText,
        timestamp: Date.now()
      });
    } else {
      showError(response.error || 'Translation failed');
      targetText.value = '';
      copyBtn.disabled = true;
      translationMeta.style.display = 'none';
    }
  } catch (error) {
    showError('Connection error: ' + error.message);
    targetText.value = '';
    copyBtn.disabled = true;
    translationMeta.style.display = 'none';
  } finally {
    hideLoading();
  }
}

// Copy translation
async function copyTranslation() {
  try {
    await navigator.clipboard.writeText(targetText.value);
    
    // Visual feedback
    const originalHTML = copyBtn.innerHTML;
    copyBtn.innerHTML = '<svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor"><path d="M13.854 3.646a.5.5 0 0 1 0 .708l-7 7a.5.5 0 0 1-.708 0l-3.5-3.5a.5.5 0 1 1 .708-.708L6.5 10.293l6.646-6.647a.5.5 0 0 1 .708 0z"/></svg>';
    copyBtn.style.color = '#4caf50';
    
    setTimeout(() => {
      copyBtn.innerHTML = originalHTML;
      copyBtn.style.color = '';
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
  errorAlert.style.display = 'flex';
}

// Hide error
function hideError() {
  errorAlert.style.display = 'none';
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

// Save history to storage
function saveHistory() {
  chrome.storage.local.set({ translationHistory });
}

// Load history from storage
function loadHistory() {
  chrome.storage.local.get(['translationHistory'], (result) => {
    if (result.translationHistory) {
      translationHistory = result.translationHistory;
      renderHistory();
    }
  });
}

// Render history
function renderHistory() {
  if (translationHistory.length === 0) {
    historySection.style.display = 'none';
    return;
  }
  
  historySection.style.display = 'block';
  historyList.innerHTML = '';
  
  translationHistory.forEach((item) => {
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
      copyBtn.disabled = false;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
    
    historyList.appendChild(historyItem);
  });
}

// Clear history
function clearHistory() {
  if (confirm('Clear all translation history?')) {
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
    return `${minutes}m ago`;
  } else if (diff < 86400000) {
    const hours = Math.floor(diff / 3600000);
    return `${hours}h ago`;
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