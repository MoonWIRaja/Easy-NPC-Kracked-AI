package com.ainpcconnector;

import com.ainpcconnector.behavior.AIController;
import com.ainpcconnector.behavior.AutonomousController;
import com.ainpcconnector.config.ConfigManager;
import com.ainpcconnector.npc.NPCManager;
import com.ainpcconnector.web.WebServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Main mod class for Easy NPC kracked AI.
 * Connects Easy NPCs with AI capabilities - Auto-thinking, autonomous NPCs.
 * Features:
 * - Autonomous movement and navigation
 * - NPC-to-NPC communication
 * - Dynamic personality evolution
 * - Human-like behavior patterns
 */
public class AINpcConnectorMod implements ModInitializer {

    public static final String MOD_ID = "easynpcai";
    public static final String MOD_NAME = "Easy NPC kracked AI";
    public static final String MOD_VERSION = "1.2.0";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Singleton instances
    private static ConfigManager configManager;
    private static NPCManager npcManager;
    private static AIController aiController;
    private static AutonomousController autonomousController;
    private static WebServer webServer;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing {} v{}", MOD_NAME, MOD_VERSION);

        // Initialize configuration with SQLite database
        configManager = new ConfigManager();
        Path configDir = FabricLoader.getInstance().getConfigDir();
        configManager.initialize(configDir);
        configManager.loadConfig();
        LOGGER.info("[Easy NPC kracked AI] Configuration loaded with SQLite database");

        // Initialize NPC management
        npcManager = new NPCManager();
        npcManager.initialize();
        LOGGER.info("[Easy NPC kracked AI] NPC Manager initialized");

        // Initialize AI controller
        aiController = new AIController(configManager);
        LOGGER.info("[Easy NPC kracked AI] AI Controller initialized");

        // Initialize autonomous controller
        autonomousController = new AutonomousController(configManager);
        npcManager.setAutonomousController(autonomousController);
        LOGGER.info("[Easy NPC kracked AI] Autonomous Controller initialized - NPCs will now move and think autonomously");

        // Register server lifecycle events
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            LOGGER.info("[Easy NPC kracked AI] Server starting...");

            // Start web server
            webServer = new WebServer(configManager);
            webServer.start();
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LOGGER.info("[Easy NPC kracked AI] Server started. Mod is ready!");
            LOGGER.info("[Easy NPC kracked AI] Access web interface at: http://localhost:{}",
                    configManager.getConfig().getWebServer().getPort());
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            LOGGER.info("[Easy NPC kracked AI] Server stopping...");

            // Save NPC data
            if (npcManager != null) {
                npcManager.saveAll();
            }

            // Stop web server
            if (webServer != null) {
                webServer.stop();
            }

            // Shutdown AI controller
            if (aiController != null) {
                aiController.shutdown();
            }

            // Shutdown autonomous controller
            if (autonomousController != null) {
                autonomousController.shutdown();
            }

            LOGGER.info("[Easy NPC kracked AI] Mod shutdown complete");
        });

        LOGGER.info("[Easy NPC kracked AI] Mod initialized successfully!");
    }

    // Getters for singleton instances

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static NPCManager getNpcManager() {
        return npcManager;
    }

    public static Optional<AIController> getAIController() {
        return Optional.ofNullable(aiController);
    }

    public static Optional<AutonomousController> getAutonomousController() {
        return Optional.ofNullable(autonomousController);
    }

    public static Optional<WebServer> getWebServer() {
        return Optional.ofNullable(webServer);
    }
}
