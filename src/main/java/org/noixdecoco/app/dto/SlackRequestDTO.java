package org.noixdecoco.app.dto;

public class SlackRequestDTO {
	
	private String token;
	private String challenge;
	private String type;
	private EventDTO event;
	
	public SlackRequestDTO() {
		
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getChallenge() {
		return challenge;
	}

	public void setChallenge(String challenge) {
		this.challenge = challenge;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public EventDTO getEvent() {
		return event;
	}

	public void setEvent(EventDTO event) {
		this.event = event;
	}
	
	@Override
	public String toString() {
		return event.toString();
	}
}
