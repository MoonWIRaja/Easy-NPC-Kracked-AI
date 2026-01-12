package com.ainpcconnector.web.handlers;

import com.ainpcconnector.ai.AIProvider;
import com.ainpcconnector.ai.AIProviderFactory;
import com.ainpcconnector.config.ConfigManager;
import com.ainpcconnector.config.ModConfig;
import io.javalin.http.Context;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Handler for AI-related endpoints.
 */
public class AIHandler {

    private final ConfigManager configManager;

    public AIHandler(ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Test connection to an AI provider.
     * POST /api/ai/test
     * Body: { "providerId": "provider-id-or-null" }
     * If providerId is null, tests the default provider.
     */
    public void handleTestConnection(Context ctx) {
        try {
            TestRequest request = ctx.bodyAsClass(TestRequest.class);
            ModConfig config = configManager.getConfig();

            String providerId = request.providerId;
            if (providerId == null || providerId.isEmpty()) {
                providerId = config.getAi().getDefaultProviderId();
            }

            if (providerId == null || providerId.isEmpty()) {
                ctx.json(Map.of(
                        "success", false,
                        "error", "No provider configured. Please add an AI provider in settings."
                ));
                return;
            }

            AIProvider aiProvider = AIProviderFactory.createProviderById(providerId, config);

            // Check if configured
            if (!aiProvider.isConfigured()) {
                ctx.json(Map.of(
                        "success", false,
                        "error", "Provider not configured. Please set API key in settings."
                ));
                return;
            }

            // Test connection asynchronously
            CompletableFuture<Boolean> testFuture = aiProvider.testConnection();

            // Wait for result (with timeout)
            Boolean result = testFuture.get(java.time.Duration.ofSeconds(30).toSeconds(),
                    java.util.concurrent.TimeUnit.SECONDS);

            if (Boolean.TRUE.equals(result)) {
                ctx.json(Map.of(
                        "success", true,
                        "provider", aiProvider.getName(),
                        "model", aiProvider.getModel(),
                        "message", "Connection successful"
                ));
            } else {
                ctx.json(Map.of(
                        "success", false,
                        "provider", aiProvider.getName(),
                        "error", "Connection test failed"
                ));
            }
        } catch (java.util.concurrent.TimeoutException e) {
            ctx.status(408).json(Map.of(
                    "success", false,
                    "error", "Connection test timed out"
            ));
        } catch (Exception e) {
            ctx.status(500).json(Map.of(
                    "success", false,
                    "error", "Test failed: " + e.getMessage()
            ));
        }
    }

    public record TestRequest(String providerId) {}
}
