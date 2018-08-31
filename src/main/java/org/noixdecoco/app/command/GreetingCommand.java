package org.noixdecoco.app.command;

import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;

import java.util.function.Predicate;

@Command(EventType.MEMBER_JOINED_CHANNEL)
public class GreetingCommand extends CoconutCommand {

    private String user;
    private String channel;

    private GreetingCommand(String user, String channel) {
        this.user = user;
        this.channel = channel;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        // TODO: Greet new users only once. Currently the user gets greeted in all channels they join
        return request -> true;
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        return new GreetingCommand(request.getEvent().getUser(), request.getEvent().getChannel());
    }

    @Override
    protected boolean validate() {
        return user != null && channel != null;
    }

    @Override
    protected void performAction() {
        speechService.sendMessage(channel, "Welcome, <@" + user + ">! If you want to know how I work, simply ask me for help by tagging my name and saying *'help'*");
    }
}
