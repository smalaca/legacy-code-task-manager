package com.smalaca.taskamanager.model.embedded;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Owner {
    @Column(name = "owner_first_name")
    private String firstName;
    @Column(name = "owner_last_name")
    private String lastName;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "prefix", column = @Column(name = "owner_phone_prefix")),
            @AttributeOverride(name = "number", column = @Column(name = "owner_phone_number"))
    })
    private PhoneNumber phoneNumber;
    @Embedded
    @AttributeOverride(name = "emailAddress", column = @Column(name = "owner_email_address"))
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
