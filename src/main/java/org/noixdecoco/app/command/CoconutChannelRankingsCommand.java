package org.noixdecoco.app.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.data.TimePeriod;
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
    private TimePeriod period;

    public CoconutChannelRankingsCommand(String userId, String channel, TimePeriod period) {
        super(userId);
        this.period = period;
        this.channel = channel;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return r -> r.getEvent().getText() != null && r.getEvent().getText().contains("leaderboard") && !r.getEvent().getText().contains("overall");
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        TimePeriod period = TimePeriod.MONTH;
        if (request.getEvent().getText().contains("yearly")) {
            period = TimePeriod.YEAR;
        } else if (request.getEvent().getText().contains("weekly")) {
            period = TimePeriod.WEEK;
        } else if (request.getEvent().getText().contains("all") && request.getEvent().getText().contains("time")) {
            period = TimePeriod.ALL_TIME;
        }

        return new CoconutChannelRankingsCommand(request.getEvent().getUser(), request.getEvent().getChannel(), period);
    }

    @Override
    protected boolean validate() {
        return true;
    }

    @Override
    protected void performAction() {
        Flux<CoconutJournal> journals = journalRepo.findByChannel(channel);
        if (period != TimePeriod.ALL_TIME) {
            // Yearly leaderboard
            journals = journals.filter(j -> j.getCoconutGivenAt().getYear() == LocalDateTime.now().getYear());
            if (period != TimePeriod.YEAR) {
                // Monthly Leaderboard
                journals = journals.filter(j -> j.getCoconutGivenAt().getMonth() == LocalDateTime.now().getMonth());
                if (period != TimePeriod.MONTH) {
                    // Weekly leaderboard
                    final WeekFields weekField = WeekFields.of(Locale.getDefault());
                    journals = journals.filter(j -> j.getCoconutGivenAt().get(weekField.weekOfWeekBasedYear()) == LocalDateTime.now().get(weekField.weekOfWeekBasedYear()));
                }
            }
        }

        Map<String, Long> rankings = new HashMap<>();
        long cocosFound = journals.count().block();
        LOGGER.info("Executing action CoconutChannelRankingsCommand. Found " + cocosFound);

        if (cocosFound > 0) {
            // Not very efficient in the long run. Will need to "flatten" data eventually
            for (CoconutJournal journal : journals.buffer().blockFirst()) {
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
        String timePeriodDescription = "";
        switch (period) {
            case ALL_TIME:
                timePeriodDescription = "all time";
                break;
            case YEAR:
                timePeriodDescription = "the year " + LocalDateTime.now().getYear();
                break;
            case MONTH:
                timePeriodDescription = "the month of " + LocalDateTime.now().getMonth().getDisplayName(TextStyle.FULL, Locale.CANADA);
                break;
            case WEEK:
                timePeriodDescription = "the week from " + LocalDateTime.now().getMonth().getDisplayName(TextStyle.FULL, Locale.CANADA) + " " + getFirstDayOfWeek(LocalDate.now()) + " to " + getLastDayOfWeek(LocalDate.now());
                break;
        }
        builder.append("*Leaderboard* for <#").append(channel).append("> for " + timePeriodDescription + "\n\n");

        final AtomicInteger currentRank = new AtomicInteger(1);
        topCocos.entrySet().stream().limit(10).forEach((entry) -> {
            builder.append(currentRank.getAndIncrement()).append(". <@").append(entry.getKey()).append(">: ").append(entry.getValue()).append("\n");
        });
        builder.append("_(note you can specify the time period for the leaderboard by specifying *yearly*, *monthly*, *weekly*, or *all time*)_");
        return builder.toString();
    }

    private String composeNoDataFound() {
        final StringBuilder builder = new StringBuilder();
        builder.append("*Leaderboard* for <#").append(channel).append(">\n\n");
        builder.append("*No data found for current year: " + LocalDateTime.now().getYear() + "*.");
        return builder.toString();
    }

    private int getFirstDayOfWeek(LocalDate date) {
        return date.with(WeekFields.of(Locale.US).dayOfWeek(), 1L).getDayOfMonth();
    }

    private int getLastDayOfWeek(LocalDate date) {
        return date.with(WeekFields.of(Locale.US).dayOfWeek(), 7L).getDayOfMonth();
    }

}
