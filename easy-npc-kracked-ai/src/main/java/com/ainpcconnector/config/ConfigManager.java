package com.ainpcconnector.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ainpcconnector.AINpcConnectorMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Manages loading and saving of mod configuration.
 * Uses SQLite for AI providers and JSON for main config.
 */
public class ConfigManager {

    private static final Path CONFIG_PATH = getConfigPath();

    private static Path getConfigPath() {
        try {
            return FabricLoader.getInstance().getConfigDir().resolve("ainpcconnector.json");
        } catch (Exception e) {
            // Fallback for standalone test environment
            return Path.of("config", "ainpcconnector.json");
        }
    }

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private ModConfig config;
    private DatabaseManager database;

    /**
     * Initialize the ConfigManager with database.
     */
    public void initialize(Path configDir) {
        DatabaseManager.init(configDir.toFile());
        this.database = DatabaseManager.getInstance();
    }

    /**
     * Loads the configuration from file and database, or creates defaults.
     */
    public ModConfig loadConfig() {
        if (config != null) {
            return config;
        }

        // Load main config from JSON
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                config = GSON.fromJson(json, ModConfig.class);
            } catch (IOException e) {
                AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Failed to load config: " + e.getMessage());
            }
        }

        if (config == null) {
            config = new ModConfig();
        }

        // Load AI providers from database
        if (database != null) {
            List<ModConfig.ProviderConfig> providers = database.getAIProviders();
            config.setAiProviders(providers);
        }

        saveConfig();
        return config;
    }

    /**
     * Saves the current configuration to file and database.
     */
    public void saveConfig() {
        if (config == null) {
            return;
        }

        // Save main config to JSON
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = GSON.toJson(config);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Failed to save config: " + e.getMessage());
        }
    }

    /**
     * Updates and saves the configuration.
     */
    public void updateConfig(ModConfig newConfig) {
        this.config = newConfig;
        saveConfig();
    }

    public ModConfig getConfig() {
        return config;
    }

    // ==================== AI PROVIDER METHODS ====================

    public List<ModConfig.ProviderConfig> getAIProviders() {
        if (database != null) {
            return database.getAIProviders();
        }
        return config.getAiProviders();
    }

    public void saveAIProvider(ModConfig.ProviderConfig provider) {
        if (database != null) {
            database.saveAIProvider(provider);
        }
        // Update in-memory config
        loadConfig();
    }

    public void deleteAIProvider(String providerId) {
        if (database != null) {
            database.deleteAIProvider(providerId);
        }
        // Update in-memory config
        loadConfig();
    }

    public DatabaseManager getDatabase() {
        return database;
    }
}
