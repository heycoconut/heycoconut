package org.noixdecoco.app.command;

import org.noixdecoco.app.exception.CoconutException;
import org.noixdecoco.app.exception.InsufficientCoconutsException;
import org.noixdecoco.app.exception.InvalidReceiverException;

import java.util.Set;

public class GiveCoconutCommand extends CoconutCommand {

    private String giver;
    private Set<String> receivers;
    private int coconutCount;
    private String channel;

    public GiveCoconutCommand(String giver, Set<String> receivers, String channel, int coconutCount) {
        this.giver = giver;
        this.receivers = receivers;
        this.channel = channel;
        this.coconutCount = coconutCount;
    }

    @Override
    protected boolean validate() {
        return giver != null && receivers != null && !receivers.isEmpty() && coconutCount > 0;
    }

    @Override
    protected void performAction() {
        StringBuilder responseMessage = new StringBuilder();
        for(String name : receivers) {
            try {
                long numCoconuts = coconutService.giveCoconut(giver, name, coconutCount);
                responseMessage.append("<@").append(giver).append("> gave ").append(coconutCount)
                        .append(" coconut").append((coconutCount > 1 ? "s":"")).append(" to <@").append(name).append(">, ")
                        .append(" they now have ").append(numCoconuts).append(" coconut").append((numCoconuts > 1 ? "s":"")).append(". ");
            }  catch (InsufficientCoconutsException e) {
                responseMessage.append("<@"+giver  + "> didn't have enough coconuts remaining for <@" + name + "> :sob:");
            } catch(InvalidReceiverException e) {
                responseMessage.append("<@"+giver  + "> tried giving himself a coconut, unfortunately that's illegal :sob: If you ask nicely, maybe someone will give you one!");
            } catch (CoconutException e) {
                responseMessage.append("Something went wrong. :sad:");
            }
        }
        speechService.sendMessage(channel, responseMessage.toString());

        long coconutsRemaining = coconutService.getCoconutsRemaining(giver);
        speechService.sendMessage(giver, "You have *" + (coconutsRemaining > 0 ? coconutsRemaining : "no") + "* coconuts left to give today.");
    }
}
