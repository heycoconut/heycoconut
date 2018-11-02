package org.noixdecoco.app.command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GlobalConfig {
    public static String COCONUT_EMOJI;
    public static String emoji;

    @Value("${emoji}")
    public void setEmoji(String emoji) {
        this.emoji = emoji;
        this.COCONUT_EMOJI = ":" + emoji + ":";
    }
}
