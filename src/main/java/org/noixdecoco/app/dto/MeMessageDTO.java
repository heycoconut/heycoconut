package org.noixdecoco.app.dto;

// The structure of Slack's meMessage request
public class MeMessageDTO {
	private String text;
	private String channel;
	
	public MeMessageDTO() {
		
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	
}
