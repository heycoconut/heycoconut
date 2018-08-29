package org.noixdecoco.app.command;

import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.data.model.CoconutLedger;
import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Predicate;

@Command(EventType.APP_MENTION)
public class CoconutRankingsCommand extends CoconutCommand {

    private String channel;

    public CoconutRankingsCommand(String channel) {
        this.channel = channel;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return (request) -> {
            String message = request.getEvent().getText();
            if (message != null && message.contains("leaderboard")) {
                return true;
            }
            return false;
        };
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        return new CoconutRankingsCommand(request.getEvent().getChannel());
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
        builder.append("*Leaderboard*\n\n");
        int currentRank = 1;
        for (CoconutLedger ledger : topLedgers) {
            builder.append(currentRank++).append(". <@").append(ledger.getUsername()).append(">: ").append(ledger.getNumberOfCoconuts()).append("\n");
        }
        return builder.toString();
    }
}
