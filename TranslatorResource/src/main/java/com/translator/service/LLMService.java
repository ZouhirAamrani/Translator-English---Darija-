package com.translator.service;

public interface LLMService {
    /**
     * Translates English text to Moroccan Arabic Darija
     * @param englishText The text to translate
     * @return Translated text in Darija
     * @throws Exception if translation fails
     */
    String translate(String englishText) throws Exception;
}