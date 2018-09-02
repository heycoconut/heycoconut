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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Command(EventType.APP_MENTION)
public class WeeklyCommand extends CoconutCommand {

    private String channel;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).minusWeeks(1).with(DayOfWeek.SUNDAY);
    private LocalDateTime end = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).minusWeeks(0).with(DayOfWeek.SUNDAY);
    private String startFormatted = start.format(formatter);
    private String endFormatted = end.format(formatter);

    @Autowired
    protected CoconutJournalRepository journal;

    public WeeklyCommand(String channel) {
        super(null);
        this.channel = channel;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return (request) -> {
            String message = request.getEvent().getText();
            if (message != null && message.contains("weekly")) {
                return true;
            }
            return false;
        };
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
        List<CoconutJournal> weekStats = journal.findByCoconutGivenAtBetween(start, end).buffer(10).blockFirst();

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

        builder.append("*Givers*\n\n");
        for (CoconutJournal journal : journals) {

            String user = journal.getUsername();
            String recipient = journal.getRecipient();
            Long coconutsGiven = journal.getCoconutsGiven();

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

        for (String giver : givers.keySet()) {
            int currentRank = 1;
            builder.append(currentRank++).append(". <@").append(giver).append(">: ").append(givers.get(giver)).append("\n");
        }

        builder.append("\n*Recipients*\n\n");
        for (String recipient : recipients.keySet()) {
            int currentRank = 1;
            builder.append(currentRank++).append(". <@").append(recipient).append(">: ").append(recipients.get(recipient)).append("\n");
        }

        return builder.toString();
    }
}
