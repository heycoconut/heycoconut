package org.noixdecoco.app.data.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "coconutledgers")
public class CoconutLedger {
	
	private String username; // Owner of this ledger
	
	private long numberOfCoconuts; // Number of coconuts received total
	
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

	public long getNumberOfCoconuts() {
		return numberOfCoconuts;
	}

	public void setNumberOfCoconuts(long numberOfCoconuts) {
		this.numberOfCoconuts = numberOfCoconuts;
	}
	
	public String toString() {
		return "CoconutLedger: [username=" + username + ", coconuts=" + numberOfCoconuts + "]";
	}

}
