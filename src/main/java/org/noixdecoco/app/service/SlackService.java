package org.noixdecoco.app.service;

import org.noixdecoco.app.dto.ChannelDTO;

public interface SlackService {
    void sendMessage(String channel, String text);

    void sendMessage(String channel, String text, boolean ephemeral, String toUser);

    void addReaction(String channel, String timestamp, String emoji);

    ChannelDTO getChannelInfo(String channelId);

    String getBotUserId();
}
