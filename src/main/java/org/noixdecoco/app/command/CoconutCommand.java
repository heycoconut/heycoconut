package org.noixdecoco.app.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.data.repository.CoconutLedgerRepository;
import org.noixdecoco.app.service.CoconutService;
import org.noixdecoco.app.service.SlackService;
import org.noixdecoco.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class CoconutCommand {

    private static final Logger LOGGER = LogManager.getLogger(CoconutCommand.class);

    @Autowired
    protected CoconutService coconutService;

    @Autowired
    protected SlackService slackService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected CoconutLedgerRepository ledgerRepo;

    protected String userId;

    protected CoconutCommand(String userId) {
        this.userId = userId;
    }

    public void execute() {
        LOGGER.debug("Executing " + this.getClass().getName());
        if (!this.getClass().getAnnotation(Command.class).adminOnly() || userService.isAdmin(userId) && validate()) {
            performAction();
        }
    }

    protected abstract boolean validate();

    protected abstract void performAction();
}
