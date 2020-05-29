package com.smalaca.taskamanager.domain;

import javax.persistence.Embeddable;

@Embeddable
public class EmailAddress {
    private String emailAddress;

    private EmailAddress() {}

    public EmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
