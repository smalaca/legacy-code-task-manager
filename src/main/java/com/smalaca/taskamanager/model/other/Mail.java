package com.smalaca.taskamanager.model.other;

import com.smalaca.taskamanager.model.embedded.EmailAddress;

public class Mail {
    private EmailAddress from;
    private EmailAddress to;
    private String topic;
    private String content;

    public EmailAddress getFrom() {
        return from;
    }

    public void setFrom(EmailAddress from) {
        this.from = from;
    }

    public EmailAddress getTo() {
        return to;
    }

    public void setTo(EmailAddress to) {
        this.to = to;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
