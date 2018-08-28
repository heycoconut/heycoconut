package org.noixdecoco.app.command;

import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

@Command(EventType.APP_MENTION)
public class TriviaCommand extends CoconutCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(TriviaCommand.class);

    @Value("#{'${coconut.facts}'.split('+')}")
    private List<String> coconutFacts;

    private String channel;

    private Random random;

    private TriviaCommand(String channel) {
        this.channel = channel;
        random = new Random(System.currentTimeMillis());
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return request -> {
            if (request.getEvent().getText().contains("trivia")) {
                return true;
            }
            return false;
        };
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        return new TriviaCommand(request.getEvent().getChannel());
    }

    @Override
    protected boolean validate() {
        return channel != null && coconutFacts != null;
    }

    @Override
    protected void performAction() {
        if (coconutFacts == null || coconutFacts.isEmpty()) {
            speechService.sendMessage(channel, "I sure wish I knew some random facts about coconuts!");
        } else {
            speechService.sendMessage(channel, coconutFacts.get(random.nextInt(coconutFacts.size())));
        }
    }
}
