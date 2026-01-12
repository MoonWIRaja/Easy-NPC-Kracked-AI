package com.ainpcconnector.web.handlers;

import com.ainpcconnector.npc.NPCProfile;
import com.ainpcconnector.npc.NPCRegistry;
import io.javalin.http.Context;

import java.util.Map;
import java.util.UUID;

/**
 * Handler for NPC management endpoints.
 */
public class NPCHandler {

    private final NPCRegistry registry;

    public NPCHandler() {
        this.registry = NPCRegistry.getInstance();
    }

    /**
     * List all NPCs.
     * GET /api/npcs
     */
    public void handleListNPCs(Context ctx) {
        ctx.json(registry.getAllProfiles());
    }

    /**
     * Get a specific NPC by ID.
     * GET /api/npcs/{id}
     */
    public void handleGetNPC(Context ctx) {
        try {
            String idStr = ctx.pathParam("id");
            UUID id = UUID.fromString(idStr);

            NPCProfile profile = registry.getProfile(id);
            if (profile == null) {
                ctx.status(404).json(Map.of("error", "NPC not found"));
                return;
            }

            ctx.json(profile);
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid NPC ID format"));
        }
    }

    /**
     * Update an NPC configuration.
     * PUT /api/npcs/{id}
     * Body: NPCProfile JSON
     */
    public void handleUpdateNPC(Context ctx) {
        try {
            String idStr = ctx.pathParam("id");
            UUID id = UUID.fromString(idStr);

            NPCProfile existingProfile = registry.getProfile(id);
            if (existingProfile == null) {
                ctx.status(404).json(Map.of("error", "NPC not found"));
                return;
            }

            NPCUpdateRequest request = ctx.bodyAsClass(NPCUpdateRequest.class);

            // Update fields
            if (request.systemPrompt != null) {
                existingProfile.setSystemPrompt(request.systemPrompt);
            }
            if (request.personality != null) {
                existingProfile.setPersonality(request.personality);
            }
            if (request.aiProviderId != null) {
                existingProfile.setAiProviderId(request.aiProviderId);
            }
            if (request.aiEnabled != null) {
                existingProfile.setAiEnabled(request.aiEnabled);
            }
            if (request.voiceEnabled != null) {
                existingProfile.setVoiceEnabled(request.voiceEnabled);
            }

            // Update personality traits if provided
            if (request.personalityTraits != null) {
                request.personalityTraits.forEach((key, value) -> {
                    existingProfile.setPersonalityTrait(key, value);
                });
            }

            registry.register(existingProfile);

            ctx.json(existingProfile);
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid request: " + e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * Delete an NPC profile.
     * DELETE /api/npcs/{id}
     */
    public void handleDeleteNPC(Context ctx) {
        try {
            String idStr = ctx.pathParam("id");
            UUID id = UUID.fromString(idStr);

            if (registry.hasProfile(id)) {
                registry.remove(id);
                ctx.json(Map.of("message", "NPC profile deleted"));
            } else {
                ctx.status(404).json(Map.of("error", "NPC not found"));
            }
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid NPC ID format"));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    public record NPCUpdateRequest(
            String systemPrompt,
            String personality,
            String aiProviderId,
            Boolean aiEnabled,
            Boolean voiceEnabled,
            Map<String, Double> personalityTraits
    ) {}
}
