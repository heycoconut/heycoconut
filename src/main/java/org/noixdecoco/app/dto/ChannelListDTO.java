package org.noixdecoco.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ChannelListDTO implements SlackDTO {
    private String ok;
    private List<ChannelDTO> channels;

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    public List<ChannelDTO> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelDTO> channels) {
        this.channels = channels;
    }
}
