package com.ainpcconnector.ai.custom;

import com.ainpcconnector.ai.AIProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Custom endpoint provider for OpenAI-compatible APIs (e.g., Ollama, LM Studio,
 * etc.).
 */
public class CustomEndpointProvider implements AIProvider {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final String apiKey;
    private final String endpoint;
    private final String model;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;

    public CustomEndpointProvider(String apiKey, String endpoint, String model) {
        this.apiKey = apiKey != null ? apiKey : "";
        // Remove /v1/v1 if present (common mistake)
        this.endpoint = (endpoint != null && !endpoint.isEmpty())
                ? endpoint.replace("/v1/v1", "/v1")
                : "http://localhost:11434/v1"; // Ollama default
        this.model = model != null ? model : "llama3";

        this.httpClient = new OkHttpClient.Builder()
                .callTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        this.executorService = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "CustomAI-Request");
            t.setDaemon(true);
            return t;
        });
    }

    @Override
    public CompletableFuture<String> chatCompletion(String systemPrompt, String userMessage,
            String conversationHistory) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String requestBody = buildRequestBody(systemPrompt, userMessage, conversationHistory);

                Request.Builder requestBuilder = new Request.Builder()
                        .url(endpoint + "/chat/completions")
                        .addHeader("Content-Type", "application/json")
                        .post(RequestBody.create(requestBody, JSON));

                // Add API key if provided
                if (!apiKey.isEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer " + apiKey);
                }

                Request request = requestBuilder.build();

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
                // OpenAI-compatible TTS request body
                String json = "{" +
                        "\"model\":\"" + model + "\"," + // Usually models like 'tts-1' if supported
                        "\"input\":\"" + escapeJson(text) + "\"," +
                        "\"voice\":\"alloy\"," +
                        "\"response_format\":\"pcm\"" +
                        "}";

                Request.Builder requestBuilder = new Request.Builder()
                        .url(endpoint + "/audio/speech")
                        .addHeader("Content-Type", "application/json")
                        .post(RequestBody.create(json, JSON));

                if (!apiKey.isEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer " + apiKey);
                }

                try (Response response = httpClient.newCall(requestBuilder.build()).execute()) {
                    if (!response.isSuccessful()) {
                        // Fallback: Custom providers often don't support TTS
                        return new byte[0];
                    }

                    return response.body() != null ? response.body().bytes() : new byte[0];
                }
            } catch (Exception e) {
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
        return "Custom";
    }

    @Override
    public boolean isConfigured() {
        return endpoint != null && !endpoint.isEmpty();
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
