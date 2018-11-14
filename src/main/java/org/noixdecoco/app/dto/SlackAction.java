package org.noixdecoco.app.dto;

public enum SlackAction {

    POST_MESSAGE("chat.postMessage"),
    POST_EPHEMERAL("chat.postEphemeral"),
    ADD_REACTION("reactions.add"),
    CHANNEL_INFO("channels.info"),
    BOT_INFO("bot.info"),
    AUTH_INFO("auth.info");

    private String key;

    SlackAction(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
