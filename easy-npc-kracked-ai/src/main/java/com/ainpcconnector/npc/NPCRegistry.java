package com.ainpcconnector.npc;

import com.ainpcconnector.AINpcConnectorMod;
import com.ainpcconnector.config.DatabaseManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Persistent registry for NPC profiles using SQLite.
 * Stores all NPC configurations to database.
 */
public class NPCRegistry {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(UUID.class, new UUIDAdapter())
            .create();

    private final Map<UUID, NPCProfile> profiles = new ConcurrentHashMap<>();
    private DatabaseManager database;

    private static NPCRegistry instance;

    private NPCRegistry() {
        // Try to get database
        try {
            database = AINpcConnectorMod.getConfigManager().getDatabase();
            loadFromDatabase();
        } catch (Exception e) {
            AINpcConnectorMod.LOGGER.warn("[AI NPC Connector] Database not available for NPC registry");
        }
    }

    public static NPCRegistry getInstance() {
        if (instance == null) {
            instance = new NPCRegistry();
        }
        return instance;
    }

    /**
     * Set database manager (for dependency injection).
     */
    public void setDatabase(DatabaseManager database) {
        this.database = database;
        loadFromDatabase();
    }

    /**
     * Load NPC profiles from database.
     */
    public void load() {
        loadFromDatabase();
    }

    private void loadFromDatabase() {
        if (database == null) return;

        List<DatabaseManager.NPCProfileRecord> records = database.getAllNPCProfiles();
        for (DatabaseManager.NPCProfileRecord record : records) {
            NPCProfile profile = convertToProfile(record);
            profiles.put(profile.getEntityUuid(), profile);
        }
        AINpcConnectorMod.LOGGER.info("[AI NPC Connector] Loaded {} NPC profiles from database", profiles.size());
    }

    private NPCProfile convertToProfile(DatabaseManager.NPCProfileRecord record) {
        // Create profile with required constructor parameters
        NPCProfile profile = new NPCProfile(
            record.uuid(),
            record.entityName(),
            "easy_npc" // Default entity type
        );
        profile.setAiEnabled(record.aiEnabled());
        profile.setVoiceEnabled(record.voiceEnabled());
        profile.setAiProviderId(record.aiProviderId());
        profile.setPersonality(record.personality());
        profile.setSystemPrompt(record.systemPrompt());

        // Parse personality traits JSON
        if (record.personalityTraits() != null && !record.personalityTraits().isEmpty()) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Double> traits = GSON.fromJson(record.personalityTraits(), Map.class);
                if (traits != null) {
                    for (Map.Entry<String, Double> entry : traits.entrySet()) {
                        profile.setPersonalityTrait(entry.getKey(), entry.getValue());
                    }
                }
            } catch (Exception e) {
                AINpcConnectorMod.LOGGER.warn("[AI NPC Connector] Failed to parse personality traits: " + e.getMessage());
            }
        }

        // Parse conversation history JSON (stored as formatted string)
        if (record.conversationHistory() != null && !record.conversationHistory().isEmpty()) {
            try {
                @SuppressWarnings("unchecked")
                List<Map<String, String>> history = GSON.fromJson(record.conversationHistory(), List.class);
                if (history != null) {
                    StringBuilder sb = new StringBuilder();
                    for (Map<String, String> msg : history) {
                        if (sb.length() > 0) sb.append("\n");
                        sb.append(msg.get("role")).append(": ").append(msg.get("content"));
                    }
                    profile.addToConversationHistory(sb.toString());
                }
            } catch (Exception e) {
                AINpcConnectorMod.LOGGER.warn("[AI NPC Connector] Failed to parse conversation history: " + e.getMessage());
            }
        }

        return profile;
    }

    private DatabaseManager.NPCProfileRecord convertToRecord(NPCProfile profile) {
        String traitsJson = GSON.toJson(profile.getPersonalityTraits());
        // Convert conversation history string to JSON array
        List<Map<String, String>> historyList = new ArrayList<>();
        String history = profile.getConversationHistory();
        if (history != null && !history.isEmpty()) {
            String[] lines = history.split("\n");
            for (String line : lines) {
                Map<String, String> msg = new HashMap<>();
                int colonIdx = line.indexOf(": ");
                if (colonIdx > 0) {
                    msg.put("role", line.substring(0, colonIdx));
                    msg.put("content", line.substring(colonIdx + 2));
                } else {
                    msg.put("role", "user");
                    msg.put("content", line);
                }
                historyList.add(msg);
            }
        }
        String historyJson = GSON.toJson(historyList);

        return new DatabaseManager.NPCProfileRecord(
            profile.getEntityUuid(),
            profile.getEntityName(),
            profile.isAiEnabled(),
            profile.isVoiceEnabled(),
            profile.getAiProviderId(),
            profile.getPersonality(),
            profile.getSystemPrompt(),
            traitsJson,
            historyJson,
            null, // createdAt - not tracked in profile
            null  // updatedAt - will be set by database
        );
    }

    /**
     * Save NPC profiles to database.
     */
    public void save() {
        // Saving is done immediately on each operation
    }

    /**
     * Register or update an NPC profile.
     */
    public void register(NPCProfile profile) {
        profiles.put(profile.getEntityUuid(), profile);

        if (database != null) {
            database.saveNPCProfile(convertToRecord(profile));
        }
    }

    /**
     * Get an NPC profile by UUID.
     */
    public NPCProfile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }

    /**
     * Get an NPC profile by entity, creating a new one if it doesn't exist.
     */
    public NPCProfile getOrCreateProfile(net.minecraft.entity.Entity entity) {
        UUID uuid = entity.getUuid();
        NPCProfile profile = profiles.get(uuid);

        if (profile == null) {
            profile = new NPCProfile(entity);
            profiles.put(uuid, profile);

            if (database != null) {
                database.saveNPCProfile(convertToRecord(profile));
            }
        }

        return profile;
    }

    /**
     * Remove an NPC profile.
     */
    public void remove(UUID uuid) {
        profiles.remove(uuid);

        if (database != null) {
            database.deleteNPCProfile(uuid);
        }
    }

    /**
     * Get all registered profiles.
     */
    public Collection<NPCProfile> getAllProfiles() {
        return profiles.values();
    }

    /**
     * Get the number of registered profiles.
     */
    public int size() {
        return profiles.size();
    }

    /**
     * Check if a profile exists for the given UUID.
     */
    public boolean hasProfile(UUID uuid) {
        return profiles.containsKey(uuid);
    }

    /**
     * Clear all profiles.
     */
    public void clear() {
        profiles.clear();

        if (database != null) {
            // Clear all profiles from database
            for (DatabaseManager.NPCProfileRecord record : database.getAllNPCProfiles()) {
                database.deleteNPCProfile(record.uuid());
            }
        }
    }

    /**
     * Gson adapter for UUID serialization.
     */
    private static class UUIDAdapter implements com.google.gson.JsonSerializer<UUID>,
            com.google.gson.JsonDeserializer<UUID> {

        @Override
        public com.google.gson.JsonElement serialize(UUID src, java.lang.reflect.Type typeOfSrc,
                com.google.gson.JsonSerializationContext context) {
            return new com.google.gson.JsonPrimitive(src.toString());
        }

        @Override
        public UUID deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT,
                com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
            return UUID.fromString(json.getAsString());
        }
    }
}
