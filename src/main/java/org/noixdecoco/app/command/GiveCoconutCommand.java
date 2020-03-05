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

@Command(EventType.MESSAGE)
public class GiveCoconutCommand extends CoconutCommand {

    private static final Logger LOGGER = LogManager.getLogger(GiveCoconutCommand.class);

    protected Set<String> receivers;
    protected int coconutCount;
    protected String channel;
    protected String timestamp;
    protected String message;
    protected String emoji = GlobalConfig.emoji;
    protected static final String TAG_START = "<@";

    protected GiveCoconutCommand(String giver, String message, Set<String> receivers, String channel, String timestamp, int coconutCount) {
        super(giver);
        this.receivers = receivers;
        this.channel = channel;
        this.message = message;
        this.timestamp = timestamp;
        this.coconutCount = coconutCount;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return request -> {
            if (request.getEvent().getText() != null) {
                String text = request.getEvent().getText();
                if (text.contains(GlobalConfig.COCONUT_EMOJI) && text.contains(TAG_START) && !text.toLowerCase().contains("sudo ") && !text.toLowerCase().contains("gift ")) {
                    return true;
                }
            }
            return false;
        };
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        int coconutsToGive = extractNumberOfCoconuts(request.getEvent().getText());
        Set<String> receivers = extractTaggedUsers(request.getEvent().getText());
        return new GiveCoconutCommand(request.getEvent().getUser(), request.getEvent().getText(), receivers, request.getEvent().getChannel(), request.getEvent().getTs(), coconutsToGive);
    }

    private static int extractNumberOfCoconuts(String message) {
        int count = 1;
        if (message != null) {
            count = StringUtils.countOccurrencesOf(message, GlobalConfig.COCONUT_EMOJI);
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
        return userId != null && receivers != null && !receivers.isEmpty() && coconutCount > 0;
    }

    @Override
    protected void performAction() {
        StringBuilder responseMessage = new StringBuilder();
        String giver = "<@" + userId + ">";
        int coconutsRemaining = coconutService.getCoconutsRemaining(userId);
        LOGGER.info(userId + " has " + coconutsRemaining + " coconuts left to give today.");
        String emojiPlural = emoji + (coconutCount > 0 ? "s" : "");
        if(coconutCount > coconutsRemaining) {
            LOGGER.info(coconutsRemaining + " is less than " + coconutCount);
            slackService.sendMessage(channel, "You tried giving " + coconutCount + " " + emojiPlural + " but you have *"
                    + (coconutsRemaining > 0 ? coconutsRemaining : "no") + "* " + emojiPlural + " left to give today.", true, userId);
            slackService.addReaction(this.channel, this.timestamp, "heavy_multiplication_x");
        } else if(receivers.size() > 1 && (coconutCount / receivers.size() < 1)) {
            slackService.sendMessage(channel, "Not enough " + emojiPlural + " to split between " + receivers.size() + " people.",true, userId);
            slackService.addReaction(this.channel, this.timestamp, "heavy_multiplication_x");
        } else {
            coconutCount /= receivers.size(); // Split coconuts between people and possibly leave a remainder
            for (String name : receivers) {
                try {
                    long numCoconuts = coconutService.giveCoconut(userId, name, coconutCount, channel);
                    responseMessage.append(giver).append(" gave ").append(coconutCount)
                            .append(" " + emoji).append((coconutCount > 1 ? "s" : "")).append(" to <@").append(name).append(">. ");

                    message = message.replaceAll(":" + emoji + ":", "").trim(); //Remove emojis from message

                    // Message receiver
                    slackService.sendMessage(name, giver + " has given you " + coconutCount + " " + emojiPlural + " In <#" + channel + ">. \n`"
                            + message + "`\n You now have *" + (numCoconuts > 0 ? numCoconuts : "no") + "* "+ emojiPlural +".");

                    coconutsRemaining = coconutService.getCoconutsRemaining(userId);

                    // Message giver
                    slackService.sendMessage(userId, "You have given <@" + name + "> " + coconutCount + " " + emojiPlural + " In <#" + channel + ">. Reason:\n`"
                            + message + "`\n You have *" + coconutsRemaining + "* "+ emojiPlural +" left to give today.");
                    slackService.addReaction(this.channel, this.timestamp, "heavy_check_mark");
                } catch (InsufficientCoconutsException e) {
                    responseMessage.append(giver + " didn't have enough "+ emoji +"s remaining for <@" + name + "> :sob:");
                    slackService.addReaction(this.channel, this.timestamp, "heavy_multiplication_x");
                } catch (InvalidReceiverException e) {
                    responseMessage.append(giver + " tried giving themself a " + emoji + ", unfortunately that's illegal :sob: If you ask nicely, maybe someone will give you one!");
                    slackService.addReaction(this.channel, this.timestamp, "heavy_multiplication_x");
                } catch (CoconutException e) {
                    responseMessage.append("Something went wrong. :sad:");
                    slackService.addReaction(this.channel, this.timestamp, "heavy_multiplication_x");
                }
            }
            if(timestamp == null) {
                // Only send a message if coconut wasn't given verbally, so no timestamp on reaction added
                slackService.sendMessageThread(channel, responseMessage.toString(), false, null, timestamp);
            }
            coconutsRemaining = coconutService.getCoconutsRemaining(userId);
            slackService.sendMessage(channel, "You have *" + coconutsRemaining + "* " + emoji + " left to give today.", true, userId);
        }
    }
}
