package org.noixdecoco.app.command;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
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
        journals = journals.filter(j -> j.getCoconutGivenAt().getYear() == LocalDateTime.now().getYear());
        Map<String, Long> rankings = new HashMap<>();
        long cocosFound = journals.count().block();
        LOGGER.info("Executing action CoconutChannelRankingsCommand. Found " + cocosFound);

        if (cocosFound > 0) {
            // Not very efficient in the long run. Will need to "flatten" data eventually
            for (CoconutJournal journal : journals.buffer().blockFirst()) {
                LOGGER.info("Calculating...");
                rankings.computeIfAbsent(journal.getRecipient(), (key) -> Long.valueOf(0));
                rankings.put(journal.getRecipient(), rankings.get(journal.getRecipient()) + journal.getCoconutsGiven());
            }
            final Map<String, Long> sorted = rankings.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue((x1, x2) -> Long.compare(x2, x1))).limit(10)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
            slackService.sendMessage(channel, composeLeaderboard(sorted));
        } else {
            slackService.sendMessage(channel, composeNoDataFound());
        }
    }

    private String composeLeaderboard(Map<String, Long> topCocos) {
        final StringBuilder builder = new StringBuilder();
        builder.append("*Leaderboard* for <#").append(channel).append(">\n\n");
        final AtomicInteger currentRank = new AtomicInteger(1);
        topCocos.entrySet().stream().limit(10).forEach((entry) -> {
            builder.append(currentRank.getAndIncrement()).append(". <@").append(entry.getKey()).append(">: ").append(entry.getValue()).append("\n");
        });
        return builder.toString();
    }

    private String composeNoDataFound() {
        final StringBuilder builder = new StringBuilder();
        builder.append("*Leaderboard* for <#").append(channel).append(">\n\n");
        builder.append("*No data found for current year: " + LocalDateTime.now().getYear() + "*.");
        return builder.toString();
    }
}
