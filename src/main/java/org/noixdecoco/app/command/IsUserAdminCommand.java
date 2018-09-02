package org.noixdecoco.app.command;

import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;

import java.util.function.Predicate;

@Command(EventType.APP_MENTION)
public class IsUserAdminCommand extends CoconutCommand {

    private String channel;

    private IsUserAdminCommand(String user, String channel) {
        super(user);
        this.channel = channel;
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return r -> r.getEvent().getText() != null && r.getEvent().getText().toLowerCase().contains("am i admin");
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        return new IsUserAdminCommand(request.getEvent().getUser(), request.getEvent().getChannel());
    }

    @Override
    protected boolean validate() {
        return userId != null && channel != null;
    }

    @Override
    protected void performAction() {
        if (userService.isAdmin(userId)) {
            slackService.sendMessage(channel, "Yes m'lord, at your service", false);
        } else {
            slackService.sendMessage(channel, "Unfortunately not young lad", false);
        }
    }
}
