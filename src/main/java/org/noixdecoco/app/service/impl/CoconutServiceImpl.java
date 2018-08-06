package org.noixdecoco.app.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.data.model.CoconutLedger;
import org.noixdecoco.app.data.repository.CoconutLedgerRepository;
import org.noixdecoco.app.exception.CoconutException;
import org.noixdecoco.app.exception.InvalidReceiverException;
import org.noixdecoco.app.service.CoconutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class CoconutServiceImpl implements CoconutService {
	
	private static final Logger LOGGER = LogManager.getLogger(CoconutService.class);
	
	@Autowired
	private CoconutLedgerRepository coconutRepo;
	
	@Value("${daily.coconut.limit}")
	private int dailyLimit;

	@Override
	public long addCoconut(String fromUser, String toUser, int numCoconuts) throws CoconutException {
		if(fromUser.equals(toUser)) {
			throw new InvalidReceiverException();
		}
		List<CoconutLedger> ledgers = coconutRepo.findByUsername(toUser).collectList().block();
		if(ledgers.isEmpty()) {
			CoconutLedger ledger = CoconutLedger.createNew();
			ledger.setUsername(toUser);
			ledger.setNumberOfCoconuts(Long.valueOf(1));
			coconutRepo.insert(ledger).subscribe((coconut) -> LOGGER.info(coconut)); 
			return 1;
		} else {
			CoconutLedger ledger = ledgers.get(0);
			ledger.setNumberOfCoconuts(ledger.getNumberOfCoconuts()+1);
			LOGGER.info(ledger.getUsername() + " now has " + ledger.getNumberOfCoconuts() + " coconut(s)");
			coconutRepo.save(ledger).subscribe();
			return ledger.getNumberOfCoconuts();
		}
		
	}
	
	public Flux<CoconutLedger> getAllLedgers() {
		return coconutRepo.findAll();
	}

	
}
