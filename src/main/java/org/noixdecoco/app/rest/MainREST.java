package org.noixdecoco.app.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.data.model.CoconutLedger;
import org.noixdecoco.app.dto.MeMessageDTO;
import org.noixdecoco.app.dto.SlackRequestDTO;
import org.noixdecoco.app.exception.CoconutException;
import org.noixdecoco.app.exception.InvalidReceiverException;
import org.noixdecoco.app.service.CoconutService;
import org.noixdecoco.app.service.SpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import reactor.core.publisher.Flux;

@RestController
public class MainREST {

	private static final Logger LOGGER = LogManager.getLogger(MainREST.class);

	@Autowired
	private CoconutService coconutService;
	
	//@Autowired
	//private SpeechService speechService;
	
	@Value("${bot.key}")
	private String botToken;
	
	@Value("${bot.auth.token}")
	private String authToken;

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
	
	@PostMapping("/event")
	public Flux<SlackRequestDTO> challenge(@RequestBody SlackRequestDTO event) {
		if(event.getChallenge() != null) {
			LOGGER.info("Getting challenged:" + event.getChallenge());
		} else if(event.getEvent() != null) {
			LOGGER.info(event.getEvent().toString());
			if(event.getEvent().getText() != null && event.getEvent().getText().contains(":coconut:") && ("channel".equals(event.getEvent().getChannel_type()) || "group".equals(event.getEvent().getChannel_type()))) {
				// Did someone give a coconut??? :O
				LOGGER.info(event.getEvent().getUser() + " just gave a coconut!");
				HttpHeaders headers = new HttpHeaders();
				headers.set("Content-Type", "application/json"); 
				headers.set("Authorization", "Bearer " + authToken); 
				String eventText = event.getEvent().getText();
				if(eventText.contains("<@")) {
					//Text contains a mention of someone or multiple people
					String[] allMentions = eventText.split("<@");
					List<String> names = new ArrayList<>();
					for(int i=1; i<allMentions.length;i++) { // Skip first element in array which doesnt start with @
						names.add(allMentions[i].substring(0, allMentions[i].indexOf('>')));
					}
					if(names.size() > 0) {
						MeMessageDTO message = new MeMessageDTO();
						message.setChannel(event.getEvent().getChannel());
						String text = "<@"+event.getEvent().getUser()  + "> just gave a coconut to ";
						for(String name : names) {
							text += "<@" + name + ">, ";
							try {
								long numCoconuts = coconutService.addCoconut(event.getEvent().getUser(), name, 1);
								text += " they now have " + numCoconuts + " coconut" + (numCoconuts > 1 ? "s":"") + ".";
							} catch (CoconutException e) {
								if (e instanceof InvalidReceiverException) {
									text = "Sorry, you can't give me or yourself coconuts :sob: If you ask nicely, maybe someone will give you one!";
								} else {
									text = "I'm so sorry! Something went wrong with the coconut transfer. Poor coconut :scream:";
								}
							}
						}
						message.setText(text);
						LOGGER.info("Message:" + message);
						HttpEntity<MeMessageDTO> request = new HttpEntity<>(message, headers);
						
						String response = new RestTemplate().postForObject("https://slack.com/api/chat.meMessage", request, String.class);
						
						LOGGER.info("response: " + response);
					}
					
				}
				
			}
		}
		
		return Flux.just(event);
	}

}
