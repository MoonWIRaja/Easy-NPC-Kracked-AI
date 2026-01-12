package com.ainpcconnector.web.handlers;

import com.ainpcconnector.config.ConfigManager;
import com.ainpcconnector.config.ModConfig;
import io.javalin.http.Context;

import java.util.Map;
import java.util.UUID;

/**
 * Handler for configuration endpoints.
 * Now uses generic AI providers - users can add any AI service.
 */
public class ConfigHandler {

    private final ConfigManager configManager;

    public ConfigHandler(ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Get current configuration.
     * GET /api/config
     */
    public void handleGetConfig(Context ctx) {
        ModConfig config = configManager.getConfig();

        // Return config with sensitive data masked
        ctx.json(Map.of(
                "webServer", Map.of(
                        "enabled", config.getWebServer().isEnabled(),
                        "ip", config.getWebServer().getIp(),
                        "port", config.getWebServer().getPort()),
                "ai", Map.of(
                        "defaultProviderId", config.getAi().getDefaultProviderId(),
                        "providers", config.getAiProviders().stream()
                                .map(p -> Map.of(
                                        "id", p.getId(),
                                        "name", p.getName(),
                                        "endpoint", p.getEndpoint(),
                                        "model", p.getModel(),
                                        "hasApiKey", p.getApiKey() != null && !p.getApiKey().isEmpty()))
                                .toList()),
                "npc", Map.of(
                        "aiThinkIntervalTicks", config.getNpc().getAiThinkIntervalTicks(),
                        "personalityEvolutionRate", config.getNpc().getPersonalityEvolutionRate()),
                "voice", Map.of(
                        "enabled", config.getVoice().isEnabled(),
                        "ttsProvider", config.getVoice().getTtsProvider())));
    }

    /**
     * Update configuration (admin only).
     * PUT /api/config
     */
    public void handleUpdateConfig(Context ctx) {
        try {
            ConfigUpdateRequest request = ctx.bodyAsClass(ConfigUpdateRequest.class);
            ModConfig config = configManager.getConfig();

            // Update web server config
            if (request.webServer != null) {
                if (request.webServer.enabled() != null) {
                    config.getWebServer().setEnabled(request.webServer.enabled());
                }
                if (request.webServer.ip() != null) {
                    config.getWebServer().setIp(request.webServer.ip());
                }
                if (request.webServer.port() != null) {
                    config.getWebServer().setPort(request.webServer.port());
                }
            }

            // Update AI config
            if (request.ai != null) {
                if (request.ai.defaultProviderId() != null) {
                    config.getAi().setDefaultProviderId(request.ai.defaultProviderId());
                }
            }

            // Update NPC config
            if (request.npc != null) {
                if (request.npc.aiThinkIntervalTicks() != null) {
                    config.getNpc().setAiThinkIntervalTicks(request.npc.aiThinkIntervalTicks());
                }
                if (request.npc.personalityEvolutionRate() != null) {
                    config.getNpc().setPersonalityEvolutionRate(request.npc.personalityEvolutionRate());
                }
            }

            // Update voice config
            if (request.voice != null) {
                if (request.voice.enabled() != null) {
                    config.getVoice().setEnabled(request.voice.enabled());
                }
                if (request.voice.ttsProvider() != null) {
                    config.getVoice().setTtsProvider(request.voice.ttsProvider());
                }
            }

            configManager.updateConfig(config);

            ctx.json(Map.of("message", "Configuration updated successfully"));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * Get all AI providers.
     * GET /api/config/providers
     */
    public void handleGetProviders(Context ctx) {
        ctx.json(configManager.getAIProviders());
    }

    /**
     * Add a new AI provider.
     * POST /api/config/providers
     */
    public void handleAddProvider(Context ctx) {
        try {
            ProviderRequest request = ctx.bodyAsClass(ProviderRequest.class);

            ModConfig.ProviderConfig provider = new ModConfig.ProviderConfig();
            provider.setId(UUID.randomUUID().toString());
            provider.setName(request.name());
            provider.setApiKey(request.apiKey());
            provider.setEndpoint(request.endpoint());
            provider.setModel(request.model());

            configManager.saveAIProvider(provider);

            ctx.json(Map.of("message", "Provider added successfully", "id", provider.getId()));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Failed to add provider: " + e.getMessage()));
        }
    }

    /**
     * Update an existing AI provider.
     * PUT /api/config/providers/{id}
     */
    public void handleUpdateProvider(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            ProviderRequest request = ctx.bodyAsClass(ProviderRequest.class);
            ModConfig config = configManager.getConfig();

            ModConfig.ProviderConfig provider = config.getAIProviderById(id);
            if (provider == null) {
                ctx.status(404).json(Map.of("error", "Provider not found"));
                return;
            }

            if (request.name() != null) {
                provider.setName(request.name());
            }
            if (request.apiKey() != null) {
                provider.setApiKey(request.apiKey());
            }
            if (request.endpoint() != null) {
                provider.setEndpoint(request.endpoint());
            }
            if (request.model() != null) {
                provider.setModel(request.model());
            }

            configManager.saveAIProvider(provider);

            ctx.json(Map.of("message", "Provider updated successfully"));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Failed to update provider: " + e.getMessage()));
        }
    }

    /**
     * Delete an AI provider.
     * DELETE /api/config/providers/{id}
     */
    public void handleDeleteProvider(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            ModConfig config = configManager.getConfig();

            ModConfig.ProviderConfig provider = config.getAIProviderById(id);
            if (provider == null) {
                ctx.status(404).json(Map.of("error", "Provider not found"));
                return;
            }

            // If this was the default provider, clear the default
            if (id.equals(config.getAi().getDefaultProviderId())) {
                config.getAi().setDefaultProviderId(null);
            }

            configManager.deleteAIProvider(id);
            configManager.updateConfig(config);

            ctx.json(Map.of("message", "Provider deleted successfully"));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Failed to delete provider: " + e.getMessage()));
        }
    }

    public record ConfigUpdateRequest(
            WebServerUpdate webServer,
            AIUpdate ai,
            NPCUpdate npc,
            VoiceUpdate voice) {
    }

    public record WebServerUpdate(
            Boolean enabled,
            String ip,
            Integer port) {
    }

    public record AIUpdate(
            String defaultProviderId) {
    }

    public record NPCUpdate(
            Integer aiThinkIntervalTicks,
            Double personalityEvolutionRate) {
    }

    public record VoiceUpdate(
            Boolean enabled,
            String ttsProvider) {
    }

    public record ProviderRequest(
            String name,
            String apiKey,
            String endpoint,
            String model) {
    }
}
