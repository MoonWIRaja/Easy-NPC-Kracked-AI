package com.ainpcconnector.behavior;

import com.ainpcconnector.npc.NPCProfile;

import java.util.*;

/**
 * Enhanced engine for evolving NPC personality over time based on interactions.
 * Features dynamic mood tracking, memory system, and personality drift.
 */
public class PersonalityEngine {

    /**
     * Memory types for different kinds of memories.
     */
    public enum MemoryType {
        POSITIVE_INTERACTION,    // Good conversations
        NEGATIVE_INTERACTION,    // Bad conversations
        ACHIEVEMENT,             // Accomplishments
        CONFLICT,                // Arguments/fights
        HELP_RECEIVED,           // When someone helped the NPC
        HELP_GIVEN,              // When the NPC helped someone
        DISCOVERY,               // New discoveries
        EMOTIONAL_EVENT          // Emotional moments
    }

    /**
     * A single memory stored by an NPC.
     */
    public static class Memory {
        private final UUID relatedEntity;  // Who this memory involves
        private final MemoryType type;      // Type of memory
        private final String description;   // What happened
        private final long timestamp;       // When it happened
        private double emotionalImpact;     // How strong the emotion was (0-1)
        private int accessCount;           // How often this memory is recalled

        public Memory(UUID relatedEntity, MemoryType type, String description, double emotionalImpact) {
            this.relatedEntity = relatedEntity;
            this.type = type;
            this.description = description;
            this.emotionalImpact = emotionalImpact;
            this.timestamp = System.currentTimeMillis();
            this.accessCount = 0;
        }

        public UUID getRelatedEntity() { return relatedEntity; }
        public MemoryType getType() { return type; }
        public String getDescription() { return description; }
        public long getTimestamp() { return timestamp; }
        public double getEmotionalImpact() { return emotionalImpact; }
        public int getAccessCount() { return accessCount; }
        public void accessed() { accessCount++; }

        /**
         * Calculate memory strength (fades over time unless frequently accessed).
         */
        public double getStrength() {
            long ageMs = System.currentTimeMillis() - timestamp;
            long days = ageMs / (24 * 60 * 60 * 1000);

            // Base decay - memories fade after 30 days
            double timeDecay = Math.max(0, 1.0 - (days / 30.0));

            // Access boost - frequently accessed memories persist longer
            double accessBoost = Math.min(1.0, accessCount * 0.1);

            return Math.min(1.0, timeDecay + accessBoost) * emotionalImpact;
        }
    }

    /**
     * Mood state for an NPC.
     */
    public static class MoodState {
        private double happiness;      // 0-1, general happiness
        private double sadness;        // 0-1, feeling down
        private double anger;          // 0-1, feeling angry
        private double fear;           // 0-1, feeling afraid
        private double excitement;     // 0-1, feeling excited
        private long lastUpdate;

        public MoodState() {
            this.happiness = 0.5;
            this.sadness = 0.1;
            this.anger = 0.1;
            this.fear = 0.1;
            this.excitement = 0.3;
            this.lastUpdate = System.currentTimeMillis();
        }

        public double getHappiness() { return happiness; }
        public double getSadness() { return sadness; }
        public double getAnger() { return anger; }
        public double getFear() { return fear; }
        public double getExcitement() { return excitement; }

        public void adjustHappiness(double delta) {
            happiness = clamp(happiness + delta);
            if (delta > 0) { sadness = clamp(sadness - delta * 0.5); }
        }
        public void adjustSadness(double delta) {
            sadness = clamp(sadness + delta);
            if (delta > 0) { happiness = clamp(happiness - delta * 0.5); }
        }
        public void adjustAnger(double delta) { anger = clamp(anger + delta); }
        public void adjustFear(double delta) { fear = clamp(fear + delta); }
        public void adjustExcitement(double delta) { excitement = clamp(excitement + delta); }

        /**
         * Get the dominant emotion.
         */
        public String getDominantEmotion() {
            double max = 0;
            String emotion = "neutral";

            if (happiness > max) { max = happiness; emotion = "happy"; }
            if (sadness > max) { max = sadness; emotion = "sad"; }
            if (anger > max) { max = anger; emotion = "angry"; }
            if (fear > max) { max = fear; emotion = "afraid"; }
            if (excitement > max) { max = excitement; emotion = "excited"; }

            return max > 0.4 ? emotion : "neutral";
        }

        /**
         * Natural mood decay over time - emotions return to baseline.
         */
        public void decay() {
            long timeSinceUpdate = System.currentTimeMillis() - lastUpdate;
            if (timeSinceUpdate < 60000) return; // Only decay every minute

            // Return to baseline slowly
            final double BASELINE_HAPPINESS = 0.5;
            final double BASELINE_NEGATIVE = 0.1;
            final double BASELINE_EXCITEMENT = 0.2;

            happiness = moveTowards(happiness, BASELINE_HAPPINESS, 0.01);
            sadness = moveTowards(sadness, BASELINE_NEGATIVE, 0.02);
            anger = moveTowards(anger, BASELINE_NEGATIVE, 0.02);
            fear = moveTowards(fear, BASELINE_NEGATIVE, 0.02);
            excitement = moveTowards(excitement, BASELINE_EXCITEMENT, 0.03);

            lastUpdate = System.currentTimeMillis();
        }

        private double moveTowards(double current, double target, double amount) {
            if (current < target) return Math.min(target, current + amount);
            return Math.max(target, current - amount);
        }

        private double clamp(double value) {
            return Math.max(0.0, Math.min(1.0, value));
        }
    }

    /**
     * Evolve an NPC's personality based on a conversation.
     * Enhanced version with mood tracking and memory.
     */
    public static void evolve(NPCProfile profile, String playerMessage, String aiResponse) {
        // Increment interaction count
        profile.incrementInteractionCount();

        // Analyze the interaction for personality traits
        Map<String, Double> traits = profile.getPersonalityTraits();

        // Analyze sentiment from AI response
        double sentiment = analyzeSentiment(aiResponse);

        // Update traits based on sentiment and interaction
        double evolutionRate = 0.01; // Small changes per interaction

        // Friendliness evolves based on positive/negative interactions
        if (sentiment > 0.3) {
            traits.put("friendliness", clamp(traits.getOrDefault("friendliness", 0.5) + evolutionRate));
        } else if (sentiment < -0.3) {
            traits.put("friendliness", clamp(traits.getOrDefault("friendliness", 0.5) - evolutionRate));
        }

        // Curiosity increases with questions
        if (playerMessage.contains("?") || playerMessage.toLowerCase().contains("what") ||
                playerMessage.toLowerCase().contains("how") || playerMessage.toLowerCase().contains("why")) {
            traits.put("curiosity", clamp(traits.getOrDefault("curiosity", 0.5) + evolutionRate));
        }

        // Humor increases with jokes or laughter
        if (aiResponse.toLowerCase().contains("haha") || aiResponse.toLowerCase().contains("lol") ||
                aiResponse.toLowerCase().contains("joke")) {
            traits.put("humor", clamp(traits.getOrDefault("humor", 0.5) + evolutionRate));
        }

        // Aggression increases with hostile responses
        if (aiResponse.toLowerCase().contains("!") || aiResponse.toLowerCase().contains("leave") ||
                aiResponse.toLowerCase().contains("go away")) {
            traits.put("aggression", clamp(traits.getOrDefault("aggression", 0.1) + evolutionRate * 0.5));
        }

        // Personality drift - small random changes to simulate natural personality development
        if (Math.random() < 0.1) {
            String[] traitNames = {"friendliness", "curiosity", "aggression", "humor"};
            String randomTrait = traitNames[new Random().nextInt(traitNames.length)];
            double drift = (Math.random() - 0.5) * 0.02;
            traits.put(randomTrait, clamp(traits.getOrDefault(randomTrait, 0.5) + drift));
        }

        // Update the timestamp
        profile.setLastPersonalityUpdate(System.currentTimeMillis());
    }

    /**
     * Generate a dynamic system prompt based on personality traits and current state.
     * Enhanced with mood and memory context.
     */
    public static String generateSystemPrompt(NPCProfile profile) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are ").append(profile.getEntityName()).append(", an NPC in a Minecraft world.\n\n");

        prompt.append("Your personality: ").append(profile.getPersonality()).append("\n\n");

        prompt.append("Personality Traits (scale 0-1):\n");
        for (Map.Entry<String, Double> trait : profile.getPersonalityTraits().entrySet()) {
            prompt.append("- ").append(trait.getKey()).append(": ").append(String.format("%.2f", trait.getValue())).append("\n");
        }

        prompt.append("\nGuidelines for behavior:\n");
        prompt.append("- Stay in character as defined by your personality\n");
        prompt.append("- Respond naturally and conversationally\n");
        prompt.append("- Keep responses concise (1-2 sentences usually)\n");
        prompt.append("- Your personality should subtly influence your responses\n");
        prompt.append("- You are in a Minecraft world, so references to blocks, mobs, and items are natural\n");

        // Add personality-specific instructions
        double friendliness = profile.getPersonalityTrait("friendliness");
        if (friendliness > 0.7) {
            prompt.append("- You are very friendly and eager to help players\n");
        } else if (friendliness < 0.3) {
            prompt.append("- You are somewhat reserved and cautious with strangers\n");
        }

        double humor = profile.getPersonalityTrait("humor");
        if (humor > 0.6) {
            prompt.append("- You enjoy making jokes and being playful\n");
        }

        return prompt.toString();
    }

    /**
     * Simple sentiment analysis (positive = positive sentiment, negative = negative).
     */
    private static double analyzeSentiment(String text) {
        if (text == null || text.isEmpty()) {
            return 0.0;
        }

        String lower = text.toLowerCase();

        // Positive words
        int positiveCount = 0;
        for (String word : new String[]{"good", "great", "happy", "love", "thanks", "awesome", "cool", "yes", "sure"}) {
            if (lower.contains(word)) positiveCount++;
        }

        // Negative words
        int negativeCount = 0;
        for (String word : new String[]{"bad", "hate", "no", "angry", "leave", "go away", "stop"}) {
            if (lower.contains(word)) negativeCount++;
        }

        // Calculate sentiment (-1 to 1)
        int total = positiveCount + negativeCount;
        if (total == 0) {
            return 0.0;
        }

        return (positiveCount - negativeCount) / (double) total;
    }

    /**
     * Clamp a value between 0 and 1.
     */
    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    /**
     * Get a description of the NPC's personality.
     */
    public static String getPersonalityDescription(NPCProfile profile) {
        StringBuilder desc = new StringBuilder();

        double friendliness = profile.getPersonalityTrait("friendliness");
        if (friendliness > 0.8) {
            desc.append("Very friendly and welcoming. ");
        } else if (friendliness > 0.5) {
            desc.append("Generally friendly. ");
        } else if (friendliness > 0.3) {
            desc.append("Somewhat reserved. ");
        } else {
            desc.append("Quite distant and cautious. ");
        }

        double curiosity = profile.getPersonalityTrait("curiosity");
        if (curiosity > 0.7) {
            desc.append("Very curious about new things. ");
        } else if (curiosity < 0.3) {
            desc.append("Not particularly curious. ");
        }

        double humor = profile.getPersonalityTrait("humor");
        if (humor > 0.6) {
            desc.append("Enjoys humor and jokes.");
        } else if (humor < 0.3) {
            desc.append("Takes things seriously.");
        }

        return desc.toString().trim();
    }

    /**
     * Generate a response that reflects the NPC's current mood.
     * This can be prepended to AI responses to add emotional context.
     */
    public static String getMoodPrefix(NPCProfile profile) {
        // This is a placeholder - actual mood tracking would need to be stored in NPCProfile
        double friendliness = profile.getPersonalityTrait("friendliness");
        double humor = profile.getPersonalityTrait("humor");

        if (friendliness > 0.7) {
            return "*smiles warmly* ";
        } else if (friendliness < 0.3) {
            return "*looks cautious* ";
        }

        if (humor > 0.6 && Math.random() < 0.3) {
            return "*chuckles* ";
        }

        return "";
    }

    /**
     * Check if an NPC should respond based on their personality.
     * Some NPCs might be more talkative than others.
     */
    public static boolean shouldRespond(NPCProfile profile) {
        double friendliness = profile.getPersonalityTrait("friendliness");
        double curiosity = profile.getPersonalityTrait("curiosity");

        // Friendly and curious NPCs are more likely to respond
        double responseChance = 0.5 + (friendliness * 0.3) + (curiosity * 0.2);
        return Math.random() < responseChance;
    }

    /**
     * Get an action hint based on personality for autonomous behavior.
     */
    public static String getPersonalityActionHint(NPCProfile profile) {
        double curiosity = profile.getPersonalityTrait("curiosity");
        double friendliness = profile.getPersonalityTrait("friendliness");
        double aggression = profile.getPersonalityTrait("aggression");

        if (curiosity > 0.7) {
            return "You feel drawn to explore new places.";
        }
        if (friendliness > 0.7) {
            return "You want to meet new people.";
        }
        if (aggression > 0.7) {
            return "You feel protective of your territory.";
        }
        if (friendliness < 0.3) {
            return "You prefer to keep to yourself.";
        }

        return "You're content with where you are.";
    }
}
