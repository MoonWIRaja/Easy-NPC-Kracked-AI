package com.ainpcconnector.behavior;

import com.ainpcconnector.npc.NPCProfile;
import com.ainpcconnector.npc.NPCRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Social system for tracking relationships between NPCs.
 * NPCs will form opinions about each other based on interactions.
 */
public class SocialSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocialSystem.class);
    private static SocialSystem instance;

    private final NPCRegistry npcRegistry;
    // Maps NPC UUID -> (Target NPC UUID -> Relationship)
    private final Map<UUID, Map<UUID, Relationship>> relationships = new ConcurrentHashMap<>();

    /**
     * Represents a relationship between two NPCs.
     */
    public static class Relationship {
        private UUID npcId;
        private UUID targetId;
        private double affection;     // -1.0 (hate) to 1.0 (love)
        private double respect;       // 0.0 (none) to 1.0 (high respect)
        private double trust;         // 0.0 (none) to 1.0 (complete trust)
        private int interactionCount;
        private long lastInteractionTime;
        private RelationshipType type;

        public Relationship(UUID npcId, UUID targetId) {
            this.npcId = npcId;
            this.targetId = targetId;
            this.affection = 0.0;
            this.respect = 0.5;
            this.trust = 0.3;
            this.interactionCount = 0;
            this.lastInteractionTime = System.currentTimeMillis();
            this.type = RelationshipType.NEUTRAL;
        }

        public UUID getNpcId() { return npcId; }
        public UUID getTargetId() { return targetId; }
        public double getAffection() { return affection; }
        public double getRespect() { return respect; }
        public double getTrust() { return trust; }
        public int getInteractionCount() { return interactionCount; }
        public long getLastInteractionTime() { return lastInteractionTime; }
        public RelationshipType getType() { return type; }

        public void setAffection(double value) { this.affection = clamp(value); }
        public void setRespect(double value) { this.respect = clamp(value); }
        public void setTrust(double value) { this.trust = clamp(value); }
        public void incrementInteractions() { this.interactionCount++; }
        public void updateInteractionTime() { this.lastInteractionTime = System.currentTimeMillis(); }
        public void setType(RelationshipType type) { this.type = type; }

        private double clamp(double value) {
            return Math.max(-1.0, Math.min(1.0, value));
        }

        /**
         * Get the overall relationship score.
         */
        public double getOverallScore() {
            return (affection * 0.5) + (respect * 0.3) + (trust * 0.2);
        }

        /**
         * Get a description of the relationship.
         */
        public String getDescription() {
            if (type == RelationshipType.FAMILY) return "family";
            if (type == RelationshipType.FRIEND) return "close friend";
            if (type == RelationshipType.ENEMY) return "enemy";
            if (type == RelationshipType.RIVAL) return "rival";

            double score = getOverallScore();
            if (score > 0.7) return "very close";
            if (score > 0.4) return "friendly";
            if (score > 0.0) return "acquaintance";
            if (score > -0.3) return "distant";
            if (score > -0.6) return "dislike";
            return "hates";
        }
    }

    /**
     * Quality of an interaction.
     */
    public enum InteractionQuality {
        VERY_POSITIVE, // Very good interaction
        POSITIVE,      // Good interaction
        NEUTRAL,       // Neutral interaction
        NEGATIVE,      // Bad interaction
        VERY_NEGATIVE  // Very bad interaction
    }

    /**
     * Types of predefined relationships.
     */
    public enum RelationshipType {
        NEUTRAL,     // No special relationship
        FRIEND,      // Close friendship
        FAMILY,      // Family bond
        ENEMY,       // Active dislike/hostility
        RIVAL,       // Competitive relationship
        MENTOR,      // One teaches the other
        STUDENT      // One learns from the other
    }

    private SocialSystem() {
        this.npcRegistry = NPCRegistry.getInstance();
    }

    public static SocialSystem getInstance() {
        if (instance == null) {
            instance = new SocialSystem();
        }
        return instance;
    }

    /**
     * Get or create a relationship between two NPCs.
     */
    public Relationship getRelationship(UUID npcId, UUID targetId) {
        return relationships
                .computeIfAbsent(npcId, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(targetId, k -> new Relationship(npcId, targetId));
    }

    /**
     * Record an interaction between two NPCs.
     * This will update their relationship based on the interaction quality.
     */
    public void recordInteraction(UUID npcId, UUID targetId, InteractionQuality quality) {
        Relationship fromNpc = getRelationship(npcId, targetId);
        Relationship fromTarget = getRelationship(targetId, npcId);

        fromNpc.incrementInteractions();
        fromNpc.updateInteractionTime();
        fromTarget.incrementInteractions();
        fromTarget.updateInteractionTime();

        // Update relationship based on interaction quality
        double change = switch (quality) {
            case VERY_POSITIVE -> 0.1;
            case POSITIVE -> 0.05;
            case NEUTRAL -> 0.01;
            case NEGATIVE -> -0.05;
            case VERY_NEGATIVE -> -0.1;
        };

        fromNpc.setAffection(fromNpc.getAffection() + change);
        fromNpc.setTrust(fromNpc.getTrust() + change * 0.5);

        // Update relationship type based on new values
        updateRelationshipType(fromNpc);
        updateRelationshipType(fromTarget);

        LOGGER.debug("[Easy NPC kracked AI] Interaction recorded: {} -> {} (quality: {}, new affection: {})",
                npcId, targetId, quality, fromNpc.getAffection());
    }

    /**
     * Update the relationship type based on current values.
     */
    private void updateRelationshipType(Relationship rel) {
        if (rel.getType() == RelationshipType.FAMILY || rel.getType() == RelationshipType.MENTOR) {
            // Don't change special relationship types
            return;
        }

        double score = rel.getOverallScore();
        if (score > 0.6) {
            rel.setType(RelationshipType.FRIEND);
        } else if (score < -0.5) {
            rel.setType(RelationshipType.ENEMY);
        } else {
            rel.setType(RelationshipType.NEUTRAL);
        }
    }

    /**
     * Set a specific relationship type between two NPCs.
     */
    public void setRelationshipType(UUID npcId, UUID targetId, RelationshipType type) {
        Relationship rel = getRelationship(npcId, targetId);
        rel.setType(type);

        // Adjust values based on type
        switch (type) {
            case FRIEND -> {
                rel.setAffection(0.8);
                rel.setTrust(0.7);
            }
            case FAMILY -> {
                rel.setAffection(0.9);
                rel.setTrust(0.8);
            }
            case ENEMY -> {
                rel.setAffection(-0.7);
                rel.setTrust(0.1);
            }
            case RIVAL -> {
                rel.setAffection(-0.3);
                rel.setRespect(0.7);
            }
            case MENTOR -> {
                rel.setRespect(0.9);
                rel.setTrust(0.8);
            }
            case STUDENT -> {
                rel.setAffection(0.5);
                rel.setTrust(0.4);
            }
            case NEUTRAL -> {
                // No change
            }
        }
    }

    /**
     * Get all NPCs that have a relationship with the given NPC.
     */
    public List<UUID> getKnownNPCs(UUID npcId) {
        Map<UUID, Relationship> npcRelations = relationships.get(npcId);
        if (npcRelations == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(npcRelations.keySet());
    }

    /**
     * Get NPCs that the given NPC considers friends.
     */
    public List<UUID> getFriends(UUID npcId) {
        List<UUID> friends = new ArrayList<>();
        Map<UUID, Relationship> npcRelations = relationships.get(npcId);
        if (npcRelations != null) {
            for (Map.Entry<UUID, Relationship> entry : npcRelations.entrySet()) {
                if (entry.getValue().getOverallScore() > 0.4) {
                    friends.add(entry.getKey());
                }
            }
        }
        return friends;
    }

    /**
     * Get NPCs that the given NPC considers enemies.
     */
    public List<UUID> getEnemies(UUID npcId) {
        List<UUID> enemies = new ArrayList<>();
        Map<UUID, Relationship> npcRelations = relationships.get(npcId);
        if (npcRelations != null) {
            for (Map.Entry<UUID, Relationship> entry : npcRelations.entrySet()) {
                if (entry.getValue().getOverallScore() < -0.3) {
                    enemies.add(entry.getKey());
                }
            }
        }
        return enemies;
    }

    /**
     * Get a description of how an NPC feels about another.
     */
    public String getRelationshipDescription(UUID npcId, UUID targetId) {
        Relationship rel = getRelationship(npcId, targetId);
        NPCProfile targetProfile = npcRegistry.getProfile(targetId);
        String targetName = targetProfile != null ? targetProfile.getEntityName() : "Unknown";

        if (rel.getType() == RelationshipType.FRIEND) {
            return String.format("%s is a good friend of mine.", targetName);
        } else if (rel.getType() == RelationshipType.FAMILY) {
            return String.format("%s is like family to me.", targetName);
        } else if (rel.getType() == RelationshipType.ENEMY) {
            return String.format("I don't get along with %s.", targetName);
        }

        double score = rel.getOverallScore();
        if (score > 0.5) {
            return String.format("I like %s quite a bit.", targetName);
        } else if (score > 0.0) {
            return String.format("%s seems alright.", targetName);
        } else if (score > -0.3) {
            return String.format("I don't know %s very well.", targetName);
        } else {
            return String.format("I'm not fond of %s.", targetName);
        }
    }

    /**
     * Decay relationships over time (relationships fade if not maintained).
     */
    public void decayRelationships() {
        long now = System.currentTimeMillis();
        long dayMs = 24 * 60 * 60 * 1000;

        for (Map<UUID, Relationship> npcRelations : relationships.values()) {
            for (Relationship rel : npcRelations.values()) {
                long timeSinceInteraction = now - rel.getLastInteractionTime();

                // Decay relationship if no interaction for 3+ days
                if (timeSinceInteraction > 3 * dayMs) {
                    // Neutral drift toward 0
                    if (rel.getAffection() > 0) {
                        rel.setAffection(rel.getAffection() - 0.001);
                    } else if (rel.getAffection() < 0) {
                        rel.setAffection(rel.getAffection() + 0.001);
                    }

                    // Trust decays faster
                    if (rel.getTrust() > 0.3) {
                        rel.setTrust(rel.getTrust() - 0.002);
                    }
                }
            }
        }
    }

    /**
     * Clear all relationships (for testing/debugging).
     */
    public void clearAll() {
        relationships.clear();
    }

    /**
     * Remove relationships for a specific NPC.
     */
    public void removeNPC(UUID npcId) {
        relationships.remove(npcId);
        // Remove from other NPCs' relationship maps
        for (Map<UUID, Relationship> npcRelations : relationships.values()) {
            npcRelations.remove(npcId);
        }
    }
}
