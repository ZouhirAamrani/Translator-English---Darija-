package com.translator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class GeminiAPIService implements LLMService {
    
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    
    public GeminiAPIService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        this.apiKey = loadApiKey();
    }
    
    private String loadApiKey() {
        // First try to load from environment variable
        String key = System.getenv("GEMINI_API_KEY");
        if (key != null && !key.isEmpty()) {
            return key;
        }
        
        // Then try to load from config.properties
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                key = prop.getProperty("gemini.api.key");
                if (key != null && !key.isEmpty()) {
                    return key;
                }
            }
        } catch (IOException ex) {
            System.err.println("Error loading config.properties: " + ex.getMessage());
        }
        
        throw new IllegalStateException("GEMINI_API_KEY not found. Please set it as environment variable or in config.properties");
    }
    
    @Override
    public String translate(String englishText) throws Exception {
        if (englishText == null || englishText.trim().isEmpty()) {
            throw new IllegalArgumentException("Text to translate cannot be empty");
        }
        
        String prompt = buildTranslationPrompt(englishText);
        String requestBody = buildRequestBody(prompt);
        
        Request request = new Request.Builder()
                .url(GEMINI_API_URL + "?key=" + apiKey)
                .post(RequestBody.create(requestBody, JSON))
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                throw new IOException("Gemini API request failed: " + response.code() + " - " + errorBody);
            }
            
            String responseBody = response.body().string();
            return extractTranslation(responseBody);
            
        } catch (IOException e) {
            throw new Exception("Failed to communicate with Gemini API: " + e.getMessage(), e);
        }
    }
    
    private String buildTranslationPrompt(String englishText) {
        return String.format(
            "Translate the following English text to Moroccan Arabic Darija (Moroccan dialect). " +
            "Use Arabic script and maintain the natural, colloquial tone of Darija. " +
            "Only return the translation, nothing else.\n\n" +
            "English text: \"%s\"\n\n" +
            "Darija translation:",
            englishText
        );
    }
    
    private String buildRequestBody(String prompt) throws Exception {
        String json = String.format(
            "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}", 
            escapeJson(prompt)
        );
        return json;
    }
    
    private String extractTranslation(String responseBody) throws Exception {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");
            
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode content = firstCandidate.path("content");
                JsonNode parts = content.path("parts");
                
                if (parts.isArray() && parts.size() > 0) {
                    String text = parts.get(0).path("text").asText();
                    return text.trim();
                }
            }
            
            throw new Exception("No translation found in Gemini API response");
            
        } catch (Exception e) {
            throw new Exception("Failed to parse Gemini API response: " + e.getMessage(), e);
        }
    }
    
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}