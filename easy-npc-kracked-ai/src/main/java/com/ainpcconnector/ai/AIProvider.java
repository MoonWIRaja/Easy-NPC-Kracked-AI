package com.ainpcconnector.ai;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for AI provider implementations.
 * Supports multiple AI backends: OpenAI, Anthropic, and custom endpoints.
 */
public interface AIProvider {

    /**
     * Send a chat completion request asynchronously.
     *
     * @param systemPrompt        The system prompt defining AI behavior
     * @param userMessage         The user's message
     * @param conversationHistory Previous conversation context
     * @return CompletableFuture containing the AI response
     */
    CompletableFuture<String> chatCompletion(
            String systemPrompt,
            String userMessage,
            String conversationHistory);

    /**
     * Test if the provider is reachable with current config.
     *
     * @return CompletableFuture containing true if connection successful
     */
    CompletableFuture<Boolean> testConnection();

    /**
     * Get the provider name.
     */
    String getName();

    /**
     * Check if this provider is configured and ready.
     */
    boolean isConfigured();

    /**
     * Generate speech from text (TTS).
     *
     * @param text The text to convert to speech
     * @return CompletableFuture containing audio bytes (PCM 16-bit 24kHz or
     *         similar)
     */
    CompletableFuture<byte[]> generateSpeech(String text);

    /**
     * Get the model name being used.
     */
    String getModel();
}
