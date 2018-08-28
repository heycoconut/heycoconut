package org.noixdecoco.app.dto;

public enum EventType {

    REACTION_ADDED("reaction_added"),
    MESSAGE("message"),
    APP_MENTION("app_mention"),
    MEMBER_JOINED_CHANNEL("member_joined_channel");

    private String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
