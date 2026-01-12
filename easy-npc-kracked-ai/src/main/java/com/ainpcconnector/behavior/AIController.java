package com.ainpcconnector.behavior;

import com.ainpcconnector.ai.AIProvider;
import com.ainpcconnector.ai.AIProviderFactory;
import com.ainpcconnector.config.ConfigManager;
import com.ainpcconnector.npc.NPCProfile;
import com.ainpcconnector.npc.NPCRegistry;
import com.ainpcconnector.voice.VoiceIntegration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main controller for AI NPC behavior.
 * Handles interactions, AI requests, and responses.
 */
public class AIController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AIController.class);

    private final ConfigManager configManager;
    private final NPCRegistry npcRegistry;
    private final VoiceIntegration voiceIntegration;

    private final ExecutorService executorService;
    private final Map<UUID, CompletableFuture<String>> activeRequests = new ConcurrentHashMap<>();

    private long lastTickTime = 0;

    public AIController(ConfigManager configManager) {
        this.configManager = configManager;
        this.npcRegistry = NPCRegistry.getInstance();
        this.voiceIntegration = new VoiceIntegration();

        this.executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                r -> {
                    Thread t = new Thread(r, "AI-NPC-Worker");
                    t.setDaemon(true);
                    return t;
                });

        LOGGER.info("[Easy NPC kracked AI] AI Controller initialized");
    }

    /**
     * Called when an NPC is loaded into the world.
     */
    public void onNPCLoaded(Entity entity, NPCProfile profile) {
        // Initialize NPC if AI is enabled
        if (profile.isAiEnabled()) {
            LOGGER.info("[Easy NPC kracked AI] AI enabled for NPC: {}", profile.getEntityName());
        }
    }

    /**
     * Called when an NPC is unloaded from the world.
     */
    public void onNPCUnloaded(Entity entity) {
        // Cancel any pending requests for this NPC
        CompletableFuture<String> request = activeRequests.remove(entity.getUuid());
        if (request != null) {
            request.cancel(true);
        }
    }

    /**
     * Handle a player interacting with an NPC.
     */
    public void handlePlayerInteraction(ServerPlayerEntity player, Entity entity, String message) {
        NPCProfile profile = npcRegistry.getProfile(entity.getUuid());
        if (profile == null) {
            profile = npcRegistry.getOrCreateProfile(entity);
        }

        if (!profile.isAiEnabled()) {
            player.sendMessage(Text.literal("This NPC is not AI-enabled. Configure it in the web interface."));
            return;
        }

        // Check if there's already an active request
        if (activeRequests.containsKey(entity.getUuid())) {
            player.sendMessage(Text.literal("Please wait, the NPC is thinking..."));
            return;
        }

        // Set status to thinking
        profile.setStatus(NPCProfile.NPCStatus.THINKING);

        // Get AI provider
        AIProvider provider = AIProviderFactory.createForNPC(profile, configManager.getConfig());
        if (!provider.isConfigured()) {
            player.sendMessage(Text.literal("AI provider is not configured. Please set API key in the web interface."));
            profile.setStatus(NPCProfile.NPCStatus.IDLE);
            return;
        }

        // Generate system prompt based on personality
        String systemPrompt = PersonalityEngine.generateSystemPrompt(profile);

        // Get conversation history
        String history = profile.getConversationHistory();

        // Create final references for lambda
        final Entity finalEntity = entity;
        final NPCProfile finalProfile = profile;
        final String finalMessage = message;

        // Create request
        CompletableFuture<String> request = provider.chatCompletion(systemPrompt, finalMessage, history);

        // Store active request
        activeRequests.put(entity.getUuid(), request);

        // Handle response
        request.thenAccept(response -> {
            activeRequests.remove(finalEntity.getUuid());

            // Send response to player
            player.sendMessage(Text.literal("<" + finalProfile.getEntityName() + "> " + response));

            // Update conversation history
            finalProfile.addToConversationHistory("Player: " + finalMessage);
            finalProfile.addToConversationHistory("NPC: " + response);

            // Evolve personality
            PersonalityEngine.evolve(finalProfile, finalMessage, response);

            // Save updated profile
            npcRegistry.register(finalProfile);

            // Play voice if enabled
            if (finalProfile.isVoiceEnabled() && configManager.getConfig().getVoice().isEnabled()) {
                voiceIntegration.speak(finalEntity, response);
            }

            finalProfile.setStatus(NPCProfile.NPCStatus.IDLE);
        }).exceptionally(ex -> {
            activeRequests.remove(finalEntity.getUuid());
            LOGGER.error("[Easy NPC kracked AI] AI request failed for NPC {}", finalProfile.getEntityName(), ex);
            player.sendMessage(Text.literal("The NPC couldn't respond right now."));
            finalProfile.setStatus(NPCProfile.NPCStatus.IDLE);
            return null;
        });
    }

    /**
     * Process AI behavior on server tick.
     */
    public void processTick(Entity entity, NPCProfile profile) {
        if (!profile.isAiEnabled()) {
            return;
        }

        // Check if it's time for AI thinking
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTickTime < configManager.getConfig().getNpc().getAiThinkIntervalTicks() * 50) {
            return;
        }
        lastTickTime = currentTime;

        // Random autonomous behavior could be added here
        // For example: random movement, idle animations, etc.
    }

    /**
     * Look at nearby players (social behavior).
     */
    public void lookAtNearbyPlayers(MobEntity entity) {
        Vec3d pos = entity.getPos();
        double range = 16.0; // 16 blocks

        Entity nearestPlayer = entity.getWorld().getClosestPlayer(
                pos.x, pos.y, pos.z, range, false);

        if (nearestPlayer != null && nearestPlayer instanceof ServerPlayerEntity) {
            entity.getLookControl().lookAt(nearestPlayer);
        }
    }

    /**
     * Shutdown the controller.
     */
    public void shutdown() {
        executorService.shutdown();
        voiceIntegration.shutdown();
    }

    /**
     * Get the voice integration instance.
     */
    public VoiceIntegration getVoiceIntegration() {
        return voiceIntegration;
    }
}
