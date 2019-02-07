package org.noixdecoco.app.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.data.model.CoconutJournal;
import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Predicate;

@Command(value = EventType.MESSAGE, adminOnly = true)
public class CoconutLogCommand extends CoconutCommand {

    private static final Logger LOGGER = LogManager.getLogger(CoconutLogCommand.class);

    protected String channel;
    protected String timestamp;
    protected String message;
    protected int total = 20;
    protected int offset = 0;

    protected CoconutLogCommand(String giver, String message, String channel, String timestamp, int total, int offset) {
        super(giver);
        this.channel = channel;
        this.message = message;
        this.timestamp = timestamp;
        this.total = total;
        this.offset = offset;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return request -> {
            if (request.getEvent().getText() != null) {
                String text = request.getEvent().getText();
                if (text.toLowerCase().contains("sudo log ")) {
                    return true;
                }
            }
            return false;
        };
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        int total = extractTotal(request.getEvent().getText());
        int offset = extractOffset(request.getEvent().getText());
        return new CoconutLogCommand(request.getEvent().getUser(), null, request.getEvent().getChannel(), request.getEvent().getTs(), total, offset);
    }

    private static int extractTotal(String message) {
        String[] split = message.split("total:");
        if (split.length > 1) {
            return Integer.parseInt(split[1].split(" ")[0]);
        }
        return 20;
    }

    private static int extractOffset(String message) {
        String[] split = message.split("offset:");
        if (split.length > 1) {
            return Integer.parseInt(split[1].split(" ")[0]);
        }
        return 0;
    }

    @Override
    protected boolean validate() {
        return userId != null && channel != null;
    }

    @Override
    protected void performAction() {
        Flux<CoconutJournal> transactions = coconutService.getAllJournals();
        List<CoconutJournal> journals = transactions.skip(offset).buffer(total).blockFirst();
        StringBuilder logs = new StringBuilder();
        logs.append("*Stats* \n Total:" + total + ", Offset: " + offset + "* \n");
        for (CoconutJournal journal : journals) {
            logs.append("Giver: <@" + journal.getUsername() + ">, receiver: <@" + journal.getRecipient() + ">, numCoconuts: "
                    + journal.getCoconutsGiven() + " channel: <#" + journal.getChannel() + "> (" + journal.getChannel() + ")\n");
        }
        slackService.sendMessage(channel, logs.toString());
    }
}
