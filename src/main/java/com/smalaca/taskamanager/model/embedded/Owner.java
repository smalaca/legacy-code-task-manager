package com.smalaca.taskamanager.model.embedded;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Owner {
    private String firstName;
    private String lastName;
    @Embedded
    private PhoneNumber phoneNumber;
    @Embedded
    private EmailAddress emailAddress;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(EmailAddress emailAddress) {
        this.emailAddress = emailAddress;
    }
}
