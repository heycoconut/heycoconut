package org.noixdecoco.app.data.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.alec.app.data.model.Person;

@Repository
public interface PersonRepository extends ReactiveMongoRepository<Person, Long> {

}
