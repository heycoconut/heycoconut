package org.noixdecoco.app.data.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "coconutjournal")
public class CoconutJournal {
    @Id
    private ObjectId id;

    private String username;

    private String recipient;

    private Long coconutsGiven;

    private LocalDateTime coconutGivenAt;

    private String channel;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String toString() {
        return "CoconutJournal: [username=" + username + ", coconuts=" + coconutsGiven + ", date=" + coconutGivenAt + ", recipient=" + recipient + "]";
    }

    public Long getCoconutsGiven() {
        return coconutsGiven;
    }

    public void setCoconutsGiven(Long coconutsGiven) {
        this.coconutsGiven = coconutsGiven;
    }

    public LocalDateTime getCoconutGivenAt() {
        return coconutGivenAt;
    }

    public void setCoconutGivenAt(LocalDateTime coconutGivenAt) {
        this.coconutGivenAt = coconutGivenAt;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public static CoconutJournal createNew() {
        CoconutJournal journal = new CoconutJournal();
        journal.setId(new ObjectId());
        journal.setCoconutsGiven(0l);

        return journal;
    }
}
