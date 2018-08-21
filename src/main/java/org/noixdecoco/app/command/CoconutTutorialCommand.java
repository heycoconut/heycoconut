package org.noixdecoco.app.command;

public class CoconutTutorialCommand extends CoconutCommand {

    private String channel;

    public CoconutTutorialCommand(String channel) {
        this.channel = channel;
    }

    @Override
    protected boolean validate() {
        return channel != null;
    }

    @Override
    protected void performAction() {
        speechService.sendMessage(channel, "Thank you for using HeyCoconut! <INSERT TUTORIAL TEXT HERE");
    }
}
