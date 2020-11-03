package com.smalaca.taskamanager.model.embedded;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class StakeholderTest {
    private static final String FIRST_NAME = "Uatu";
    private static final String LAST_NAME = "The Stakeholder";
    private static final String EMAIL_ADDRESS = "uatu.stakeholder@moon.com";
    private static final String PHONE_PREFIX = "1234";
    private static final String PHONE_NUMBER = "567890";

    @Test
    void shouldCreateStakeholder() {
        Stakeholder actual = new Stakeholder();
        actual.setFirstName(FIRST_NAME);
        actual.setLastName(LAST_NAME);
        actual.setEmailAddress(emailAddress(EMAIL_ADDRESS));
        actual.setPhoneNumber(phoneNumber(PHONE_PREFIX, PHONE_NUMBER));

        assertThat(actual.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actual.getLastName()).isEqualTo(LAST_NAME);
        assertThat(actual.getEmailAddress().getEmailAddress()).isEqualTo(EMAIL_ADDRESS);
        assertThat(actual.getPhoneNumber().getPrefix()).isEqualTo(PHONE_PREFIX);
        assertThat(actual.getPhoneNumber().getNumber()).isEqualTo(PHONE_NUMBER);
    }

    @Test
    void shouldBeEqual() {
        Stakeholder actual = stakeholder();

        assertThat(actual.equals(stakeholder())).isTrue();
        assertThat(actual.hashCode()).isEqualTo(stakeholder().hashCode());
    }

    @Test
    void shouldBeEqualWithItself() {
        Stakeholder actual = stakeholder();

        assertThat(actual.equals(actual)).isTrue();
        assertThat(actual.hashCode()).isEqualTo(actual.hashCode());
    }

    @Test
    void shouldNotBeEqualToNull() {
        assertThat(stakeholder().equals(null)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("notEqualStakeholders")
    void shouldNotBeEqual(Object stakeholder) {
        Stakeholder actual = stakeholder();

        assertThat(actual.equals(stakeholder)).isFalse();
        assertThat(actual.hashCode()).isNotEqualTo(stakeholder.hashCode());
    }

    private Stakeholder stakeholder() {
        return stakeholder(FIRST_NAME, LAST_NAME, EMAIL_ADDRESS, PHONE_PREFIX, PHONE_NUMBER);
    }

    private static List<Object> notEqualStakeholders() {
        return asList(
                stakeholder("Name", LAST_NAME, EMAIL_ADDRESS, PHONE_PREFIX, PHONE_NUMBER),
                stakeholder(FIRST_NAME, "it", EMAIL_ADDRESS, PHONE_PREFIX, PHONE_NUMBER),
                stakeholder(FIRST_NAME, LAST_NAME, "however", PHONE_PREFIX, PHONE_NUMBER),
                stakeholder(FIRST_NAME, LAST_NAME, EMAIL_ADDRESS, "you", PHONE_NUMBER),
                stakeholder(FIRST_NAME, LAST_NAME, EMAIL_ADDRESS, PHONE_PREFIX, "want"),
                BigDecimal.valueOf(13));
    }

    private static Stakeholder stakeholder(String firstName, String lastName, String email, String phonePrefix, String phoneNumber) {
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.setFirstName(firstName);
        stakeholder.setLastName(lastName);
        EmailAddress emailAddress = emailAddress(email);
        stakeholder.setEmailAddress(emailAddress);
        stakeholder.setPhoneNumber(phoneNumber(phonePrefix, phoneNumber));

        return stakeholder;
    }

    private static EmailAddress emailAddress(String email) {
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress(email);
        return emailAddress;
    }

    private static PhoneNumber phoneNumber(String prefix, String number) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix(prefix);
        phoneNumber.setNumber(number);
        return phoneNumber;
    }
}