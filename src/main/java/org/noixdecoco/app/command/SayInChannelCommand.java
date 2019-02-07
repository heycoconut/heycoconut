package org.noixdecoco.app.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.GlobalConfig;
import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;
import org.noixdecoco.app.exception.CoconutException;
import org.noixdecoco.app.exception.InsufficientCoconutsException;
import org.noixdecoco.app.exception.InvalidReceiverException;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@Command(value = EventType.MESSAGE, adminOnly = true)
public class SayInChannelCommand extends CoconutCommand {

    private static final Logger LOGGER = LogManager.getLogger(SayInChannelCommand.class);

    protected String channel;
    protected String timestamp;
    protected String message;
    protected static final String CHANNEL_TAG_START = "<#";

    protected SayInChannelCommand(String giver, String message, String channel, String timestamp) {
        super(giver);
        this.channel = channel;
        this.message = message;
        this.timestamp = timestamp;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return request -> {
            if (request.getEvent().getText() != null) {
                String text = request.getEvent().getText();
                if (text.contains(CHANNEL_TAG_START) && !text.toLowerCase().contains("sudo say ")) {
                    return true;
                }
            }
            return false;
        };
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        String message = extractMessage(request.getEvent().getText());
        String channel = extractChannel(request.getEvent().getText());
        return new SayInChannelCommand(request.getEvent().getUser(), message, channel, request.getEvent().getTs());
    }

    private static String extractMessage(String message) {
        message = message.replaceAll("sudo say","").trim();
        return message.replaceAll("<#(\\S)*>","").trim();
    }

    private static String extractChannel(String message) {
        String[] splitMessage = message.split(CHANNEL_TAG_START);
        return splitMessage[1].substring(0, splitMessage[1].indexOf('>'));
    }

    @Override
    protected boolean validate() {
        return userId != null && channel != null;
    }

    @Override
    protected void performAction() {
        slackService.sendMessage(channel, message);
    }
}
