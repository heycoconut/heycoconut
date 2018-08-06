package org.noixdecoco.app.data.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "coconutledgers")
public class CoconutLedger {
	
	@Id
	private ObjectId id;
	
	private String username; // Owner of this ledger
	
	private Long numberOfCoconuts; // Number of coconuts received total
	
	public CoconutLedger() {
		
	}
	
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

	public Long getNumberOfCoconuts() {
		return numberOfCoconuts;
	}

	public void setNumberOfCoconuts(Long numberOfCoconuts) {
		this.numberOfCoconuts = numberOfCoconuts;
	}
	
	public String toString() {
		return "CoconutLedger: [username=" + username + ", coconuts=" + numberOfCoconuts + "]";
	}
	
	public static CoconutLedger createNew() {
		CoconutLedger ledger = new CoconutLedger();
		ledger.setId(new ObjectId());
		return ledger;
	}

}
