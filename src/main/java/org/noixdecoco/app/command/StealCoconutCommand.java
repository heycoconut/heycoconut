package org.noixdecoco.app.command;

import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@Command(value = EventType.MESSAGE, adminOnly = true)
public class StealCoconutCommand extends CoconutCommand {

    protected String giver;
    protected Set<String> receivers;
    protected int coconutCount;
    protected String channel;

    protected static final String COCONUT_EMOJI = ":coconut:";
    protected static final String TAG_START = "<@";

    protected StealCoconutCommand(String giver, Set<String> receivers, String channel, int coconutCount) {
        this.giver = giver;
        this.receivers = receivers;
        this.channel = channel;
        this.coconutCount = coconutCount;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return request -> {
            if (request.getEvent().getText() != null) {
                String text = request.getEvent().getText();
                if (text.contains(COCONUT_EMOJI) && text.contains(TAG_START) && text.toLowerCase().contains("steal")) {
                    return true;
                }
            }
            return false;
        };
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        int coconutsToSteal = extractNumberOfCoconuts(request.getEvent().getText());
        Set<String> receivers = extractTaggedUsers(request.getEvent().getText());
        return new StealCoconutCommand(request.getEvent().getUser(), receivers, request.getEvent().getChannel(), coconutsToSteal);
    }

    private static int extractNumberOfCoconuts(String message) {
        int count = 1;
        if (message != null) {
            count = StringUtils.countOccurrencesOf(message, COCONUT_EMOJI);
        }
        return count;
    }

    private static Set<String> extractTaggedUsers(String message) {
        String[] allMentions = message.split(TAG_START);
        Set<String> names = new HashSet<>();
        for (int i = 1; i < allMentions.length; i++) { // Skip first element in array which doesnt start with @
            names.add(allMentions[i].substring(0, allMentions[i].indexOf('>')));
        }
        return names;
    }

    @Override
    protected boolean validate() {
        return giver != null && receivers != null && !receivers.isEmpty() && coconutCount > 0;
    }

    @Override
    protected void performAction() {
        StringBuilder responseMessage = new StringBuilder();
        for (String name : receivers) {
            coconutService.addCoconut(name, -coconutCount);
            responseMessage.append("<@").append(name).append("> has " + (coconutCount > 0 ? "received" : "lost")).append(coconutCount).append(" coconut").append((Math.abs(coconutCount) > 1 ? "s" : "")).append(".");
        }
        slackService.sendMessage(channel, responseMessage.toString());
    }


}
