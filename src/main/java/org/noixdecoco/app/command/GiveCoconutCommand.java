package org.noixdecoco.app.command;

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

@Command(EventType.MESSAGE)
public class GiveCoconutCommand extends CoconutCommand {

    protected String giver;
    protected Set<String> receivers;
    protected int coconutCount;
    protected String channel;

    protected static final String COCONUT_EMOJI = ":coconut:";
    protected static final String TAG_START = "<@";

    protected GiveCoconutCommand(String giver, Set<String> receivers, String channel, int coconutCount) {
        this.giver = giver;
        this.receivers = receivers;
        this.channel = channel;
        this.coconutCount = coconutCount;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return request -> {
            if (request.getEvent().getText() != null) {
                String text = request.getEvent().getText();
                if (text.contains(COCONUT_EMOJI) && text.contains(TAG_START)) {
                    return true;
                }
            }
            return false;
        };
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        int coconutsToGive = extractNumberOfCoconuts(request.getEvent().getText());
        Set<String> receivers = extractTaggedUsers(request.getEvent().getText());
        return new GiveCoconutCommand(request.getEvent().getUser(), receivers, request.getEvent().getChannel(), coconutsToGive);
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
            try {
                long numCoconuts = coconutService.giveCoconut(giver, name, coconutCount);
                responseMessage.append("<@").append(giver).append("> gave ").append(coconutCount)
                        .append(" coconut").append((coconutCount > 1 ? "s" : "")).append(" to <@").append(name).append(">, ")
                        .append(" they now have ").append(numCoconuts).append(" coconut").append((numCoconuts > 1 ? "s" : "")).append(". ");
            } catch (InsufficientCoconutsException e) {
                responseMessage.append("<@" + giver + "> didn't have enough coconuts remaining for <@" + name + "> :sob:");
            } catch (InvalidReceiverException e) {
                responseMessage.append("<@" + giver + "> tried giving himself a coconut, unfortunately that's illegal :sob: If you ask nicely, maybe someone will give you one!");
            } catch (CoconutException e) {
                responseMessage.append("Something went wrong. :sad:");
            }
        }
        slackService.sendMessage(channel, responseMessage.toString());

        long coconutsRemaining = coconutService.getCoconutsRemaining(giver);
        slackService.sendMessage(giver, "You have *" + (coconutsRemaining > 0 ? coconutsRemaining : "no") + "* coconuts left to give today.");
    }


}
