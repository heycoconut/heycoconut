package org.noixdecoco.app.command;

import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;
import org.springframework.beans.factory.annotation.Value;

import java.util.function.Predicate;

@Command(EventType.APP_MENTION)
public class CoconutTutorialCommand extends CoconutCommand {

    private String channel;

    @Value("${tutorial.message}")
    private String tutorialMessage;

    public CoconutTutorialCommand(String channel) {
        this.channel = channel;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return request -> {
            if (request.getEvent() != null && EventType.APP_MENTION.getValue().equals(request.getEvent().getType())) {
                String message = request.getEvent().getText();
                if (message != null && message.contains("help")) {
                    return true;
                }
            }
            return false;
        };
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        return new CoconutTutorialCommand(request.getEvent().getChannel());
    }

    @Override
    protected boolean validate() {
        return channel != null;
    }

    @Override
    protected void performAction() {
        speechService.sendMessage(channel, tutorialMessage);
    }
}
