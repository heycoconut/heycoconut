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

    public static EventType fromString(String value) {
        for (EventType type : EventType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
