package org.noixdecoco.app.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.data.model.CoconutJournal;
import org.noixdecoco.app.data.model.CoconutLedger;
import org.noixdecoco.app.data.repository.CoconutJournalRepository;
import org.noixdecoco.app.data.repository.CoconutLedgerRepository;
import org.noixdecoco.app.exception.CoconutException;
import org.noixdecoco.app.exception.InsufficientCoconutsException;
import org.noixdecoco.app.exception.InvalidReceiverException;
import org.noixdecoco.app.service.CoconutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CoconutServiceImpl implements CoconutService {

    private static final Logger LOGGER = LogManager.getLogger(CoconutService.class);

    @Autowired
    private CoconutLedgerRepository coconutRepo;

    @Autowired
    private CoconutJournalRepository coconutJournalRepo;

    @Value("${daily.coconut.limit}")
    private int dailyLimit;

    @Override
    public long giveCoconut(String fromUser, String toUser, int numCoconuts, String channel) throws CoconutException {
        if (fromUser.equals(toUser)) {
            throw new InvalidReceiverException();
        }

        CoconutLedger giversLedger = getLedger(fromUser);

        if (giversLedger.getCoconutsGiven() + numCoconuts > dailyLimit) {
            throw new InsufficientCoconutsException();
        }

        giversLedger.setCoconutsGiven(giversLedger.getCoconutsGiven() + numCoconuts);
        LocalDateTime now = LocalDateTime.now();
        giversLedger.setLastCoconutGivenAt(now);
        recordTransaction(fromUser, toUser, numCoconuts, channel, now);

        coconutRepo.save(giversLedger).subscribe();

        CoconutLedger receiver = getLedger(toUser);
        receiver.setNumberOfCoconuts(receiver.getNumberOfCoconuts() + numCoconuts);
        LOGGER.info(receiver.getUsername() + " now has " + receiver.getNumberOfCoconuts() + " coconut(s)");
        coconutRepo.save(receiver).subscribe();
        return receiver.getNumberOfCoconuts();
    }

    private CoconutLedger initializeLedger(String userId) {
        CoconutLedger ledger = CoconutLedger.createNew();
        ledger.setUsername(userId);
        ledger.setCoconutsGiven(0);
        ledger.setLastCoconutGivenAt(LocalDateTime.now());
        return ledger;
    }

    @Override
    public void addCoconut(String toUser, int numCoconuts) {
        CoconutLedger ledger = getLedger(toUser);
        ledger.setNumberOfCoconuts(ledger.getNumberOfCoconuts() + numCoconuts);
        LOGGER.info(ledger.getUsername() + " now has " + ledger.getNumberOfCoconuts() + " coconut(s)");
        coconutRepo.save(ledger).subscribe();
    }

    @Override
    public Flux<CoconutLedger> getAllLedgers() {
        return coconutRepo.findAll();
    }

    @Override
    public Flux<CoconutJournal> getAllJournals() {
        return coconutJournalRepo.findAll();
    }

    @Override
    public int getCoconutsRemaining(String user) {
        CoconutLedger ledger = getLedger(user);
        if (ledger != null && ledger.getCoconutsGiven() != null) {
            return dailyLimit - ledger.getCoconutsGiven();
        }
        return dailyLimit;
    }

    public void recordTransaction(String username, String recipient, int numCoconuts, String channel, LocalDateTime date) {
        CoconutJournal journal = CoconutJournal.createNew();
        journal.setUsername(username);
        journal.setRecipient(recipient);
        journal.setCoconutsGiven(Long.valueOf(numCoconuts));
        journal.setCoconutGivenAt(date);
        journal.setChannel(channel);
        coconutJournalRepo.save(journal).subscribe();
    }

    @Override
    public void subtractCoconutsGiven(String toUser, int numCoconuts) {
        CoconutLedger ledger = getLedger(toUser);
        ledger.setCoconutsGiven(ledger.getCoconutsGiven() - numCoconuts);
        coconutRepo.save(ledger).subscribe();
    }

    // IMPORTANT: Use this method to properly reset cocos daily
    private CoconutLedger getLedger(String userId) {
        CoconutLedger ledger = coconutRepo.findByUsername(userId).blockFirst();
        if (ledger == null) {
            ledger = initializeLedger(userId);
            ledger = coconutRepo.insert(ledger).block();
        }
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        if (ledger.getLastCoconutGivenAt() == null || ledger.getLastCoconutGivenAt().isBefore(startOfDay)) {
            ledger.setCoconutsGiven(0);
            ledger.setLastCoconutGivenAt(LocalDateTime.now());
        }
        return ledger;
    }
}
