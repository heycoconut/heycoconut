package org.noixdecoco.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ChannelDTO implements SlackDTO {
    private String id;
    private String name;
    @JsonProperty("name_normalized")
    private String normalizedName;
    @JsonProperty("is_channel")
    private Boolean isChannel;
    @JsonProperty("is_general")
    private Boolean isGeneral;
    @JsonProperty("is_member")
    private Boolean isMember;
    private List<String> members;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    public Boolean getChannel() {
        return isChannel;
    }

    public void setChannel(Boolean channel) {
        isChannel = channel;
    }

    public Boolean getGeneral() {
        return isGeneral;
    }

    public void setGeneral(Boolean general) {
        isGeneral = general;
    }

    public Boolean getMember() {
        return isMember;
    }

    public void setMember(Boolean member) {
        isMember = member;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
