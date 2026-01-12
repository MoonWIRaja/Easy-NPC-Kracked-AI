package com.ainpcconnector.behavior;

import com.ainpcconnector.npc.NPCProfile;

import java.util.Map;

/**
 * Engine for evolving NPC personality over time based on interactions.
 */
public class PersonalityEngine {

    /**
     * Evolve an NPC's personality based on a conversation.
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

        // Update the timestamp
        profile.setLastPersonalityUpdate(System.currentTimeMillis());
    }

    /**
     * Generate a dynamic system prompt based on personality traits.
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
}
