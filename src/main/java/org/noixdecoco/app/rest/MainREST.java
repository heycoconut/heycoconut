package org.noixdecoco.app.rest;

import java.util.ArrayList;
import java.util.List;

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

	@RequestMapping("/health")
	public String health() {
		LOGGER.info("Check check....");
		return "I'm alright, how about you?";
	}

	@GetMapping("/coconut")
	public Flux<CoconutLedger> getAllCoconutLedgers() {
		LOGGER.info("Getting ledgers");
		return coconutService.getAllLedgers();
	}
	
	@GetMapping("/secretEndpointReset")
	public Flux<CoconutLedger> resetCoconutLedgers() {
		LOGGER.info("Getting ledgers");
		return coconutService.getAllLedgers();
	}
	
	@PostMapping("/event")
	public Flux<SlackRequestDTO> challenge(@RequestBody SlackRequestDTO event) {
		if(event.getChallenge() != null) {
			LOGGER.info("Getting challenged:" + event.getChallenge());
		} else if(event.getEvent() != null) {
			LOGGER.info(event.getEvent().toString());
			if(event.getEvent().getText() != null && event.getEvent().getText().contains(":coconut:") && ("channel".equals(event.getEvent().getChannel_type()) || "group".equals(event.getEvent().getChannel_type()))) {
				// Did someone give a coconut??? :O
				
				LOGGER.info(event.getEvent().getUser() + " just gave a coconut!");
				
				String eventText = event.getEvent().getText();
				int numberOfCoconuts = eventText.split(":coconut:").length-1;
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
								long numCoconuts = coconutService.addCoconut(event.getEvent().getUser(), name, numberOfCoconuts);
								text += "<@"+event.getEvent().getUser()  + "> gave " + numberOfCoconuts +
										" coconut" + (numberOfCoconuts > 1 ? "s":"") + " to <@" + name + ">, " +
										" they now have " + numCoconuts + " coconut" + (numCoconuts > 1 ? "s":"") + ". ";
							}  catch (InsufficientCoconutsException e) {
								text += "<@"+event.getEvent().getUser()  + "> didn't have enough coconuts remaining for <@" + name + "> :sob:"; 
							} catch(InvalidReceiverException e) {
								text += "<@"+event.getEvent().getUser()  + "> tried giving himself a coconut, unfortunately that's illegal :sob: If you ask nicely, maybe someone will give you one!";
							} catch (CoconutException e) {
								text += "Something went wrong. :sad:";
							}
						}
						speechService.sendMessage(event.getEvent().getChannel(), text);
						
						long coconutsRemaining = coconutService.getCoconutsRemaining(event.getEvent().getUser());
						speechService.sendMessage(event.getEvent().getUser(), "You are so kind to give coconuts! You now have " +coconutsRemaining + " left to give today :+1:");
						
					}
					
				}
				
			}
		}
		
		return Flux.just(event);
	}

}
