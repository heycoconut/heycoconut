package org.noixdecoco.app.command;

public class CoconutRankingsCommand extends CoconutCommand {

    private String channel;

    public CoconutRankingsCommand(String channel) {
        this.channel = channel;
    }

    @Override
    protected boolean validate() {
        return true;
    }

    @Override
    protected void performAction() {
        // TODO: Gather stats and rankings, print results to channel
        ledgerRepo.getUsersByTotalCoconutCount(10);
        speechService.sendMessage(channel, "TODO: Leaderboards...");
    }
}
