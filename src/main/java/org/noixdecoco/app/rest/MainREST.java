package org.noixdecoco.app.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.noixdecoco.app.data.model.Person;
import org.noixdecoco.app.data.repository.PersonRepository;

import reactor.core.publisher.Flux;

@RestController
public class MainREST {

	private static final Logger LOGGER = LogManager.getLogger(MainREST.class);

	@Autowired
	private PersonRepository personRepository;

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
	
	public Flux<Void> challenge(@RequestParam(name="challenge")String challenge) {
		LOGGER.info("Getting challenged:" + challenge);
		return Flux.empty();
	}

}
