package org.noixdecoco.app.dto;

public class SlackRequestDTO {
	
	private String token;
	private String challenge;
	private String type;
	private EventDTO event;
	private String event_id;
	
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

	public String getEvent_id() {
		return event_id;
	}

	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}

	@Override
	public String toString() {
		return "Event_id=" + event_id + ": " + event.toString();
	}
}
