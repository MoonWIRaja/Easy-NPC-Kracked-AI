package com.ainpcconnector.ai;

import com.ainpcconnector.ai.openai.OpenAIProvider;
import com.ainpcconnector.config.ModConfig;

/**
 * Factory for creating AI provider instances.
 * Uses generic AI providers from config - works with any OpenAI-compatible API.
 */
public class AIProviderFactory {

    /**
     * Create an AI provider by ID from the config.
     * Returns default provider if ID not found.
     */
    public static AIProvider createProviderById(String providerId, ModConfig config) {
        // Find the provider by ID
        ModConfig.ProviderConfig providerConfig = config.getAIProviderById(providerId);

        if (providerConfig == null) {
            // Use default provider
            String defaultId = config.getAi().getDefaultProviderId();
            if (defaultId != null) {
                providerConfig = config.getAIProviderById(defaultId);
            }
        }

        if (providerConfig == null) {
            // No provider configured, return a dummy one
            return new DummyAIProvider();
        }

        // All providers use OpenAI-compatible API
        return new OpenAIProvider(
                providerConfig.getName(),
                providerConfig.getApiKey(),
                providerConfig.getEndpoint(),
                providerConfig.getModel());
    }

    /**
     * Create an AI provider for a specific NPC profile.
     * Uses NPC's configured provider ID or falls back to default.
     */
    public static AIProvider createForNPC(com.ainpcconnector.npc.NPCProfile profile, ModConfig config) {
        String providerId = profile.getAiProviderId();

        if (providerId == null || providerId.isEmpty()) {
            // Use default provider
            providerId = config.getAi().getDefaultProviderId();
        }

        return createProviderById(providerId, config);
    }

    /**
     * Create the default AI provider from config.
     */
    public static AIProvider createDefault(ModConfig config) {
        String defaultId = config.getAi().getDefaultProviderId();
        return createProviderById(defaultId, config);
    }

    /**
     * Create an AI provider directly from config (for testing).
     */
    public static AIProvider createFromConfig(ModConfig.ProviderConfig providerConfig) {
        return new OpenAIProvider(
                providerConfig.getName(),
                providerConfig.getApiKey(),
                providerConfig.getEndpoint(),
                providerConfig.getModel());
    }

    /**
     * Dummy provider used when no AI provider is configured.
     */
    private static class DummyAIProvider implements AIProvider {
        @Override
        public java.util.concurrent.CompletableFuture<String> chatCompletion(
                String systemPrompt, String userMessage, String conversationHistory) {
            return java.util.concurrent.CompletableFuture.completedFuture(
                    "[No AI provider configured. Please add an AI provider in settings.]");
        }

        @Override
        public java.util.concurrent.CompletableFuture<Boolean> testConnection() {
            return java.util.concurrent.CompletableFuture.completedFuture(false);
        }

        @Override
        public String getName() {
            return "Not Configured";
        }

        @Override
        public boolean isConfigured() {
            return false;
        }

        @Override
        public java.util.concurrent.CompletableFuture<byte[]> generateSpeech(String text) {
            return java.util.concurrent.CompletableFuture.completedFuture(new byte[0]);
        }

        @Override
        public String getModel() {
            return "none";
        }
    }
}
