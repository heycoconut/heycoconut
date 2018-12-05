package org.noixdecoco.app.service;

import org.noixdecoco.app.data.model.CoconutLedger;
import org.noixdecoco.app.exception.CoconutException;
import reactor.core.publisher.Flux;

public interface CoconutService {
    long giveCoconut(String fromUser, String toUser, int numCoconuts, String channel) throws CoconutException;

    void addCoconut(String toUser, int numCoconuts);

    Flux<CoconutLedger> getAllLedgers();

    long getCoconutsRemaining(String user);
}
