package org.noixdecoco.app.data.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import org.noixdecoco.app.data.model.CoconutLedger;

@Repository
public interface CoconutLedgerRepository extends ReactiveMongoRepository<CoconutLedger, Long> {

}
