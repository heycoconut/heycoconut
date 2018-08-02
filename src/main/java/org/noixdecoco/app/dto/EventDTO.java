package org.noixdecoco.app.dto;

public class EventDTO {
	
	private String type;
	private String channel;
	private String user;
	private String text;
	private String ts;
	private String event_ts; //TODO use camel case and specify JSON name
	private String channel_type; //TODO use camel case and specify JSON name

	public EventDTO() {
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getEvent_ts() {
		return event_ts;
	}

	public void setEvent_ts(String event_ts) {
		this.event_ts = event_ts;
	}

	public String getChannel_type() {
		return channel_type;
	}

	public void setChannel_type(String channel_type) {
		this.channel_type = channel_type;
	}
	
	@Override
	public String toString() {
		return "EventDTO:\n" +
				"type: " + type +
				"\nchannel: " + channel +
				"\nuser: " + user +
				"\nts: " + ts +
				"\nevent_ts: " + event_ts +
				"\nchannel_type: " + channel;
	}
	
}
