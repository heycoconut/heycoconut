package org.noixdecoco.app.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.EvictingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.command.CoconutCommand;
import org.noixdecoco.app.command.helper.CoconutCommandHelper;
import org.noixdecoco.app.dto.SlackRequestDTO;
import org.noixdecoco.app.utils.SlackSignatureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletResponse;

@RestController
public class MainREST {

	private static final Logger LOGGER = LogManager.getLogger(MainREST.class);

	@Autowired
	private CoconutCommandHelper commandHelper;

	@Autowired
	private SlackSignatureUtil signatureUtil;

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
	public synchronized Flux<SlackRequestDTO> receiveEvent(@RequestHeader HttpHeaders headers, @RequestBody String bodyString, HttpServletResponse response) {
		SlackRequestDTO request = extractRequestFromBody(bodyString);
		if (request == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return Flux.empty();
		}
		if (request.getChallenge() != null) {
			LOGGER.info("Getting challenged:" + request.getChallenge());
		} else if (request.getEvent() != null && !treatedEventIds.contains(request.getEventId()) && signatureUtil.signatureIsValid(headers, bodyString)) {
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

	private SlackRequestDTO extractRequestFromBody(String body) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(body, SlackRequestDTO.class);
		} catch (Exception e) {
			LOGGER.error("Failed to map request to SlackRequestDTO", e);
		}
		return null;
	}

}
