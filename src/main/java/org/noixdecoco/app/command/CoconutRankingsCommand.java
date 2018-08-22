package org.noixdecoco.app.command;

import org.noixdecoco.app.data.model.CoconutLedger;
import org.springframework.data.domain.Sort;

import java.util.List;

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
        Sort sort = new Sort(Sort.Direction.DESC, "numberOfCoconuts");
        List<CoconutLedger> topTen = ledgerRepo.findAll(sort).buffer(10).blockFirst();

        speechService.sendMessage(channel, composeLeaderboard(topTen));
    }

    private String composeLeaderboard(List<CoconutLedger> topLedgers) {
        StringBuilder builder = new StringBuilder();
        builder.append("Leaderboard\n```--------------------------------\n");
        int currentRank = 1;
        for (CoconutLedger ledger : topLedgers) {
            builder.append(currentRank++).append(". <@").append(ledger.getUsername()).append("> :").append(ledger.getNumberOfCoconuts());
        }
        return builder.toString();
    }
}
