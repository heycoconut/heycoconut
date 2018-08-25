package org.noixdecoco.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDTO {
	
	private String type;
	private String channel;
	private String user;
	private String text;
	private String ts;
	private String reaction;

	private ItemDTO item;

	@JsonProperty("item_user")
	private String itemUser;

	@JsonProperty("event_ts")
	private String eventTs;

	@JsonProperty("channel_type")
	private String channelType;



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

	public String getEventTs() {
		return eventTs;
	}

	public void setEventTs(String eventTs) {
		this.eventTs = eventTs;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public String getReaction() {
		return reaction;
	}

	public void setReaction(String reaction) {
		this.reaction = reaction;
	}

	public ItemDTO getItem() {
		return item;
	}

	public void setItem(ItemDTO item) {
		this.item = item;
	}

	public String getItemUser() {
		return itemUser;
	}

	public void setItemUser(String itemUser) {
		this.itemUser = itemUser;
	}

	@Override
	public String toString() {
		return "EventDTO:\n" +
				"type: " + type +
				"\nchannel: " + channel +
				"\nuser: " + user +
				"\nts: " + ts +
				"\nreaction: " + reaction +
				"\nitem_user: " + itemUser +
				"\nitem:" + item +
				"\ntext:" + text +
				"\neventTs: " + eventTs +
				"\nchannelType: " + channelType;
	}
	
}
