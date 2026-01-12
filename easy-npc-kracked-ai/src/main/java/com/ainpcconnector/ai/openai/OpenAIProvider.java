package com.ainpcconnector.ai.openai;

import com.ainpcconnector.ai.AIProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic OpenAI-compatible provider implementation.
 * Works with any AI service that uses the OpenAI API format:
 * - OpenAI
 * - Azure OpenAI
 * - Anthropic (via OpenAI-compatible endpoint)
 * - Ollama
 * - LocalAI
 * - vLLM
 * - And many more...
 */
public class OpenAIProvider implements AIProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAIProvider.class);
    private static final String DEFAULT_ENDPOINT = "https://api.openai.com/v1";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final String name;
    private final String apiKey;
    private final String endpoint;
    private final String model;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;

    public OpenAIProvider(String name, String apiKey, String endpoint, String model) {
        this.name = name != null ? name : "AI";
        this.apiKey = apiKey != null ? apiKey : "";
        this.endpoint = endpoint != null && !endpoint.isEmpty() ? endpoint : DEFAULT_ENDPOINT;
        this.model = model != null ? model : "gpt-4o";

        this.httpClient = new OkHttpClient.Builder()
                .callTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        this.executorService = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "OpenAI-Request");
            t.setDaemon(true);
            return t;
        });
    }

    @Override
    public CompletableFuture<String> chatCompletion(String systemPrompt, String userMessage,
            String conversationHistory) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Build request body
                String requestBody = buildRequestBody(systemPrompt, userMessage, conversationHistory);

                Request request = new Request.Builder()
                        .url(endpoint + "/chat/completions")
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .addHeader("Content-Type", "application/json")
                        .post(RequestBody.create(requestBody, JSON))
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    String responseBody = response.body() != null ? response.body().string() : "";
                    return parseResponse(responseBody);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to get AI response", e);
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<byte[]> generateSpeech(String text) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // OpenAI TTS request body
                String json = "{" +
                        "\"model\":\"tts-1\"," +
                        "\"input\":\"" + escapeJson(text) + "\"," +
                        "\"voice\":\"alloy\"," +
                        "\"response_format\":\"pcm\"" +
                        "}";

                Request request = new Request.Builder()
                        .url(endpoint + "/audio/speech")
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .addHeader("Content-Type", "application/json")
                        .post(RequestBody.create(json, JSON))
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("TTS Request failed: " + response);
                    }

                    return response.body() != null ? response.body().bytes() : new byte[0];
                }
            } catch (Exception e) {
                LOGGER.error("[OpenAI] Failed to generate speech", e);
                return new byte[0];
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Boolean> testConnection() {
        return chatCompletion("You are a test.", "Say 'OK'", "")
                .thenApply(response -> response != null && !response.isEmpty())
                .exceptionally(e -> false);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }

    @Override
    public String getModel() {
        return model;
    }

    private String buildRequestBody(String systemPrompt, String userMessage, String conversationHistory) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"model\":\"").append(model).append("\",");
        json.append("\"messages\":[");

        // System message
        json.append("{\"role\":\"system\",\"content\":\"").append(escapeJson(systemPrompt)).append("\"}");

        // Add conversation history if present
        if (conversationHistory != null && !conversationHistory.isEmpty()) {
            // Simple approach: add history as a single user message
            // In a full implementation, you'd parse the history into individual messages
            json.append(",{\"role\":\"user\",\"content\":\"")
                    .append(escapeJson("Previous conversation:\n" + conversationHistory)).append("\"}");
        }

        // Current user message
        json.append(",{\"role\":\"user\",\"content\":\"").append(escapeJson(userMessage)).append("\"}");

        json.append("],");
        json.append("\"max_tokens\":500");
        json.append("}");

        return json.toString();
    }

    private String parseResponse(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode choices = root.get("choices");

        if (choices != null && choices.isArray() && choices.size() > 0) {
            JsonNode message = choices.get(0).get("message");
            if (message != null) {
                JsonNode content = message.get("content");
                if (content != null) {
                    return content.asText();
                }
            }
        }

        throw new IOException("Invalid response format: " + responseBody);
    }

    private String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
