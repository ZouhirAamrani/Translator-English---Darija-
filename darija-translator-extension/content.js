// Content script for future enhancements
// Currently passive, but can be used for:
// - Auto-detecting translatable text
// - In-page translation overlays
// - Custom UI elements

console.log('Darija Translator content script loaded');

// Listen for messages from background
chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
  if (request.action === 'getSelectedText') {
    const selectedText = window.getSelection().toString().trim();
    sendResponse({ text: selectedText });
  }
});