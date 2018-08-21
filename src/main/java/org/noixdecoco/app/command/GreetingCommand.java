package org.noixdecoco.app.command;

public class GreetingCommand extends CoconutCommand {

    private String user;
    private String channel;

    public GreetingCommand(String user, String channel) {
        this.user = user;
        this.channel = channel;
    }


    @Override
    protected boolean validate() {
        return user != null && channel != null;
    }

    @Override
    protected void performAction() {
        speechService.sendMessage(channel, "Welcome, <@" + user + ">! If you want to know how I work, simply ask me for help by tagging my name and saying *'help'*");
    }
}
