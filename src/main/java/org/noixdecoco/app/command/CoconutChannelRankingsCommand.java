package org.noixdecoco.app.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.data.model.CoconutJournal;
import org.noixdecoco.app.data.model.CoconutLedger;
import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Command(EventType.APP_MENTION)
public class CoconutChannelRankingsCommand extends CoconutCommand {

    private static final Logger LOGGER = LogManager.getLogger(CoconutChannelRankingsCommand.class);

    private String channel;

    public CoconutChannelRankingsCommand(String userId, String channel) {
        super(userId);
        this.channel = channel;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return r -> r.getEvent().getText() != null && r.getEvent().getText().contains("leaderboard") && !r.getEvent().getText().contains("overall");
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        return new CoconutChannelRankingsCommand(request.getEvent().getUser(), request.getEvent().getChannel());
    }

    @Override
    protected boolean validate() {
        return true;
    }

    @Override
    protected void performAction() {
        Flux<CoconutJournal> journals = journalRepo.findByChannel(channel);
        Map<String, Long> rankings = new HashMap<>();
        LOGGER.info("Executing action CoconutChannelRankingsCommand");
        // Not very efficient in the long run. Will need to "flatten" data eventually
        journals.subscribe((journal) -> {
            LOGGER.info("Calculating...");
            rankings.computeIfAbsent(journal.getRecipient(), (key) -> Long.valueOf(0));
            rankings.put(journal.getRecipient(), rankings.get(journal.getRecipient()) + journal.getCoconutsGiven());
        });
        journals.doOnComplete(() -> {
            final Map<String, Long> sorted = rankings.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x,y) -> y, LinkedHashMap::new));
            slackService.sendMessage(channel, composeLeaderboard(sorted));
            LOGGER.info("Inside doOnComplete method. Collected " + sorted.size() + " entries");
        });


    }

    private String composeLeaderboard(Map<String, Long> topCocos) {
        StringBuilder builder = new StringBuilder();
        builder.append("*Leaderboard* for <#").append(channel).append(">\n\n");
        int currentRank = 1;
        for (Map.Entry<String, Long> entry : topCocos.entrySet()) {
            builder.append(currentRank++).append(". <@").append(entry.getKey()).append(">: ").append(entry.getValue()).append("\n");
        }
        return builder.toString();
    }
}