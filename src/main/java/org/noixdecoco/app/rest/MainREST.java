package org.noixdecoco.app.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.noixdecoco.app.data.model.CoconutLedger;
import org.noixdecoco.app.data.repository.CoconutLedgerRepository;
import org.noixdecoco.app.dto.SlackRequestDTO;

import reactor.core.publisher.Flux;

@RestController
public class MainREST {

	private static final Logger LOGGER = LogManager.getLogger(MainREST.class);

	@Autowired
	private CoconutLedgerRepository coconutRepo;
	
	@Value("${bot.key}")
	private String botToken;
	
	@Value("${bot.auth.token}")
	private String authToken;

	@RequestMapping("/health")
	public String health() {
		LOGGER.info("Check check....");
		return "I'm alright, how about you?";
	}

	//@PostMapping("/coconuts")
	public void insertPerson(@RequestParam("id") Long id) {
		CoconutLedger p = new CoconutLedger();
		p.setId(id);
		coconutRepo.insert(p);
		LOGGER.info("Inserted person:" + id);
	}

	@GetMapping("/coconut")
	public Flux<CoconutLedger> getPerson(@RequestParam(name = "id", required = false) Long id) {
		LOGGER.info("Getting person:" + id);
		if (id != null) {
			return coconutRepo.findById(id).flux();
		} else {
			return coconutRepo.findAll();
		}

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
				String text = event.getEvent().getText();
				if(text.contains("@")) {
					//Text contains a mention of someone or multiple people
					String[] allMentions = text.split("@");
					List<String> names = new ArrayList<>();
					for(int i=1; i<allMentions.length;i++) { // Skip first element in array which doesnt start with @
						names.add(allMentions[i].substring(0, allMentions[i].indexOf(' ')-1));
					}
					if(names.size() > 0) {
						String data = "{ \"channel\":\""+event.getEvent().getChannel()+"\", \"text\": \"DID <@"+event.getEvent().getUser()  + "> just give coconuts to ";
						for(String name : names) {
							data += "<@" + name + "> ";
							coconutRepo.findOne(Example.of(new CoconutLedger(name))).subscribe(
									value -> value.setNumberOfCoconuts(value.getNumberOfCoconuts()+1),
									error -> LOGGER.error(error),
									() -> {
										CoconutLedger ledger = new CoconutLedger(name);
										ledger.setNumberOfCoconuts(1);
										coconutRepo.insert(ledger); 
									});
						}
						data += "? \"}";
						LOGGER.info("Data:" + data);
						HttpEntity<String> request = new HttpEntity<>(data, headers);
						
						String response = new RestTemplate().postForObject("https://slack.com/api/chat.meMessage", request, String.class);
						
						LOGGER.info("response: " + response);
					}
					
				}
				
			}
		}
		
		return Flux.just(event);
	}

}
