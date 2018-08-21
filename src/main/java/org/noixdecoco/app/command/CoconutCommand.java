package org.noixdecoco.app.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.data.repository.CoconutLedgerRepository;
import org.noixdecoco.app.service.CoconutService;
import org.noixdecoco.app.service.SpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class CoconutCommand {

    private static final Logger LOGGER = LogManager.getLogger(CoconutCommand.class);

    @Autowired
    protected CoconutService coconutService;

    @Autowired
    protected SpeechService speechService;

    @Autowired
    protected CoconutLedgerRepository ledgerRepo;

    public void execute() {
        LOGGER.info("Executing " + this.getClass().getName());
        if(validate()) {
            LOGGER.info("Passed validation");
            performAction();
        }
    }

    protected abstract boolean validate();
    protected abstract void performAction();
}
