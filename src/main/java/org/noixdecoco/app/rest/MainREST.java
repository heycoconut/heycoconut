package org.noixdecoco.app.rest;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.EvictingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.data.model.CoconutLedger;
import org.noixdecoco.app.dto.MessageDTO;
import org.noixdecoco.app.dto.SlackRequestDTO;
import org.noixdecoco.app.exception.CoconutException;
import org.noixdecoco.app.exception.InsufficientCoconutsException;
import org.noixdecoco.app.exception.InvalidReceiverException;
import org.noixdecoco.app.service.CoconutService;
import org.noixdecoco.app.service.SpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
public class MainREST {

	private static final Logger LOGGER = LogManager.getLogger(MainREST.class);

	@Autowired
	private CoconutService coconutService;
	
	@Autowired
	private SpeechService speechService;

	private EvictingQueue<String> treatedEventIds;

	private static final int EVENT_ID_QUEUE_SIZE = 500;

	public MainREST() {
		treatedEventIds = EvictingQueue.create(EVENT_ID_QUEUE_SIZE);
	}

	@RequestMapping("/health")
	public String health() {
		LOGGER.info("Health check....");
		return "Healthy as can be";
	}

	// Disable viewing of everyone's coconuts
	//@GetMapping("/coconut")
	public Flux<CoconutLedger> getAllCoconutLedgers() {
		LOGGER.info("Getting ledgers");
		return coconutService.getAllLedgers();
	}

	@PostMapping("/event")
	public synchronized Flux<SlackRequestDTO> receiveEvent(@RequestBody SlackRequestDTO request) {
		if(request.getChallenge() != null) {
			LOGGER.info("Getting challenged:" + request.getChallenge());
		} else if(request.getEvent() != null && !treatedEventIds.contains(request.getEvent_id())) {
			treatedEventIds.add(request.getEvent_id());
			LOGGER.info(request.getEvent().toString());
			if(request.getEvent().getText() != null && request.getEvent().getText().contains(":coconut:") && ("channel".equals(request.getEvent().getChannel_type()) || "group".equals(request.getEvent().getChannel_type()))) {
				// Did someone give a coconut??? :O
				
				LOGGER.info(request.getEvent().getUser() + " just gave a coconut!");
				
				String eventText = request.getEvent().getText();
				int numberOfCoconuts = StringUtils.countOccurrencesOf(eventText, ":coconut:");
				if(eventText.contains("<@")) {
					//Text contains a mention of someone or multiple people
					String[] allMentions = eventText.split("<@");
					List<String> names = new ArrayList<>();
					for(int i=1; i<allMentions.length;i++) { // Skip first element in array which doesnt start with @
						names.add(allMentions[i].substring(0, allMentions[i].indexOf('>')));
					}
					if(names.size() > 0) {
						String text = "";
						for(String name : names) {
							try {
								long numCoconuts = coconutService.addCoconut(request.getEvent().getUser(), name, numberOfCoconuts);
								text += "<@"+request.getEvent().getUser()  + "> gave " + numberOfCoconuts +
										" coconut" + (numberOfCoconuts > 1 ? "s":"") + " to <@" + name + ">, " +
										" they now have " + numCoconuts + " coconut" + (numCoconuts > 1 ? "s":"") + ". ";
							}  catch (InsufficientCoconutsException e) {
								text += "<@"+request.getEvent().getUser()  + "> didn't have enough coconuts remaining for <@" + name + "> :sob:"; 
							} catch(InvalidReceiverException e) {
								text += "<@"+request.getEvent().getUser()  + "> tried giving himself a coconut, unfortunately that's illegal :sob: If you ask nicely, maybe someone will give you one!";
							} catch (CoconutException e) {
								text += "Something went wrong. :sad:";
							}
						}
						speechService.sendMessage(request.getEvent().getChannel(), text);
						
						long coconutsRemaining = coconutService.getCoconutsRemaining(request.getEvent().getUser());
						speechService.sendMessage(request.getEvent().getUser(), "You have *" + (coconutsRemaining > 0 ? coconutsRemaining : "no") + "* left to give today.");
						
					}
					
				}
				
			}
		}
		
		return Flux.just(request);
	}

}
