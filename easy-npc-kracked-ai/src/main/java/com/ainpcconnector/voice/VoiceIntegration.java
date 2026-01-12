package com.ainpcconnector.voice;

import com.ainpcconnector.ai.AIProvider;
import com.ainpcconnector.ai.AIProviderFactory;
import com.ainpcconnector.config.ConfigManager;
import com.ainpcconnector.npc.NPCProfile;
import com.ainpcconnector.npc.NPCRegistry;
import de.maxhenkel.voicechat.api.audiochannel.AudioPlayer;
import de.maxhenkel.voicechat.api.audiochannel.EntityAudioChannel;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import net.minecraft.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Voice integration for Text-to-Speech (TTS).
 */
public class VoiceIntegration {

    private static final Logger LOGGER = LoggerFactory.getLogger(VoiceIntegration.class);
    private boolean enabled = true;

    /**
     * Speak text using TTS for an NPC.
     */
    public void speak(Entity entity, String text) {
        if (!enabled) {
            return;
        }

        VoicechatServerApi api = AINpcVoicechatPlugin.voicechatServerApi;
        if (api == null) {
            LOGGER.debug("[AI NPC Connector] Voicechat API not initialized or mod not installed");
            return;
        }

        NPCProfile profile = NPCRegistry.getInstance().getProfile(entity.getUuid());
        if (profile == null || !profile.isVoiceEnabled()) {
            return;
        }

        ConfigManager configManager = com.ainpcconnector.AINpcConnectorMod.getConfigManager();
        if (configManager == null)
            return;

        // Use the NPC's configured AI provider for TTS
        AIProvider aiProvider = AIProviderFactory.createForNPC(profile, configManager.getConfig());
        aiProvider.generateSpeech(text).thenAccept(audioData -> {
            if (audioData != null && audioData.length > 0) {
                processAndPlay(api, entity, audioData);
            } else {
                LOGGER.debug("[AI NPC Connector] No audio data received from provider");
            }
        });
    }

    private void processAndPlay(VoicechatServerApi api, Entity entity, byte[] audioData) {
        try {
            // Upsample from 24kHz to 48kHz (OpenAI provides 24kHz PCM)
            byte[] upsampledData = upsample24to48(audioData);

            // Convert bytes to shorts for the API
            short[] pcm = new short[upsampledData.length / 2];
            for (int i = 0; i < pcm.length; i++) {
                pcm[i] = (short) ((upsampledData[i * 2] & 0xFF) | (upsampledData[i * 2 + 1] << 8));
            }

            // Play in Minecraft
            playAudio(api, entity, pcm);
        } catch (Exception e) {
            LOGGER.error("[AI NPC Connector] Error playing NPC voice", e);
        }
    }

    private void playAudio(VoicechatServerApi api, Entity entity, short[] pcm) {
        de.maxhenkel.voicechat.api.Entity voicechatEntity = api.fromEntity(entity);
        EntityAudioChannel channel = api.createEntityAudioChannel(entity.getUuid(), voicechatEntity);
        if (channel == null)
            return;

        AudioPlayer player = api.createAudioPlayer(channel, api.createEncoder(), pcm);
        player.startPlaying();

        LOGGER.info("[AI NPC Connector] NPC {} is speaking: {} samples", entity.getName().getString(), pcm.length);
    }

    private byte[] upsample24to48(byte[] data) {
        // Simple sample doubling for 24kHz -> 48kHz conversion
        byte[] upsampled = new byte[data.length * 2];
        for (int i = 0; i < data.length; i += 2) {
            // First 48kHz sample (copy of 24kHz sample)
            upsampled[i * 2] = data[i];
            upsampled[i * 2 + 1] = data[i + 1];

            // Second 48kHz sample (copy of 24kHz sample)
            upsampled[i * 2 + 2] = data[i];
            upsampled[i * 2 + 3] = data[i + 1];
        }
        return upsampled;
    }

    public boolean isAvailable() {
        return AINpcVoicechatPlugin.voicechatServerApi != null;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void shutdown() {
        // Clean up
    }
}
