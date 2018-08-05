package org.noixdecoco.app.data.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "coconutledgers")
public class CoconutLedger {
	
	private String username; // Owner of this ledger
	
	private Long numberOfCoconuts; // Number of coconuts received total
	
	public CoconutLedger() {
		
	}
	
	public CoconutLedger(String username) {
		this.username = username;
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

}
