package org.noixdecoco.app.command;

import org.noixdecoco.app.data.repository.CoconutLedgerRepository;
import org.noixdecoco.app.service.CoconutService;
import org.noixdecoco.app.service.SpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class CoconutCommand {

    @Autowired
    protected CoconutService coconutService;

    @Autowired
    protected SpeechService speechService;

    @Autowired
    protected CoconutLedgerRepository ledgerRepo;

    public void execute() {
        if(validate()) {
            performAction();
        }
    }

    protected abstract boolean validate();
    protected abstract void performAction();
}
