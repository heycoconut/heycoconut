package org.noixdecoco.app.data.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "coconutledgers")
public class CoconutLedger {
	
	@Id
	private ObjectId id;
	
	private String username; // Owner of this ledger
	
	private Integer numberOfCoconuts; // Number of coconuts received total
	
	private Integer coconutsGiven;
	
	private LocalDateTime lastCoconutGivenAt;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getNumberOfCoconuts() {
		return numberOfCoconuts;
	}

	public void setNumberOfCoconuts(Integer numberOfCoconuts) {
		this.numberOfCoconuts = numberOfCoconuts;
	}
	
	public String toString() {
		return "CoconutLedger: [username=" + username + ", coconuts=" + numberOfCoconuts + "]";
	}
	
	public Integer getCoconutsGiven() {
		return coconutsGiven;
	}

	public void setCoconutsGiven(Integer coconutsGiven) {
		this.coconutsGiven = coconutsGiven;
	}

	public LocalDateTime getLastCoconutGivenAt() {
		return lastCoconutGivenAt;
	}

	public void setLastCoconutGivenAt(LocalDateTime lastCoconutGivenAt) {
		this.lastCoconutGivenAt = lastCoconutGivenAt;
	}

	public static CoconutLedger createNew() {
		CoconutLedger ledger = new CoconutLedger();
		ledger.setId(new ObjectId());
		ledger.setCoconutsGiven(0);
		ledger.setNumberOfCoconuts(0);
		return ledger;
	}

}
