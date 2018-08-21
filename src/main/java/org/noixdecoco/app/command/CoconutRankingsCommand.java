package org.noixdecoco.app.command;

public class CoconutRankingsCommand extends CoconutCommand {
    @Override
    protected boolean validate() {
        return false;
    }

    @Override
    protected void performAction() {
        // TODO: Gather stats and rankings, print results to channel
    }
}
