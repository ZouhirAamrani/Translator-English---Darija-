// Configuration
const API_BASE_URL = 'http://localhost:8080/translator/api';
const API_TRANSLATE_ENDPOINT = `${API_BASE_URL}/translator/translate`;
const API_USERNAME = 'user';
const API_PASSWORD = 'user123';

// Create Basic Auth header
function getAuthHeader() {
  const credentials = btoa(`${API_USERNAME}:${API_PASSWORD}`);
  return `Basic ${credentials}`;
}
// Install event
chrome.runtime.onInstalled.addListener(() => {
  console.log('Darija Translator extension installed');
  
  // Create context menu for selected text
  chrome.contextMenus.create({
    id: 'translateToDarija',
    title: 'Translate to Darija',
    contexts: ['selection']
  });
  
  // Set default side panel behavior
  chrome.sidePanel.setPanelBehavior({ openPanelOnActionClick: true })
    .catch((error) => console.error(error));
});

// Handle extension icon click
chrome.action.onClicked.addListener(async (tab) => {
  // Open side panel
  await chrome.sidePanel.open({ windowId: tab.windowId });
});

// Handle context menu clicks
chrome.contextMenus.onClicked.addListener(async (info, tab) => {
  if (info.menuItemId === 'translateToDarija') {
    const selectedText = info.selectionText;
    
    // Open side panel
    await chrome.sidePanel.open({ windowId: tab.windowId });
    
    // Send selected text to side panel
    setTimeout(() => {
      chrome.runtime.sendMessage({
        action: 'translateText',
        text: selectedText
      });
    }, 500);
  }
});

// Listen for messages from content script or side panel
chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
  if (request.action === 'translate') {
    handleTranslation(request.text)
      .then(result => sendResponse(result))
      .catch(error => sendResponse({ 
        success: false, 
        error: error.message 
      }));
    return true; // Will respond asynchronously
  }
  
  if (request.action === 'checkHealth') {
    checkServiceHealth()
      .then(result => sendResponse(result))
      .catch(error => sendResponse({ 
        success: false, 
        error: error.message 
      }));
    return true;
  }
});

// Handle translation API call
async function handleTranslation(text) {
  try {
    if (!text || text.trim() === '') {
      throw new Error('Text is required');
    }
    
    const startTime = Date.now();
    
    const response = await fetch(API_TRANSLATE_ENDPOINT, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': getAuthHeader()
      },
      body: JSON.stringify({ text: text.trim() })
    });
    
    const endTime = Date.now();
    const duration = endTime - startTime;
    
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
    }
    
    const data = await response.json();
    
    return {
      success: true,
      data: {
        originalText: data.originalText,
        translatedText: data.translatedText,
        sourceLanguage: data.sourceLanguage,
        targetLanguage: data.targetLanguage,
        processingTime: duration
      }
    };
  } catch (error) {
    console.error('Translation error:', error);
    return {
      success: false,
      error: error.message
    };
  }
}

// Check service health
async function checkServiceHealth() {
  try {
    const response = await fetch(`${API_BASE_URL}/translator/health`, {
      method: 'GET',
      headers: {
        'Accept': 'application/json'
      }
    });
    
    if (response.ok) {
      return {
        success: true,
        status: 'online'
      };
    } else {
      return {
        success: false,
        status: 'offline'
      };
    }
  } catch (error) {
    return {
      success: false,
      status: 'offline',
      error: error.message
    };
  }
}