package com.ainpcconnector.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import java.util.List;

import java.util.UUID;

/**
 * Main configuration class for Easy NPC kracked AI mod.
 * Uses generic AI providers - users can add any AI service.
 */
public class ModConfig {

    private final WebServerConfig webServer = new WebServerConfig();
    private final AIConfig ai = new AIConfig();
    private final NPCConfig npc = new NPCConfig();
    private final VoiceConfig voice = new VoiceConfig();

    // AI Providers list (loaded from separate file for easier management)
    private List<ProviderConfig> aiProviders = new ArrayList<>();

    public WebServerConfig getWebServer() {
        return webServer;
    }

    public AIConfig getAi() {
        return ai;
    }

    public NPCConfig getNpc() {
        return npc;
    }

    public VoiceConfig getVoice() {
        return voice;
    }

    public List<ProviderConfig> getAiProviders() {
        if (aiProviders == null) {
            aiProviders = new ArrayList<>();
        }
        return aiProviders;
    }

    public void setAiProviders(List<ProviderConfig> providers) {
        this.aiProviders = providers;
    }

    public ProviderConfig getAIProviderById(String id) {
        if (aiProviders == null)
            return null;
        return aiProviders.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static class WebServerConfig {
        private boolean enabled = true;
        private String ip = null; // null = use server IP
        private int port = 8080;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public static class AIConfig {
        private String defaultProviderId = null; // ID of default AI provider

        public String getDefaultProviderId() {
            return defaultProviderId;
        }

        public void setDefaultProviderId(String defaultProviderId) {
            this.defaultProviderId = defaultProviderId;
        }
    }

    /**
     * Generic AI Provider - can be any AI service with OpenAI-compatible API
     */
    public static class ProviderConfig {
        private String id;
        private String name;
        private String apiKey;
        private String endpoint;
        private String model;

        public ProviderConfig() {
            this.id = UUID.randomUUID().toString();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }
    }

    public static class NPCConfig {
        private int aiThinkIntervalTicks = 40; // 2 seconds
        private double personalityEvolutionRate = 0.01;

        public int getAiThinkIntervalTicks() {
            return aiThinkIntervalTicks;
        }

        public void setAiThinkIntervalTicks(int aiThinkIntervalTicks) {
            this.aiThinkIntervalTicks = aiThinkIntervalTicks;
        }

        public double getPersonalityEvolutionRate() {
            return personalityEvolutionRate;
        }

        public void setPersonalityEvolutionRate(double personalityEvolutionRate) {
            this.personalityEvolutionRate = personalityEvolutionRate;
        }
    }

    /**
     * Voice/TTS configuration
     */
    public static class VoiceConfig {
        private boolean enabled = false;
        private String ttsProvider = "browser"; // browser, external

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getTtsProvider() {
            return ttsProvider;
        }

        public void setTtsProvider(String ttsProvider) {
            this.ttsProvider = ttsProvider;
        }
    }

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public String toJson() {
        return GSON.toJson(this);
    }

    public static ModConfig fromJson(String json) {
        return GSON.fromJson(json, ModConfig.class);
    }
}
