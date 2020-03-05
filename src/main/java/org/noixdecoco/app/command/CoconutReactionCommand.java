package org.noixdecoco.app.command;

import org.noixdecoco.app.GlobalConfig;
import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@Command(EventType.REACTION_ADDED)
public class CoconutReactionCommand extends GiveCoconutCommand {


    protected CoconutReactionCommand(String giver, Set<String> receivers, String channel, String timestamp, int coconutCount) {
        super(giver, "(from a reaction)", receivers, channel, timestamp, coconutCount);
    }

    public static Predicate<SlackRequestDTO> getPredicate() {
        return r -> r.getEvent().getReaction().equalsIgnoreCase(GlobalConfig.emoji) && r.getEvent().getItemUser() != null;
    }

    public static CoconutCommand build(SlackRequestDTO request) {
        int coconutsToGive = 1;
        Set<String> receivers = new HashSet<>(1);
        receivers.add(request.getEvent().getItemUser());
        return new CoconutReactionCommand(request.getEvent().getUser(), receivers, request.getEvent().getItem().getChannel(), request.getEvent().getItem().getTs(), coconutsToGive);
    }
}
