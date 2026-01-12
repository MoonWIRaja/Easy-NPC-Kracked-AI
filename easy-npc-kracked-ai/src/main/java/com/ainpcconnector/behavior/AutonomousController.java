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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * Advanced autonomous controller for AI NPCs.
 * Handles autonomous movement, decision-making, and human-like behaviors.
 */
public class AutonomousController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutonomousController.class);

    private final ConfigManager configManager;
    private final NPCRegistry npcRegistry;
    private final VoiceIntegration voiceIntegration;
    private final SocialSystem socialSystem;

    private final ExecutorService executorService;
    private final Map<UUID, CompletableFuture<String>> activeRequests = new ConcurrentHashMap<>();
    private final Map<UUID, AutonomousState> npcStates = new ConcurrentHashMap<>();

    // Behavior settings
    private static final double MOVEMENT_SPEED = 0.4;
    private static final double INTERACTION_RADIUS = 8.0;
    private static final int AUTONOMOUS_ACTION_INTERVAL = 100; // ticks between autonomous actions

    /**
     * Represents the current autonomous state of an NPC.
     */
    public static class AutonomousState {
        public BehaviorMode currentMode = BehaviorMode.IDLE;
        public Vec3d targetPosition = null;
        public UUID targetEntity = null;
        public int actionTimer = 0;
        public int idleTimer = 0;
        public String lastThought = "";
        public double mood = 0.5; // 0 = unhappy, 1 = happy
        public double energy = 1.0; // 0 = tired, 1 = energetic
        public List<String> currentGoals = new ArrayList<>();

        public AutonomousState() {
            currentMode = BehaviorMode.IDLE;
            randomizeGoals();
        }

        public void randomizeGoals() {
            currentGoals.clear();
            String[] possibleGoals = {"explore", "socialize", "rest", "work", "wander"};
            for (int i = 0; i < 2; i++) {
                currentGoals.add(possibleGoals[new Random().nextInt(possibleGoals.length)]);
            }
        }
    }

    /**
     * Behavior modes for autonomous NPCs.
     */
    public enum BehaviorMode {
        IDLE,           // Standing still, observing
        WANDERING,      // Random movement within radius
        FOLLOWING,      // Following a player or NPC
        CONVERSING,     // In conversation
        WORKING,        // Performing a task
        RESTING,        // Taking a break
        EXPLORING,      // Moving to a new location
        FLEEING,        // Moving away from danger
        SOCIALIZING     // Seeking other NPCs
    }

    public AutonomousController(ConfigManager configManager) {
        this.configManager = configManager;
        this.npcRegistry = NPCRegistry.getInstance();
        this.voiceIntegration = new VoiceIntegration();
        this.socialSystem = SocialSystem.getInstance();

        this.executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                r -> {
                    Thread t = new Thread(r, "AI-NPC-Autonomous-Worker");
                    t.setDaemon(true);
                    return t;
                });

        LOGGER.info("[Easy NPC kracked AI] Autonomous Controller initialized");
    }

    /**
     * Process autonomous behavior for an NPC each tick.
     */
    public void processAutonomousBehavior(ServerWorld world, Entity entity, NPCProfile profile) {
        if (!profile.isAiEnabled()) {
            return;
        }

        UUID uuid = entity.getUuid();
        AutonomousState state = npcStates.computeIfAbsent(uuid, k -> new AutonomousState());

        // Update state timers
        state.actionTimer++;
        if (state.currentMode == BehaviorMode.IDLE) {
            state.idleTimer++;
        }

        // Slowly regenerate energy
        if (state.energy < 1.0 && state.currentMode == BehaviorMode.RESTING) {
            state.energy = Math.min(1.0, state.energy + 0.001);
        }

        // Decide on new behavior periodically
        if (state.actionTimer >= AUTONOMOUS_ACTION_INTERVAL) {
            state.actionTimer = 0;
            decideNewBehavior(world, entity, profile, state);
        }

        // Execute current behavior
        executeBehavior(world, entity, profile, state);

        // Look at nearby entities (social awareness)
        lookAtNearbyEntities(world, entity);
    }

    /**
     * Decide on a new behavior based on personality, mood, and environment.
     */
    private void decideNewBehavior(ServerWorld world, Entity entity, NPCProfile profile, AutonomousState state) {
        Random random = new Random();

        // Update mood based on personality
        double friendliness = profile.getPersonalityTrait("friendliness");
        double curiosity = profile.getPersonalityTrait("curiosity");
        double aggression = profile.getPersonalityTrait("aggression");

        // Check for nearby entities to influence behavior
        List<Entity> nearbyEntities = getNearbyEntities(world, entity, INTERACTION_RADIUS * 2);
        boolean nearbyPlayers = nearbyEntities.stream().anyMatch(e -> e instanceof ServerPlayerEntity);
        boolean nearbyNPCs = nearbyEntities.stream().anyMatch(e -> isNPC(e) && !e.getUuid().equals(entity.getUuid()));

        // Decision making based on personality and situation
        if (state.energy < 0.2) {
            state.currentMode = BehaviorMode.RESTING;
            state.lastThought = "I'm feeling tired, time to rest.";
        } else if (nearbyNPCs && friendliness > 0.5 && random.nextInt(100) < 30) {
            state.currentMode = BehaviorMode.SOCIALIZING;
            state.lastThought = "I see someone I should talk to.";
        } else if (nearbyPlayers && aggression > 0.7) {
            state.currentMode = BehaviorMode.FLEEING;
            state.lastThought = "I don't like strangers around here.";
        } else if (curiosity > 0.6 && random.nextInt(100) < 20) {
            state.currentMode = BehaviorMode.EXPLORING;
            state.targetPosition = generateWanderTarget(entity);
            state.lastThought = "I wonder what's over there...";
        } else if (state.idleTimer > 200) {
            state.currentMode = BehaviorMode.WANDERING;
            state.targetPosition = generateWanderTarget(entity);
            state.idleTimer = 0;
            state.lastThought = "Time to stretch my legs.";
        } else if (random.nextInt(100) < 10) {
            // Random chance to change goals
            state.randomizeGoals();
        }

        // Update profile with new thought
        if (!state.lastThought.isEmpty()) {
            profile.setStatus(NPCProfile.NPCStatus.THINKING);
        }
    }

    /**
     * Execute the current behavior.
     */
    private void executeBehavior(ServerWorld world, Entity entity, NPCProfile profile, AutonomousState state) {
        if (!(entity instanceof MobEntity mob)) {
            return;
        }

        switch (state.currentMode) {
            case WANDERING, EXPLORING -> {
                if (state.targetPosition == null) {
                    state.targetPosition = generateWanderTarget(entity);
                }
                moveTowards(mob, state.targetPosition, profile);
            }
            case FOLLOWING -> {
                if (state.targetEntity != null) {
                    Entity target = world.getEntity(state.targetEntity);
                    if (target != null) {
                        follow(mob, target);
                    } else {
                        state.targetEntity = null;
                        state.currentMode = BehaviorMode.IDLE;
                    }
                }
            }
            case SOCIALIZING -> {
                // Find nearby NPCs to socialize with
                Entity nearbyNPC = findNearestNPC(world, entity, INTERACTION_RADIUS);
                if (nearbyNPC != null && Math.random() < 0.02) { // Small chance per tick
                    initiateNPCConversation(world, entity, nearbyNPC, profile, state);
                }
            }
            case FLEEING -> {
                Vec3d awayFrom = getNearestPlayerPosition(world, entity);
                if (awayFrom != null) {
                    moveAwayFrom(mob, awayFrom);
                } else {
                    state.currentMode = BehaviorMode.IDLE;
                }
            }
            case RESTING, IDLE, CONVERSING, WORKING -> {
                // Stay still, occasionally look around
                if (Math.random() < 0.05) {
                    Entity target = world.getClosestPlayer(mob, 16.0);
                    if (target != null) {
                        mob.getLookControl().lookAt(target);
                    }
                }
            }
        }

        // Slowly decrease energy when moving
        if (isMoving(mob) && state.currentMode != BehaviorMode.RESTING) {
            state.energy = Math.max(0.0, state.energy - 0.0001);
        }
    }

    /**
     * Generate a random wandering target position.
     */
    private Vec3d generateWanderTarget(Entity entity) {
        Random random = new Random();
        double angle = random.nextDouble() * Math.PI * 2;
        double distance = 5.0 + random.nextDouble() * 15.0;

        double x = entity.getX() + Math.cos(angle) * distance;
        double z = entity.getZ() + Math.sin(angle) * distance;
        double y = entity.getY();

        return new Vec3d(x, y, z);
    }

    /**
     * Move the NPC towards a target position.
     */
    private void moveTowards(MobEntity mob, Vec3d target, NPCProfile profile) {
        double dx = target.x - mob.getX();
        double dz = target.z - mob.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance < 1.5) {
            // Arrived at target
            AutonomousState state = npcStates.get(mob.getUuid());
            if (state != null) {
                state.targetPosition = null;
                state.currentMode = BehaviorMode.IDLE;
            }
            return;
        }

        // Set movement - use profile curiosity for speed variation
        double curiosity = profile.getPersonalityTrait("curiosity");
        double speed = MOVEMENT_SPEED * (1.0 + curiosity * 0.3);
        mob.getNavigation().startMovingTo(target.x, target.y, target.z, speed);
    }

    /**
     * Follow another entity.
     */
    private void follow(MobEntity mob, Entity target) {
        double distance = mob.squaredDistanceTo(target);
        if (distance > 36.0) { // More than 6 blocks away
            double speed = MOVEMENT_SPEED * 1.2;
            mob.getNavigation().startMovingTo(target, speed);
        }
        mob.getLookControl().lookAt(target);
    }

    /**
     * Move away from a position.
     */
    private void moveAwayFrom(MobEntity mob, Vec3d from) {
        double dx = mob.getX() - from.x;
        double dz = mob.getZ() - from.z;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 16.0) {
            AutonomousState state = npcStates.get(mob.getUuid());
            if (state != null) {
                state.currentMode = BehaviorMode.IDLE;
            }
            return;
        }

        // Move in opposite direction
        double speed = MOVEMENT_SPEED * 1.5;
        Vec3d target = new Vec3d(
                mob.getX() + dx / distance * 10,
                mob.getY(),
                mob.getZ() + dz / distance * 10
        );
        mob.getNavigation().startMovingTo(target.x, target.y, target.z, speed);
    }

    /**
     * Check if the mob is currently moving.
     */
    private boolean isMoving(MobEntity mob) {
        return mob.getVelocity().horizontalLength() > 0.01;
    }

    /**
     * Find the nearest NPC within range.
     */
    private Entity findNearestNPC(ServerWorld world, Entity entity, double radius) {
        Entity nearest = null;
        double nearestDist = radius * radius;

        for (Entity other : world.iterateEntities()) {
            if (isNPC(other) && !other.getUuid().equals(entity.getUuid())) {
                double dist = entity.squaredDistanceTo(other);
                if (dist < nearestDist) {
                    nearest = other;
                    nearestDist = dist;
                }
            }
        }

        return nearest;
    }

    /**
     * Get position of nearest player.
     */
    private Vec3d getNearestPlayerPosition(ServerWorld world, Entity entity) {
        Entity player = world.getClosestPlayer(entity, 16.0);
        return player != null ? player.getPos() : null;
    }

    /**
     * Get all nearby entities.
     */
    private List<Entity> getNearbyEntities(ServerWorld world, Entity entity, double radius) {
        List<Entity> nearby = new ArrayList<>();
        double radiusSq = radius * radius;

        for (Entity other : world.iterateEntities()) {
            if (!other.getUuid().equals(entity.getUuid()) &&
                entity.squaredDistanceTo(other) < radiusSq) {
                nearby.add(other);
            }
        }

        return nearby;
    }

    /**
     * Check if an entity is an NPC.
     */
    private boolean isNPC(Entity entity) {
        if (entity == null) return false;
        NPCProfile profile = npcRegistry.getProfile(entity.getUuid());
        return profile != null && profile.isAiEnabled();
    }

    /**
     * Look at nearby entities for social awareness.
     */
    private void lookAtNearbyEntities(ServerWorld world, Entity entity) {
        if (!(entity instanceof MobEntity mob)) return;

        // Prioritize looking at players
        Entity nearestPlayer = world.getClosestPlayer(entity, 16.0);
        if (nearestPlayer != null) {
            mob.getLookControl().lookAt(nearestPlayer);
            return;
        }

        // Then look at other NPCs
        Entity nearestNPC = findNearestNPC(world, entity, 16.0);
        if (nearestNPC != null) {
            mob.getLookControl().lookAt(nearestNPC);
        }
    }

    /**
     * Initiate a conversation between two NPCs.
     */
    private void initiateNPCConversation(ServerWorld world, Entity speaker, Entity listener,
                                         NPCProfile speakerProfile, AutonomousState state) {
        // Mark both as conversing
        state.currentMode = BehaviorMode.CONVERSING;
        AutonomousState listenerState = npcStates.get(listener.getUuid());
        if (listenerState != null) {
            listenerState.currentMode = BehaviorMode.CONVERSING;
        }

        // Don't start if already processing
        if (activeRequests.containsKey(speaker.getUuid())) {
            return;
        }

        // Get listener profile - make effectively final
        final NPCProfile listenerProfile;
        NPCProfile tempProfile = npcRegistry.getProfile(listener.getUuid());
        if (tempProfile == null) {
            listenerProfile = npcRegistry.getOrCreateProfile(listener);
        } else {
            listenerProfile = tempProfile;
        }

        // Get social relationship context
        SocialSystem.Relationship relationship = socialSystem.getRelationship(
                speaker.getUuid(), listener.getUuid());
        String relationshipContext = socialSystem.getRelationshipDescription(
                speaker.getUuid(), listener.getUuid());

        // Generate conversation topic based on relationship
        String[] topics;
        if (relationship.getType() == SocialSystem.RelationshipType.FRIEND ||
            relationship.getType() == SocialSystem.RelationshipType.FAMILY) {
            topics = new String[]{"their day", "shared memories", "plans", "friendly gossip"};
        } else if (relationship.getType() == SocialSystem.RelationshipType.ENEMY) {
            topics = new String[]{"disagreements", "competitive matters", "defensive remarks"};
        } else {
            topics = new String[]{"the weather", "casual greetings", "nearby events", "local rumors"};
        }
        String topic = topics[new Random().nextInt(topics.length)];

        // Create conversational prompt with relationship context
        String prompt = String.format(
                "You are %s talking to %s. Your relationship: %s. " +
                "Start a brief, natural conversation about %s. Keep it to 1-2 sentences. " +
                "Stay in character based on your relationship.",
                speakerProfile.getEntityName(),
                listenerProfile.getEntityName(),
                relationshipContext,
                topic
        );

        // Get AI provider
        AIProvider provider = AIProviderFactory.createForNPC(speakerProfile, configManager.getConfig());
        if (!provider.isConfigured()) return;

        // Store active request
        CompletableFuture<String> request = provider.chatCompletion(
                PersonalityEngine.generateSystemPrompt(speakerProfile),
                prompt,
                ""
        );
        activeRequests.put(speaker.getUuid(), request);

        // Handle response
        request.thenAccept(response -> {
            activeRequests.remove(speaker.getUuid());

            // Analyze sentiment to determine interaction quality
            SocialSystem.InteractionQuality quality = analyzeConversationQuality(response);

            // Record the interaction in the social system
            socialSystem.recordInteraction(speaker.getUuid(), listener.getUuid(), quality);

            // Broadcast message to nearby players
            String message = String.format("%s says to %s: \"%s\"",
                    speakerProfile.getEntityName(),
                    listenerProfile.getEntityName(),
                    response);

            for (ServerPlayerEntity player : world.getPlayers()) {
                if (player.squaredDistanceTo(speaker) < 64.0) { // 8 blocks
                    player.sendMessage(Text.literal(message), false);
                }
            }

            // Update conversation history
            speakerProfile.addToConversationHistory("Said to " + listenerProfile.getEntityName() + ": " + response);
            listenerProfile.addToConversationHistory("Heard from " + speakerProfile.getEntityName() + ": " + response);

            // Evolve personalities
            PersonalityEngine.evolve(speakerProfile, "talking to " + listenerProfile.getEntityName(), response);

            // Save profiles
            npcRegistry.register(speakerProfile);
            npcRegistry.register(listenerProfile);

            // Reset states after conversation
            state.currentMode = BehaviorMode.IDLE;
            if (listenerState != null) {
                listenerState.currentMode = BehaviorMode.IDLE;
            }

            // Voice output (optional)
            if (speakerProfile.isVoiceEnabled() && configManager.getConfig().getVoice().isEnabled()) {
                voiceIntegration.speak(speaker, response);
            }
        }).exceptionally(ex -> {
            activeRequests.remove(speaker.getUuid());
            LOGGER.error("[Easy NPC kracked AI] NPC-to-NPC conversation failed", ex);
            state.currentMode = BehaviorMode.IDLE;
            return null;
        });
    }

    /**
     * Analyze conversation quality based on response content.
     */
    private SocialSystem.InteractionQuality analyzeConversationQuality(String response) {
        String lower = response.toLowerCase();

        // Positive indicators
        if (lower.contains("good") || lower.contains("great") || lower.contains("love") ||
            lower.contains("friend") || lower.contains("happy") || lower.contains("thank") ||
            lower.contains("pleasure") || lower.contains("wonderful")) {
            return SocialSystem.InteractionQuality.POSITIVE;
        }

        // Negative indicators
        if (lower.contains("hate") || lower.contains("stupid") || lower.contains("annoying") ||
            lower.contains("go away") || lower.contains("leave") || lower.contains("terrible")) {
            return SocialSystem.InteractionQuality.NEGATIVE;
        }

        // Neutral by default
        return SocialSystem.InteractionQuality.NEUTRAL;
    }

    /**
     * Called when an NPC is unloaded.
     */
    public void onNPCUnloaded(Entity entity) {
        CompletableFuture<String> request = activeRequests.remove(entity.getUuid());
        if (request != null) {
            request.cancel(true);
        }
        npcStates.remove(entity.getUuid());
    }

    /**
     * Get the autonomous state for an NPC.
     */
    public AutonomousState getState(UUID uuid) {
        return npcStates.get(uuid);
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
