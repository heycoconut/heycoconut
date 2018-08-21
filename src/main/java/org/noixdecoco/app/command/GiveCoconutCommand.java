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
        return giver != null && receivers != null && receivers.size() > 0 && coconutCount > 0;
    }

    @Override
    protected void performAction() {
        String text = "";
        for(String name : receivers) {
            try {
                long numCoconuts = coconutService.giveCoconut(giver, name, coconutCount);
                text += "<@"+ giver  + "> gave " + coconutCount +
                        " coconut" + (coconutCount > 1 ? "s":"") + " to <@" + name + ">, " +
                        " they now have " + numCoconuts + " coconut" + (numCoconuts > 1 ? "s":"") + ". ";
            }  catch (InsufficientCoconutsException e) {
                text += "<@"+giver  + "> didn't have enough coconuts remaining for <@" + name + "> :sob:";
            } catch(InvalidReceiverException e) {
                text += "<@"+giver  + "> tried giving himself a coconut, unfortunately that's illegal :sob: If you ask nicely, maybe someone will give you one!";
            } catch (CoconutException e) {
                text += "Something went wrong. :sad:";
            }
        }
        speechService.sendMessage(channel, text);

        long coconutsRemaining = coconutService.getCoconutsRemaining(giver);
        speechService.sendMessage(giver, "You have *" + (coconutsRemaining > 0 ? coconutsRemaining : "no") + "* coconuts left to give today.");
    }
}
