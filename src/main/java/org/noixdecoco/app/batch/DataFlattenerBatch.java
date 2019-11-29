package org.noixdecoco.app.batch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.data.model.CoconutJournal;
import org.noixdecoco.app.data.model.CoconutLedger;
import org.noixdecoco.app.data.repository.CoconutJournalRepository;
import org.noixdecoco.app.service.CoconutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DataFlattenerBatch {

    private static final Logger LOGGER = LogManager.getLogger(CoconutService.class);

    @Autowired
    private CoconutService coconutService;

    @Autowired
    private CoconutJournalRepository journalRepo;

    @Value("${flatten.older.than.days}")
    private Integer flattenDataOlderThan;


    //@Scheduled(cron = "0 12 * * *")
    public void flattenOldData() {
        StopWatch watch = new StopWatch();
        watch.start("FlattenOldData");
        LOGGER.info("Flatten Old Data Started!");
        Flux<CoconutLedger> allLedgers = coconutService.getAllLedgers();
        allLedgers.subscribe(ledger -> {

            Flux<CoconutJournal> journals = coconutService.getAllJournalsOlderThanByRecipient(ledger.getUsername(), flattenDataOlderThan);
            final CoconutJournal flattenedJournal = createFlattenedJournal(ledger);

            journals.subscribe(journal -> flattenedJournal.addCoconutsGiven(journal.getCoconutsGiven()),
                    error -> LOGGER.error("Failed to flatten journal for user: " + ledger.getUsername()),
                    () -> {
                        journalRepo.insert(flattenedJournal).block();
                        journalRepo.deleteOlderThan(ledger.getUsername(), LocalDateTime.now().minus(flattenDataOlderThan, ChronoUnit.DAYS);
                        LOGGER.info("Successfully flattened journals for user: " + ledger.getUsername());
                    }
            );

        }, error -> {
          LOGGER.error("Error... abort");
        }, () -> {
            LOGGER.info("Completed!");
        });

        LOGGER.info("Flatten took: " + (watch.getTotalTimeSeconds() > 60 ? (watch.getTotalTimeSeconds() / 60 + " minutes and ") : "") + watch.getTotalTimeSeconds() % 60 + " seconds to run.");
    }

    private CoconutJournal createFlattenedJournal(CoconutLedger ledger) {
        final CoconutJournal flattenedJournal = CoconutJournal.createNew();
        flattenedJournal.setCoconutGivenAt(null);
        flattenedJournal.setChannel(null);
        flattenedJournal.setCoconutsGiven(0l);
        flattenedJournal.setUsername(null);
        flattenedJournal.setRecipient(ledger.getUsername());
        return flattenedJournal;
    }
}
