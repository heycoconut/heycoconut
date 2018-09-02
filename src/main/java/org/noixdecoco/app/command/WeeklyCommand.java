package org.noixdecoco.app.command;

import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.data.model.CoconutJournal;
import org.noixdecoco.app.data.repository.CoconutJournalRepository;
import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toMap;

@Command(EventType.APP_MENTION)
public class WeeklyCommand extends CoconutCommand {

    private String channel;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).minusWeeks(1).with(DayOfWeek.SUNDAY);
    private LocalDateTime end = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).with(DayOfWeek.SUNDAY);
    private String startFormatted = start.format(formatter);
    private String endFormatted = end.format(formatter);

    @Autowired
    protected CoconutJournalRepository journal;

    public WeeklyCommand(String channel) {
        super(null);
        this.channel = channel;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return r -> r.getEvent().getText() != null && r.getEvent().getText().contains("weekly");
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        return new WeeklyCommand(request.getEvent().getChannel());
    }

    @Override
    protected boolean validate() {
        return true;
    }

    @Override
    protected void performAction() {
        List<CoconutJournal> weekStats = journal.findByCoconutGivenAtBetween(start, end).buffer().blockFirst();

        if (weekStats != null) {
            slackService.sendMessage(channel, composeStats(weekStats));
        } else {
            slackService.sendMessage(channel, "No weekly summary from " + startFormatted + " to " + endFormatted);
        }
    }

    private String composeStats(List<CoconutJournal> journals) {
        StringBuilder builder = new StringBuilder();
        builder.append("*Weekly Summary*:chart_with_upwards_trend:\n" + startFormatted + " to " + endFormatted + "\n\n");

        Map<String, Long> givers = new HashMap<>();
        Map<String, Long> recipients = new HashMap<>();
        int count = 1;

        builder.append("*Top Givers* :heart:\n\n");
        for (CoconutJournal journalEntry : journals) {

            String user = journalEntry.getUsername();
            String recipient = journalEntry.getRecipient();
            Long coconutsGiven = journalEntry.getCoconutsGiven();

            // Build givers hashmap
            if (givers.containsKey(user)) {
                givers.put(user, givers.get(user) + coconutsGiven);
            } else {
                givers.put(user, coconutsGiven);
            }

            // Build recipients hashmap
            if (recipients.containsKey(recipient)) {
                recipients.put(recipient, recipients.get(recipient) + coconutsGiven);
            } else {
                recipients.put(recipient, coconutsGiven);
            }
        }

        for (Map.Entry<String, Long> giver : sortValDesc(givers).entrySet()) {
            builder.append(count++).append(". <@").append(giver.getKey()).append(">: ").append(giver.getValue()).append("\n");
        }

        count = 1;
        builder.append("\n*Top Recipients* :trophy:\n\n");
        for (Map.Entry<String, Long> recipient : sortValDesc(recipients).entrySet()) {
            builder.append(count++).append(". <@").append(recipient.getKey()).append(">: ").append(recipient.getValue()).append("\n");
        }

        return builder.toString();
    }

    private Map<String, Long> sortValDesc(Map<String, Long> unsorted) {
        return unsorted.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(toMap(Map.Entry::getKey,
                        Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
