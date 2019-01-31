package org.noixdecoco.app.command;

import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;

import java.util.function.Predicate;

//@Command(EventType.MEMBER_JOINED_CHANNEL)
public class GreetingCommand extends CoconutCommand {

    private String channel;

    private GreetingCommand(String user, String channel) {
        super(user);
        this.channel = channel;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return r -> true;
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        return new GreetingCommand(request.getEvent().getUser(), request.getEvent().getChannel());
    }

    @Override
    protected boolean validate() {
        if (userId != null && channel != null) {
            return slackService.getChannelInfo(channel).getGeneral();
        }
        return false;
    }

    @Override
    protected void performAction() {
        slackService.sendMessage(channel, "Welcome, <@" + userId + ">! If you want to know how I work, simply ask me for help by tagging my name and saying *'help'*");
    }
}
