package org.noixdecoco.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GroupDTO implements SlackDTO {
    private String id;
    private String name;
    @JsonProperty("is_group")
    private Boolean isGroup;
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

    public Boolean getGroup() {
        return isGroup;
    }

    public void setGroup(Boolean group) {
        isGroup = group;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
