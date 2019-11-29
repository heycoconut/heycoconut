package org.noixdecoco.app.service;

import org.noixdecoco.app.data.model.CoconutJournal;
import org.noixdecoco.app.data.model.CoconutLedger;
import org.noixdecoco.app.exception.CoconutException;
import reactor.core.publisher.Flux;

public interface CoconutService {
    long giveCoconut(String fromUser, String toUser, int numCoconuts, String channel) throws CoconutException;

    void addCoconut(String toUser, int numCoconuts);

    void subtractCoconutsGiven(String toUser, int numCoconuts);

    Flux<CoconutLedger> getAllLedgers();

    Flux<CoconutJournal> getAllJournals();

    Flux<CoconutJournal> getAllJournalsOlderThanByRecipient(String userId, int days);

    int getCoconutsRemaining(String user);
}
