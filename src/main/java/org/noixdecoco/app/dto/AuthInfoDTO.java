package org.noixdecoco.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthInfoDTO {

    private Boolean ok;
    private String url;
    private String user;
    @JsonProperty("team_id")
    private String teamId;
    @JsonProperty("user_id")
    private String userId;

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
