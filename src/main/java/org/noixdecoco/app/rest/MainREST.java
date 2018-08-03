package org.noixdecoco.app.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.noixdecoco.app.data.model.Person;
import org.noixdecoco.app.data.repository.PersonRepository;
import org.noixdecoco.app.dto.SlackRequestDTO;

import reactor.core.publisher.Flux;

@RestController
public class MainREST {

	private static final Logger LOGGER = LogManager.getLogger(MainREST.class);

	@Autowired
	private PersonRepository personRepository;
	
	@Value("bot.token")
	private String botToken;
	
	@Value("bot.auth.token")
	private String authToken;

	@RequestMapping("/health")
	public String health() {
		LOGGER.info("Check check....");
		return "I'm alright, how about you?";
	}

	@PostMapping("/person")
	public void insertPerson(@RequestParam("id") Long id) {
		Person p = new Person();
		p.setId(id);
		personRepository.insert(p);
		LOGGER.info("Inserted person:" + id);
	}

	@GetMapping("/person")
	public Flux<Person> getPerson(@RequestParam(name = "id", required = false) Long id) {
		LOGGER.info("Getting person:" + id);
		if (id != null) {
			return personRepository.findById(id).flux();
		} else {
			return personRepository.findAll();
		}

	}
	
	@PostMapping("/event")
	public Flux<SlackRequestDTO> challenge(@RequestBody SlackRequestDTO event) {
		if(event.getChallenge() != null) {
			LOGGER.info("Getting challenged:" + event.getChallenge());
		} else if(event.getEvent() != null) {
			LOGGER.info(event.getEvent().toString());
			if(event.getEvent().getText().contains(":coconut:")) {
				// Did someone give a coconut??? :O
				LOGGER.info("COCONUT TIME!!!!" + event.getEvent().getUser() + " just gave a coconut!");
				HttpHeaders headers = new HttpHeaders();
				headers.set("Content-Type", "application/json"); 
				headers.set("Authorization", "Bearer " + authToken); 
				HttpEntity<String> request = new HttpEntity<>("{'token': '"+botToken+"', 'channel':'"+event.getEvent().getChannel()+"', 'message':'DID SOMEONE SAY COCONUT!?!?!' }", headers);
				
				new RestTemplate().postForObject("https://slack.com/api/chat.meMessage", request, Void.class);
			}
		}
		
		return Flux.just(event);
	}

}
