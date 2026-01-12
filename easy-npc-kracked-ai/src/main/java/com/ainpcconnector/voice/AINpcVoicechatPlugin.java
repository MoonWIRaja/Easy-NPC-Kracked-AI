package com.ainpcconnector.voice;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;

import java.util.logging.Logger;

/**
 * Voicechat plugin implementation for Simple Voice Chat API.
 */
public class AINpcVoicechatPlugin implements VoicechatPlugin {

    public static VoicechatServerApi voicechatServerApi;
    private static final Logger LOGGER = Logger.getLogger("AINpcVoicechatPlugin");

    @Override
    public String getPluginId() {
        return "easynpcai";
    }

    @Override
    public void initialize(VoicechatApi api) {
        if (api instanceof VoicechatServerApi serverApi) {
            voicechatServerApi = serverApi;
            LOGGER.info("[Easy NPC kracked AI] Voicechat Server API initialized");
        }
    }
}
