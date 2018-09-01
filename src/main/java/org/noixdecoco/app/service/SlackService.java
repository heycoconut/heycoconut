package org.noixdecoco.app.service;

import org.noixdecoco.app.dto.ChannelDTO;

public interface SlackService {
    void sendMessage(String channel, String text);

    void sendMessage(String channel, String text, boolean ephemeral);

    ChannelDTO getChannelInfo(String channelId);

    String getBotUserId();
}
