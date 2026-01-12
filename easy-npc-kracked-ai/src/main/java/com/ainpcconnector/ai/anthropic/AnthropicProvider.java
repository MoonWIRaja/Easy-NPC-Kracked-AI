package com.ainpcconnector.ai.anthropic;

import com.ainpcconnector.ai.AIProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Anthropic Claude provider implementation.
 */
public class AnthropicProvider implements AIProvider {

    private static final String DEFAULT_ENDPOINT = "https://api.anthropic.com/v1/messages";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final String apiKey;
    private final String model;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;

    public AnthropicProvider(String apiKey, String model) {
        this.apiKey = apiKey != null ? apiKey : "";
        this.model = model != null ? model : "claude-3-5-sonnet-20241022";

        this.httpClient = new OkHttpClient.Builder()
                .callTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        this.executorService = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "Anthropic-Request");
            t.setDaemon(true);
            return t;
        });
    }

    @Override
    public CompletableFuture<String> chatCompletion(String systemPrompt, String userMessage,
            String conversationHistory) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String fullUserMessage = userMessage;
                if (conversationHistory != null && !conversationHistory.isEmpty()) {
                    fullUserMessage = "Previous conversation:\n" + conversationHistory + "\n\nCurrent message: "
                            + userMessage;
                }

                String requestBody = buildRequestBody(systemPrompt, fullUserMessage);

                Request request = new Request.Builder()
                        .url(DEFAULT_ENDPOINT)
                        .addHeader("x-api-key", apiKey)
                        .addHeader("anthropic-version", "2023-06-01")
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
        // Anthropic does not provide a native TTS API yet
        return CompletableFuture.completedFuture(new byte[0]);
    }

    @Override
    public CompletableFuture<Boolean> testConnection() {
        return chatCompletion("You are a test.", "Say 'OK'", "")
                .thenApply(response -> response != null && !response.isEmpty())
                .exceptionally(e -> false);
    }

    @Override
    public String getName() {
        return "Anthropic";
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }

    @Override
    public String getModel() {
        return model;
    }

    private String buildRequestBody(String systemPrompt, String userMessage) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"model\":\"").append(model).append("\",");
        json.append("\"max_tokens\":500,");
        json.append("\"system\":\"").append(escapeJson(systemPrompt)).append("\",");
        json.append("\"messages\":[");
        json.append("{\"role\":\"user\",\"content\":\"").append(escapeJson(userMessage)).append("\"}");
        json.append("]");
        json.append("}");

        return json.toString();
    }

    private String parseResponse(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode content = root.get("content");

        if (content != null && content.isArray() && content.size() > 0) {
            JsonNode firstBlock = content.get(0);
            if (firstBlock != null) {
                JsonNode text = firstBlock.get("text");
                if (text != null) {
                    return text.asText();
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
