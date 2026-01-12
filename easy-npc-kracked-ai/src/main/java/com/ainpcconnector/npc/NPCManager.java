package com.ainpcconnector.npc;

import com.ainpcconnector.AINpcConnectorMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Manages detection, tracking, and lifecycle of NPCs.
 */
public class NPCManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(NPCManager.class);

    private final NPCRegistry registry;

    private int tickCounter = 0;

    public NPCManager() {
        this.registry = NPCRegistry.getInstance();
    }

    /**
     * Initialize event listeners for NPC detection.
     */
    public void initialize() {
        // Listen for entity spawning
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (isEasyNPC(entity)) {
                onNPCLoaded(entity, world);
            }
        });

        // Listen for entity unloading
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (isEasyNPC(entity)) {
                onNPCUnloaded(entity);
            }
        });

        // Tick event for NPC behavior processing
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter >= 40) { // Every 2 seconds
                tickCounter = 0;
                onServerTick(server);
            }
        });

        LOGGER.info("[Easy NPC kracked AI] NPC Manager initialized");
    }

    /**
     * Check if an entity is an Easy NPC.
     */
    private boolean isEasyNPC(Entity entity) {
        Identifier entityId = EntityType.getId(entity.getType());
        if (entityId == null) {
            return false;
        }

        String path = entityId.getPath();
        String namespace = entityId.getNamespace();

        // Check for Easy NPC entity types
        return namespace.equals("easynpc") ||
                path.contains("humanoid") ||
                path.contains("npc");
    }

    /**
     * Called when an NPC is loaded into the world.
     */
    private void onNPCLoaded(Entity entity, ServerWorld world) {
        UUID uuid = entity.getUuid();

        LOGGER.info("[Easy NPC kracked AI] Easy NPC detected: {} ({})",
                entity.getName().getString(), uuid);

        // Get or create profile
        NPCProfile profile = registry.getOrCreateProfile(entity);

        // Update position tracking
        // Using stable coordinate access instead of version-sensitive
        // getPos()/getBlockPos()
        profile.setLastKnownPosition(new net.minecraft.util.math.Vec3d(entity.getX(), entity.getY(), entity.getZ()));
        profile.setHomePosition(
                new net.minecraft.util.math.BlockPos((int) entity.getX(), (int) entity.getY(), (int) entity.getZ()));
        profile.setWorldId(world.getRegistryKey().getValue().toString());

        // Fire event for other systems
        AINpcConnectorMod.getAIController().ifPresent(controller -> {
            controller.onNPCLoaded(entity, profile);
        });
    }

    /**
     * Called when an NPC is unloaded from the world.
     */
    private void onNPCUnloaded(Entity entity) {
        UUID uuid = entity.getUuid();

        // Check if the entity is being removed permanently (killed or discarded)
        // If it's just unloading (unloaded to chunk), we keep the profile
        Entity.RemovalReason reason = entity.getRemovalReason();
        if (reason != null && (reason == Entity.RemovalReason.KILLED || reason == Entity.RemovalReason.DISCARDED)) {
            LOGGER.info("[Easy NPC kracked AI] Easy NPC permanently removed ({}): {}", reason, uuid);
            registry.remove(uuid);
        } else {
            LOGGER.debug("[Easy NPC kracked AI] Easy NPC unloaded: {}", uuid);
        }

        AINpcConnectorMod.getAIController().ifPresent(controller -> {
            controller.onNPCUnloaded(entity);
        });
    }

    /**
     * Called every server tick for NPC behavior processing.
     */
    private void onServerTick(net.minecraft.server.MinecraftServer server) {
        // Collect all Easy NPC entities currently loaded across all worlds to avoid
        // crashing version-sensitive getEntity(UUID) calls
        java.util.Map<UUID, Entity> entityMap = new java.util.HashMap<>();
        for (ServerWorld world : server.getWorlds()) {
            for (Entity entity : world.iterateEntities()) {
                if (isEasyNPC(entity)) {
                    entityMap.put(entity.getUuid(), entity);
                }
            }
        }

        // Process AI behavior for all enabled NPCs
        for (NPCProfile profile : registry.getAllProfiles()) {
            if (profile.isAiEnabled() && profile.getStatus() == NPCProfile.NPCStatus.IDLE) {
                // Find the entity in our pre-collected map
                Entity entity = entityMap.get(profile.getEntityUuid());

                if (entity != null && entity.isAlive()) {
                    // Update position using stable coordinate access
                    profile.setLastKnownPosition(
                            new net.minecraft.util.math.Vec3d(entity.getX(), entity.getY(), entity.getZ()));

                    // Process AI behavior
                    AINpcConnectorMod.getAIController().ifPresent(controller -> {
                        controller.processTick(entity, profile);
                    });
                }
            }
        }
    }


    /**
     * Get the NPC registry.
     */
    public NPCRegistry getRegistry() {
        return registry;
    }

    /**
     * Force save all NPC profiles.
     */
    public void saveAll() {
        registry.save();
    }
}
