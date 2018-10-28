package org.noixdecoco.app.command;

import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.data.facts.CoconutFacts;
import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;
import java.util.function.Predicate;

@Command(EventType.APP_MENTION)
public class ExistentialCommand extends CoconutCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExistentialCommand.class);

    private String channel;

    private ExistentialCommand(String user, String channel) {
        super(user);
        this.channel = channel;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return r -> r.getEvent().getText().contains("who are you");
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        return new ExistentialCommand(request.getEvent().getUser(), request.getEvent().getChannel());
    }

    @Override
    protected boolean validate() {
        return channel != null;
    }

    @Override
    protected void performAction() {
        slackService.sendMessage(channel, "I'm the one and only HeyCoconut! I bring joy to the world in the form of virtual coconuts.");
    }
}
