package org.noixdecoco.app.command.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.command.*;
import org.noixdecoco.app.dto.EventDTO;
import org.noixdecoco.app.dto.SlackRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CoconutCommandHelper {

    private static final Logger LOGGER = LogManager.getLogger(CoconutCommandHelper.class);

    private static final String COCONUT_EMOJI = ":coconut:";
    private static final String TAG_START = "<@"; // The start of the tag for a user
    private static final String HELP = "help";
    private static final String LEADERBOARD = "leaderboard";

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    public CoconutCommand buildFromRequest(SlackRequestDTO request) {
        CoconutCommand command = null;
        if(request.getEvent() != null && request.getEvent().getType() != null) {
            String message = request.getEvent().getText();
            switch(request.getEvent().getType()) {
                case "app_mention":
                    // You're talking to ME?
                    if (message.toLowerCase().contains(HELP)) {
                        command = new CoconutTutorialCommand(request.getEvent().getChannel());
                    } else if (message.toLowerCase().contains(LEADERBOARD)) {
                        command = new CoconutRankingsCommand();
                    }
                    break;
                case "channel":
                case "group":
                    // Message in a channel/group
                    if (message.contains(COCONUT_EMOJI) && message.contains(TAG_START)) {
                        command = buildGiveCoconutCommand(request.getEvent());
                    }
                    break;
                case "member_joined_channel":
                    // Send a greeting!
                    if (request.getEvent().getUser() != null && request.getEvent().getChannel() != null) {
                        command = new GreetingCommand(request.getEvent().getUser(), request.getEvent().getChannel());
                    }
                    break;
                default:
                    LOGGER.debug(String.format("Unsupported event type: %s", request.getEvent().getType()));
                    break;
            }
        }
        if(command != null) {
            beanFactory.autowireBean(command);
        }
        return command;
    }

    private CoconutCommand buildGiveCoconutCommand(EventDTO event) {
        int coconutsToGive = extractNumberOfCoconuts(event.getText());
        String giver = event.getUser();
        Set<String> receivers = extractTaggedUsers(event.getText());
        GiveCoconutCommand command = new GiveCoconutCommand(giver, receivers, event.getChannel(), coconutsToGive);
        return command;
    }

    private int extractNumberOfCoconuts(String message) {
        return StringUtils.countOccurrencesOf(message, COCONUT_EMOJI);
    }

    private Set<String> extractTaggedUsers(String message) {
        String[] allMentions = message.split("<@");
        Set<String> names = new HashSet<>();
        for (int i=1; i<allMentions.length; i++) { // Skip first element in array which doesnt start with @
            names.add(allMentions[i].substring(0, allMentions[i].indexOf('>')));
        }
        return names;
    }
}
