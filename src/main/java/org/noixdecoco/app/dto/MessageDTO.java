package org.noixdecoco.app.dto;

// The structure of Slack's meMessage request
public class MessageDTO {
	private String text;
	private String channel;
	private Boolean as_user = Boolean.TRUE;

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

	public Boolean getAs_user() {
		return as_user;
	}

	public void setAs_user(Boolean as_user) {
		this.as_user = as_user;
	}
	
}
