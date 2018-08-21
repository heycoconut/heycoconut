package org.noixdecoco.app.data.repository;

import org.noixdecoco.app.data.model.CoconutLedger;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface CoconutLedgerRepository extends ReactiveMongoRepository<CoconutLedger, Long> {
	Flux<CoconutLedger> findByUsername(String username);

	@Query()
	Flux<CoconutLedger> getUsersByTotalCoconutCount(int limit);
}
