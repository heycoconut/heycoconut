package org.noixdecoco.app.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.data.model.CoconutLedger;
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
import java.util.Date;
import java.util.List;

@Service
public class CoconutServiceImpl implements CoconutService {
	
	private static final Logger LOGGER = LogManager.getLogger(CoconutService.class);
	
	@Autowired
	private CoconutLedgerRepository coconutRepo;
	
	@Value("${daily.coconut.limit}")
	private long dailyLimit;

	@Override
	public long giveCoconut(String fromUser, String toUser, int numCoconuts) throws CoconutException {
		if(fromUser.equals(toUser)) {
			throw new InvalidReceiverException();
		}
		
		CoconutLedger giversLedger = null;
		LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
		List<CoconutLedger> fromUserLedger = coconutRepo.findByUsername(fromUser).collectList().block();
		if(fromUserLedger.size() > 0) {
			giversLedger = fromUserLedger.get(0);
			if(giversLedger.getLastCoconutGivenAt() == null || giversLedger.getLastCoconutGivenAt().isBefore(startOfDay)) {
				giversLedger.setCoconutsGiven(0l);
				giversLedger.setLastCoconutGivenAt(LocalDateTime.now());
			}
		} else {
			giversLedger = CoconutLedger.createNew();
			giversLedger.setUsername(fromUser);
			giversLedger.setCoconutsGiven(0l);
			giversLedger.setLastCoconutGivenAt(LocalDateTime.now());
		}
		if(numCoconuts > dailyLimit) {
			if(giversLedger.getLastCoconutGivenAt().isAfter(startOfDay)) {
				throw new InsufficientCoconutsException(dailyLimit - giversLedger.getCoconutsGiven());
			} else {
				throw new InsufficientCoconutsException(dailyLimit);
			}
		} else if(giversLedger.getLastCoconutGivenAt().isAfter(startOfDay) &&
				giversLedger.getCoconutsGiven() + numCoconuts > dailyLimit) {
			throw new InsufficientCoconutsException(giversLedger.getCoconutsGiven());
		}
		
		giversLedger.setCoconutsGiven(giversLedger.getCoconutsGiven() + numCoconuts);
		giversLedger.setLastCoconutGivenAt(LocalDateTime.now());
		coconutRepo.save(giversLedger).subscribe();
		
		List<CoconutLedger> ledgers = coconutRepo.findByUsername(toUser).collectList().block();
		if(ledgers.isEmpty()) {
			CoconutLedger ledger = CoconutLedger.createNew();
			ledger.setUsername(toUser);
			ledger.setNumberOfCoconuts(Long.valueOf(numCoconuts));
			coconutRepo.insert(ledger).subscribe((coconut) -> LOGGER.info(coconut)); 
			return numCoconuts;
		} else {
			CoconutLedger ledger = ledgers.get(0);
			ledger.setNumberOfCoconuts(ledger.getNumberOfCoconuts()+numCoconuts);
			LOGGER.info(ledger.getUsername() + " now has " + ledger.getNumberOfCoconuts() + " coconut(s)");
			coconutRepo.save(ledger).subscribe();
			return ledger.getNumberOfCoconuts();
		}
		
	}
	
	public Flux<CoconutLedger> getAllLedgers() {
		return coconutRepo.findAll();
	}

	@Override
	public long getCoconutsRemaining(String user) {
		CoconutLedger ledger = coconutRepo.findByUsername(user).blockFirst();
		if(ledger != null && ledger.getCoconutsGiven() != null) {
			return dailyLimit - ledger.getCoconutsGiven();
		}
		return dailyLimit;
	}

	
}
