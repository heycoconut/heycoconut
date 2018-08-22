package org.noixdecoco.app.command;

import org.springframework.beans.factory.annotation.Value;

public class CoconutTutorialCommand extends CoconutCommand {

    private String channel;

    @Value("${tutorial.message}")
    private String tutorialMessage;

    public CoconutTutorialCommand(String channel) {
        this.channel = channel;
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
