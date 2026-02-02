# 🇲🇦 Darija Translator - English to Moroccan Arabic Translation Service

A complete AI-powered translation system for converting English text to Moroccan Arabic (Darija) dialect, featuring a secure REST API, web client, and Chrome extension.

![Java](https://img.shields.io/badge/Java-17-orange)
![JAX-RS](https://img.shields.io/badge/JAX--RS-3.1-blue)
![PHP](https://img.shields.io/badge/PHP-7.4+-purple)
![Chrome Extension](https://img.shields.io/badge/Chrome-Manifest%20V3-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 📋 Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Security](#security)
- [Clients](#clients)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## ✨ Features

### REST API
- ✅ RESTful web service built with JAX-RS (Jersey)
- ✅ AI-powered translation using Google Gemini API
- ✅ Jakarta Authentication with Basic Auth
- ✅ Role-based access control (USER, ADMIN)
- ✅ Password hashing with SHA-256 and salt
- ✅ JSON request/response format
- ✅ CORS support for cross-origin requests

### PHP Web Client
- ✅ Modern, responsive user interface
- ✅ Real-time character counter
- ✅ Translation history with local storage
- ✅ Copy to clipboard functionality
- ✅ Service health monitoring
- ✅ Error handling with user-friendly messages

### Chrome Extension
- ✅ Manifest V3 compliance
- ✅ Side Panel API integration
- ✅ Context menu for selected text translation
- ✅ Persistent translation history
- ✅ One-click translation from toolbar
- ✅ Works on any webpage

## 🏗️ Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENTS                               │
├──────────────┬──────────────────┬──────────────────────────┤
│  Web Client  │  Chrome Extension │      Postman/cURL        │
│    (PHP)     │   (Manifest V3)   │    (API Testing)         │
└──────┬───────┴────────┬─────────┴──────────┬───────────────┘
       │                 │                     │
       │    HTTP Requests with Basic Auth     │
       │                 │                     │
       └─────────────────┼─────────────────────┘
                         │
                         ▼
          ┌──────────────────────────────┐
          │     Authentication Filter     │
          │   (Basic Auth Validation)     │
          └──────────────┬───────────────┘
                         │
                         ▼
          ┌──────────────────────────────┐
          │      REST API Endpoints       │
          │    (JAX-RS/Jersey 3.1.5)     │
          │                               │
          │  • POST /api/translator/      │
          │         translate             │
          │  • GET  /api/translator/      │
          │         health                │
          │  • GET  /api/translator/me    │
          └──────────────┬───────────────┘
                         │
                         ▼
          ┌──────────────────────────────┐
          │   Translation Service Layer   │
          │    (Business Logic)           │
          └──────────────┬───────────────┘
                         │
                         ▼
          ┌──────────────────────────────┐
          │     Google Gemini API         │
          │   (AI Translation Engine)     │
          └───────────────────────────────┘
```

## 🛠️ Technologies

### Backend
- **Java 17** - Programming language
- **JAX-RS 3.1 (Jersey)** - REST API framework
- **Jakarta Servlet 6.0** - Web application framework
- **Jakarta Authentication** - Security specification
- **Apache Tomcat 10** - Application server
- **Maven 3.x** - Build and dependency management
- **Google Gemini API** - AI translation engine
- **OkHttp 4.12** - HTTP client
- **Jackson 2.16** - JSON processing

### Frontend (PHP Client)
- **PHP 7.4+** - Server-side scripting
- **HTML5/CSS3** - Modern web standards
- **JavaScript (ES6+)** - Client-side interactivity
- **cURL** - HTTP client for API calls

### Chrome Extension
- **Manifest V3** - Latest Chrome extension standard
- **Chrome Side Panel API** - Modern UI integration
- **JavaScript ES6+** - Extension logic
- **Chrome Storage API** - Persistent data storage

## 📦 Prerequisites

### For REST API
- Java Development Kit (JDK) 17 or higher
- Apache Maven 3.6+
- Apache Tomcat 10.x
- Eclipse IDE (or any Java IDE)
- Google Gemini API key ([Get one here](https://makersuite.google.com/app/apikey))

### For PHP Client
- PHP 7.4 or higher
- cURL extension enabled
- Web server (Apache/Nginx) or PHP built-in server

### For Chrome Extension
- Google Chrome 88+ (for Manifest V3 support)
- Developer mode enabled in Chrome

## 🚀 Installation

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/darija-translator.git
cd darija-translator
```

### 2. Backend Setup (REST API)

#### A. Configure Google Gemini API

1. Get your API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Create `src/main/resources/config.properties`:
```properties
gemini.api.key=YOUR_GEMINI_API_KEY_HERE
```

#### B. Configure Users

The default users are:
- **Admin**: `admin` / `admin123`
- **User**: `user` / `user123`
- **Test**: `test` / `test123`

To add or modify users, edit `src/main/resources/users.properties`

To generate encoded passwords, run:
```bash
cd TranslatorService
# Create a temporary utility to generate passwords
# Or use the PasswordGeneratorUtil class provided in the project
```

#### C. Build and Deploy

**Using Eclipse:**

1. Import as Maven project: `File → Import → Existing Maven Projects`
2. Select the `TranslatorService` folder
3. Right-click project → `Maven → Update Project`
4. Right-click project → `Run As → Maven build...`
   - Goals: `clean package`
5. Deploy to Tomcat:
   - Right-click project → `Run As → Run on Server`
   - Select Tomcat 10.x

**Using Command Line:**
```bash
cd TranslatorService
mvn clean package
# Deploy the generated WAR file from target/translator.war to Tomcat webapps/
cp target/translator.war /path/to/tomcat/webapps/
```

#### D. Verify Installation
```bash
# Check health endpoint (no auth required)
curl http://localhost:8080/translator/api/translator/health

# Expected response:
# {"status":"Translation service is running","timestamp":1234567890}
```

### 3. PHP Client Setup
```bash
cd translator-client

# Configure API endpoint
# Edit config.php and set:
# - API_BASE_URL
# - API_USERNAME
# - API_PASSWORD

# Start PHP development server
php -S localhost:8000

# Or copy to your web server directory
sudo cp -r . /var/www/html/translator-client/
```

Access at: `http://localhost:8000`

### 4. Chrome Extension Setup
```bash
cd darija-translator-extension

# Edit background.js and configure:
# - API_BASE_URL (line 2)
# - API_USERNAME (line 4)
# - API_PASSWORD (line 5)
```

**Install in Chrome:**

1. Open Chrome and go to `chrome://extensions/`
2. Enable "Developer mode" (top-right toggle)
3. Click "Load unpacked"
4. Select the `darija-translator-extension` folder
5. Pin the extension for easy access

## ⚙️ Configuration

### API Configuration

**File: `TranslatorService/src/main/resources/config.properties`**
```properties
# Google Gemini API Configuration
gemini.api.key=YOUR_API_KEY_HERE
```

### User Configuration

**File: `TranslatorService/src/main/resources/users.properties`**
```properties
# Format:
# username.password=ENCODED_PASSWORD
# username.roles=ROLE1,ROLE2
# username.enabled=true/false

admin.password=ENCODED_PASSWORD_HERE
admin.roles=ADMIN,USER
admin.enabled=true

user.password=ENCODED_PASSWORD_HERE
user.roles=USER
user.enabled=true
```

### PHP Client Configuration

**File: `translator-client/config.php`**
```php
<?php
// REST API Configuration
define('API_BASE_URL', 'http://localhost:8080/translator/api');
define('API_USERNAME', 'user');
define('API_PASSWORD', 'user123');
define('API_TIMEOUT', 60);
?>
```

### Chrome Extension Configuration

**File: `darija-translator-extension/background.js`**
```javascript
// Configuration
const API_BASE_URL = 'http://localhost:8080/translator/api';
const API_USERNAME = 'user';
const API_PASSWORD = 'user123';
```

## 📖 Usage

### Using the REST API Directly

#### 1. Health Check (No Authentication)
```bash
curl http://localhost:8080/translator/api/translator/health
```

**Response:**
```json
{
  "status": "Translation service is running",
  "timestamp": 1707753600000
}
```

#### 2. Translate Text (With Authentication)
```bash
curl -X POST http://localhost:8080/translator/api/translator/translate \
  -u user:user123 \
  -H "Content-Type: application/json" \
  -d '{"text":"Hello, how are you?"}'
```

**Response:**
```json
{
  "originalText": "Hello, how are you?",
  "translatedText": "السلام، كيفاش داير؟",
  "sourceLanguage": "English",
  "targetLanguage": "Moroccan Darija",
  "timestamp": 1707753600000
}
```

#### 3. Get Current User Info
```bash
curl -u user:user123 \
  http://localhost:8080/translator/api/translator/me
```

**Response:**
```json
{
  "username": "user",
  "admin": false,
  "timestamp": 1707753600000
}
```

### Using the PHP Client

1. Open `http://localhost:8000` in your browser
2. Enter English text in the left textarea
3. Click "Translate" button
4. View Darija translation on the right
5. Use "Copy" button to copy the translation
6. View translation history at the bottom

### Using the Chrome Extension

**Method 1: Extension Icon**
1. Click the Darija Translator icon in Chrome toolbar
2. Side panel opens automatically
3. Enter text and click "Translate"

**Method 2: Context Menu**
1. Select any English text on a webpage
2. Right-click
3. Choose "Translate to Darija"
4. Side panel opens with translation

**Method 3: Keyboard Shortcut**
1. Open side panel
2. Type text
3. Press `Ctrl + Enter` to translate

## 📚 API Documentation

### Endpoints

#### `GET /api/translator/health`
Check service health status.

- **Authentication**: None required
- **Response**: `200 OK`
```json
{
  "status": "Translation service is running",
  "timestamp": 1707753600000
}
```

---

#### `POST /api/translator/translate`
Translate English text to Moroccan Darija.

- **Authentication**: Required (Basic Auth)
- **Required Roles**: `USER` or `ADMIN`
- **Content-Type**: `application/json`

**Request Body:**
```json
{
  "text": "Your English text here"
}
```

**Response:** `200 OK`
```json
{
  "originalText": "Your English text here",
  "translatedText": "الترجمة بالدارجة المغربية",
  "sourceLanguage": "English",
  "targetLanguage": "Moroccan Darija",
  "timestamp": 1707753600000
}
```

**Error Responses:**
- `400 Bad Request` - Invalid input
- `401 Unauthorized` - Missing or invalid credentials
- `500 Internal Server Error` - Translation service error

---

#### `GET /api/translator/me`
Get current authenticated user information.

- **Authentication**: Required (Basic Auth)
- **Required Roles**: `USER` or `ADMIN`
- **Response**: `200 OK`
```json
{
  "username": "user",
  "admin": false,
  "timestamp": 1707753600000
}
```

### Authentication

All protected endpoints use **HTTP Basic Authentication**.

**Header Format:**
```
Authorization: Basic base64(username:password)
```

**Example:**
```bash
# username: user
# password: user123
# Encoded: dXNlcjp1c2VyMTIz

curl -H "Authorization: Basic dXNlcjp1c2VyMTIz" \
  http://localhost:8080/translator/api/translator/me
```

### Error Response Format
```json
{
  "error": "Error message description",
  "timestamp": 1707753600000
}
```

## 🔒 Security

### Authentication System

- **Type**: HTTP Basic Authentication (Jakarta Authentication)
- **Password Storage**: SHA-256 hashing with random salt
- **Session**: Stateless (credentials required per request)

### Security Features

1. **Password Hashing**
   - Algorithm: SHA-256
   - Random salt per password
   - Base64 encoding for storage

2. **Role-Based Access Control (RBAC)**
   - Roles: `USER`, `ADMIN`
   - Annotation-based authorization (`@RolesAllowed`)

3. **Request Filtering**
   - Custom authentication filter
   - Pre-authentication validation
   - Security context injection

4. **Public Endpoints**
   - `/health` - Publicly accessible
   - All other endpoints require authentication

### Security Best Practices

⚠️ **Important for Production:**

1. **Use HTTPS** - Basic Auth transmits credentials in Base64 (easily decoded)
2. **Strong Passwords** - Use complex passwords with special characters
3. **Environment Variables** - Never commit credentials to version control
4. **API Key Protection** - Store Gemini API key securely
5. **Rate Limiting** - Implement to prevent brute force attacks
6. **CORS Configuration** - Restrict allowed origins
7. **Regular Updates** - Keep dependencies up to date

### Generating Secure Passwords
```java
// Use the PasswordEncoder utility
String encoded = PasswordEncoder.encode("mySecurePassword123!");
System.out.println(encoded);
```

## 🖥️ Clients

### 1. PHP Web Client

**Features:**
- Clean, modern interface
- Real-time character counting
- Translation history (localStorage)
- Copy to clipboard
- Service health indicator
- Error handling

**Technology Stack:**
- Pure PHP (no framework)
- Vanilla JavaScript
- CSS3 with gradients
- Responsive design

### 2. Chrome Extension

**Features:**
- Side Panel integration (Manifest V3)
- Context menu translation
- Persistent history (Chrome Storage API)
- Service health monitoring
- Keyboard shortcuts

**Permissions:**
- `sidePanel` - Display translation interface
- `storage` - Save translation history
- `activeTab` - Access current page
- `contextMenus` - Right-click menu

## 📸 Screenshots

### REST API (Postman)

![Postman Health Check](screenshots/postman-health.png)
*Health check endpoint (no authentication required)*

![Postman Translation](screenshots/postman-translate.png)
*Translation with Basic Auth*

### PHP Web Client

![PHP Client Interface](screenshots/php-client.png)
*Modern web interface with translation history*

### Chrome Extension

![Chrome Extension Side Panel](screenshots/chrome-extension.png)
*Side panel integration with context menu*

## 🧪 Testing

### Unit Tests
```bash
cd TranslatorService
mvn test
```

### Integration Tests with Postman

Import the Postman collection from `tests/postman-collection.json`

**Test Cases:**
1. Health check (no auth)
2. Translation without auth (should fail)
3. Translation with valid auth (should succeed)
4. Translation with invalid auth (should fail)
5. Get user info

### Manual Testing Checklist

- [ ] REST API responds to health check
- [ ] Translation works with valid credentials
- [ ] Authentication rejects invalid credentials
- [ ] PHP client displays translations correctly
- [ ] Chrome extension opens side panel
- [ ] Context menu translation works
- [ ] History is saved and displayed
- [ ] Copy to clipboard functions properly

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards

- **Java**: Follow Oracle Java Code Conventions
- **PHP**: Follow PSR-12 coding standard
- **JavaScript**: Use ESLint with Airbnb style guide
- **Documentation**: Update README for any new features

## 🐛 Troubleshooting

### Common Issues

**Issue: "Service Offline" in clients**
- ✅ Verify REST API is running on correct port
- ✅ Check API_BASE_URL in client configuration
- ✅ Ensure no firewall blocking

**Issue: "401 Unauthorized"**
- ✅ Verify username and password are correct
- ✅ Check Basic Auth header format
- ✅ Ensure user exists in users.properties

**Issue: "Translation failed"**
- ✅ Verify Gemini API key is valid
- ✅ Check internet connection
- ✅ Review API quota limits

**Issue: Chrome extension not loading**
- ✅ Check Chrome version (requires 88+)
- ✅ Verify Manifest V3 compatibility
- ✅ Check for JavaScript errors in console

**Issue: PHP client connection error**
- ✅ Ensure cURL extension is enabled
- ✅ Verify API endpoint is reachable
- ✅ Check CORS configuration

### Debug Mode

Enable debug logging in `background.js`:
```javascript
const DEBUG = true; // Set to true for detailed logs
```

View logs in:
- Chrome Extension: `chrome://extensions` → Extension details → Inspect service worker
- REST API: Tomcat logs in `catalina.out`

#
```

## 👤 Contact

**Zouhir Aamrani**
- GitHub: [@ZouhirAamrani](https://github.com/ZouhirAamrani)
- Email: aamrani.zouhir@gmail.com
- LinkedIn: [AAMRANI ZOUHIR](https://www.linkedin.com/in/zouhir-aamrani/)

## 🙏 Acknowledgments

- [Google Gemini](https://deepmind.google/technologies/gemini/) - AI translation engine
- [Jersey Framework](https://eclipse-ee4j.github.io/jersey/) - JAX-RS implementation
- [Apache Tomcat](https://tomcat.apache.org/) - Application server
- [Chrome Extensions](https://developer.chrome.com/docs/extensions/) - Extension platform

---

**Made with ❤️ for Morocco** 🇲🇦

**Star ⭐ this repository if you found it helpful!**