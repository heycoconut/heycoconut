package org.noixdecoco.app.command;

import org.noixdecoco.app.GlobalConfig;
import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.dto.ChannelDTO;
import org.noixdecoco.app.dto.ChannelListDTO;
import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@Command(value = EventType.MESSAGE, adminOnly = true)
public class ListJoinedChannelsCommand extends CoconutCommand {

    protected String channel;

    protected ListJoinedChannelsCommand(String giver, String channel) {
        super(giver);
        this.channel = channel;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return request -> {
            if (request.getEvent().getText() != null) {
                String text = request.getEvent().getText();
                if (text.toLowerCase().contains("sudo what channels")) {
                    return true;
                }
            }
            return false;
        };
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        return new ListJoinedChannelsCommand(request.getEvent().getUser(), request.getEvent().getChannel());
    }

    @Override
    protected boolean validate() {
        return userId != null;
    }

    @Override
    protected void performAction() {
        ChannelListDTO channelList = slackService.getChannelsBotIsIn();
        String channelNames = "";
        for (ChannelDTO oneChannel : channelList.getChannels()) {
            if (Boolean.TRUE.equals(oneChannel.getMember())) {
                channelNames += oneChannel.getName() + ", ";
            }
        }
        slackService.sendMessage(channel, channelNames);
    }


}
