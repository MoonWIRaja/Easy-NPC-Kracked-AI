package com.ainpcconnector.npc;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Profile data for an AI-controlled NPC.
 */
public class NPCProfile {

    private final UUID entityUuid;
    private String entityName;
    private String entityType;

    // AI Configuration
    private String aiProviderId = null; // ID of the AI provider to use (null = use default)
    private String systemPrompt = "You are a friendly NPC in a Minecraft world. Stay in character and respond naturally.";
    private String personality = "Friendly and helpful";

    // Personality Evolution
    private int interactionCount = 0;
    private Map<String, Double> personalityTraits = new HashMap<>();
    private long lastPersonalityUpdate = 0;

    // Status
    private boolean aiEnabled = false;
    private boolean voiceEnabled = false;
    private NPCStatus status = NPCStatus.IDLE;

    // Position tracking
    private Vec3d lastKnownPosition;
    private BlockPos homePosition;
    private String worldId;

    // Conversation history (last 10 messages)
    private String conversationHistory = "";

    public enum NPCStatus {
        IDLE, CONVERSING, MOVING, THINKING
    }

    public NPCProfile(UUID entityUuid, String entityName, String entityType) {
        this.entityUuid = entityUuid;
        this.entityName = entityName;
        this.entityType = entityType;

        // Initialize default personality traits
        personalityTraits.put("friendliness", 0.7);
        personalityTraits.put("curiosity", 0.5);
        personalityTraits.put("aggression", 0.1);
        personalityTraits.put("humor", 0.3);
    }

    public NPCProfile(Entity entity) {
        this(entity.getUuid(),
                entity.getName().getString(),
                entity.getType().toString());
        this.lastKnownPosition = entity.getPos();
        this.homePosition = entity.getBlockPos();
    }

    // Getters and Setters

    public UUID getEntityUuid() {
        return entityUuid;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getAiProviderId() {
        return aiProviderId;
    }

    public void setAiProviderId(String aiProviderId) {
        this.aiProviderId = aiProviderId;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public int getInteractionCount() {
        return interactionCount;
    }

    public void incrementInteractionCount() {
        this.interactionCount++;
    }

    public Map<String, Double> getPersonalityTraits() {
        return personalityTraits;
    }

    public void setPersonalityTrait(String trait, double value) {
        personalityTraits.put(trait, Math.max(0.0, Math.min(1.0, value)));
    }

    public double getPersonalityTrait(String trait) {
        return personalityTraits.getOrDefault(trait, 0.5);
    }

    public boolean isAiEnabled() {
        return aiEnabled;
    }

    public void setAiEnabled(boolean aiEnabled) {
        this.aiEnabled = aiEnabled;
    }

    public boolean isVoiceEnabled() {
        return voiceEnabled;
    }

    public void setVoiceEnabled(boolean voiceEnabled) {
        this.voiceEnabled = voiceEnabled;
    }

    public NPCStatus getStatus() {
        return status;
    }

    public void setStatus(NPCStatus status) {
        this.status = status;
    }

    public Vec3d getLastKnownPosition() {
        return lastKnownPosition;
    }

    public void setLastKnownPosition(Vec3d lastKnownPosition) {
        this.lastKnownPosition = lastKnownPosition;
    }

    public BlockPos getHomePosition() {
        return homePosition;
    }

    public void setHomePosition(BlockPos homePosition) {
        this.homePosition = homePosition;
    }

    public String getWorldId() {
        return worldId;
    }

    public void setWorldId(String worldId) {
        this.worldId = worldId;
    }

    public String getConversationHistory() {
        return conversationHistory;
    }

    public void addToConversationHistory(String message) {
        if (conversationHistory.isEmpty()) {
            conversationHistory = message;
        } else {
            conversationHistory += "\n" + message;
        }

        // Keep only last 1000 characters
        if (conversationHistory.length() > 1000) {
            conversationHistory = conversationHistory.substring(conversationHistory.length() - 1000);
        }
    }

    public void clearConversationHistory() {
        conversationHistory = "";
    }

    public long getLastPersonalityUpdate() {
        return lastPersonalityUpdate;
    }

    public void setLastPersonalityUpdate(long lastPersonalityUpdate) {
        this.lastPersonalityUpdate = lastPersonalityUpdate;
    }
}
