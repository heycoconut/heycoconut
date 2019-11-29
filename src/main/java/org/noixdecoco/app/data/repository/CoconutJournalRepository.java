package org.noixdecoco.app.data.repository;

import org.noixdecoco.app.data.model.CoconutJournal;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface CoconutJournalRepository extends ReactiveMongoRepository<CoconutJournal, Long> {
    Flux<CoconutJournal> findByUsername(String username);
    Flux<CoconutJournal> findByUsername(String username, Sort sort);
    Flux<CoconutJournal> findByRecipient(String recipient, Sort sort);
    Flux<CoconutJournal> findAll(Sort sort);
    Flux<CoconutJournal> findByCoconutGivenAtBetween(LocalDateTime start, LocalDateTime end);
    Flux<CoconutJournal> findByChannel(String channel);

    @DeleteQuery(value = "{'username' : $0, 'coconutGivenAt': { $lte : $1} }")
    boolean deleteOlderThan(String username, LocalDateTime date);
}