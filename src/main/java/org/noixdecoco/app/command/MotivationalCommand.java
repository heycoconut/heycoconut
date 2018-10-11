package org.noixdecoco.app.command;

import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.function.Predicate;

@Command(EventType.APP_MENTION)
public class MotivationalCommand extends CoconutCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(MotivationalCommand.class);

    private String channel;

    private Random random;

    private MotivationalCommand(String user, String channel) {
        super(user);
        this.channel = channel;
        random = new Random(System.currentTimeMillis());
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return r -> r.getEvent().getText().contains("who da best");
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        return new MotivationalCommand(request.getEvent().getUser(), request.getEvent().getChannel());
    }

    @Override
    protected boolean validate() {
        return channel != null;
    }

    @Override
    protected void performAction() {
        slackService.sendMessage(channel, "*You* da best, <@" + userId + ">!");
    }
}
