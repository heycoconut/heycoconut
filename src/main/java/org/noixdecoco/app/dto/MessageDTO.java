package org.noixdecoco.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// The structure of Slack's message request
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDTO implements SlackDTO {
    private String text;
    private String channel;
    private String user;

    @JsonProperty("as_user")
    private Boolean asUser = Boolean.TRUE;

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

    public Boolean getAsUser() {
        return asUser;
    }

    public void setAsUser(Boolean asUser) {
        this.asUser = asUser;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
