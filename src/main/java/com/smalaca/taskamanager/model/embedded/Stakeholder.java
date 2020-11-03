package com.smalaca.taskamanager.model.embedded;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Stakeholder {
    @Embedded private EmailAddress emailAddress;
    @Embedded private PhoneNumber phoneNumber;
    private String firstName;
    private String lastName;

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setEmailAddress(EmailAddress emailAddress) {
        this.emailAddress = emailAddress;
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Stakeholder stakeholder = (Stakeholder) o;

        return new EqualsBuilder()
                .append(firstName, stakeholder.firstName)
                .append(lastName, stakeholder.lastName)
                .append(phoneNumber, stakeholder.phoneNumber)
                .append(emailAddress, stakeholder.emailAddress)
                .isEquals();
    }

    @Override
    @SuppressWarnings("MagicNumber")
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstName)
                .append(lastName)
                .append(phoneNumber)
                .append(emailAddress)
                .toHashCode();
    }
}
