package org.noixdecoco.app.data.facts;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "coconut")
public class CoconutFacts {

    private List<String> facts;

    public CoconutFacts() {
        this.facts = new ArrayList<>();
    }

    public List<String> getFacts() {
        return this.facts;
    }
}
