package org.noixdecoco.app.rest;

import com.google.common.collect.EvictingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.command.CoconutCommand;
import org.noixdecoco.app.command.helper.CoconutCommandHelper;
import org.noixdecoco.app.data.model.CoconutLedger;
import org.noixdecoco.app.dto.SlackRequestDTO;
import org.noixdecoco.app.exception.CoconutException;
import org.noixdecoco.app.exception.InsufficientCoconutsException;
import org.noixdecoco.app.exception.InvalidReceiverException;
import org.noixdecoco.app.service.CoconutService;
import org.noixdecoco.app.service.SpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MainREST {

	private static final Logger LOGGER = LogManager.getLogger(MainREST.class);

	@Autowired
	private CoconutCommandHelper commandHelper;

	private EvictingQueue<String> treatedEventIds;

	private static final int EVENT_ID_QUEUE_SIZE = 200;

	public MainREST() {
		// Used to prevent events from being processed multiple times
		treatedEventIds = EvictingQueue.create(EVENT_ID_QUEUE_SIZE);
	}

	@RequestMapping("/health")
	public String health() {
		LOGGER.info("Health check....");
		return "Healthy as can be";
	}

	@PostMapping("/event")
	public synchronized Flux<SlackRequestDTO> receiveEvent(@RequestHeader HttpHeaders headers, @RequestBody SlackRequestDTO request) {
		LOGGER.info("Headers: " + headers.toString());
		if(request.getChallenge() != null) {
			LOGGER.info("Getting challenged:" + request.getChallenge());
		} else if(request.getEvent() != null && !treatedEventIds.contains(request.getEventId())) {
			// Add eventId to treatedEventIds to prevent reprocessing
			treatedEventIds.add(request.getEventId());
			LOGGER.info(request.toString());
			CoconutCommand command = commandHelper.buildFromRequest(request);
			if(command != null) {
				command.execute();
			}
		}
		
		return Flux.just(request);
	}

}
