package com.smalaca.taskamanager.model.embedded;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class WatcherTest {
    private static final String FIRST_NAME = "Uatu";
    private static final String LAST_NAME = "The Watcher";
    private static final String EMAIL_ADDRESS = "uatu.watcher@moon.com";
    private static final String PHONE_PREFIX = "1234";
    private static final String PHONE_NUMBER = "567890";

    @Test
    void shouldCreateWatcher() {
        Watcher actual = new Watcher();
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
        Watcher actual = watcher();

        assertThat(actual.equals(watcher())).isTrue();
        assertThat(actual.hashCode()).isEqualTo(watcher().hashCode());
    }

    @Test
    void shouldBeEqualWithItself() {
        Watcher actual = watcher();

        assertThat(actual.equals(actual)).isTrue();
        assertThat(actual.hashCode()).isEqualTo(actual.hashCode());
    }

    @Test
    void shouldNotBeEqualToNull() {
        assertThat(watcher().equals(null)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("notEqualWatchers")
    void shouldNotBeEqual(Object watcher) {
        Watcher actual = watcher();

        assertThat(actual.equals(watcher)).isFalse();
        assertThat(actual.hashCode()).isNotEqualTo(watcher.hashCode());
    }

    private Watcher watcher() {
        return watcher(FIRST_NAME, LAST_NAME, EMAIL_ADDRESS, PHONE_PREFIX, PHONE_NUMBER);
    }

    private static List<Object> notEqualWatchers() {
        return asList(
                watcher("Name", LAST_NAME, EMAIL_ADDRESS, PHONE_PREFIX, PHONE_NUMBER),
                watcher(FIRST_NAME, "it", EMAIL_ADDRESS, PHONE_PREFIX, PHONE_NUMBER),
                watcher(FIRST_NAME, LAST_NAME, "however", PHONE_PREFIX, PHONE_NUMBER),
                watcher(FIRST_NAME, LAST_NAME, EMAIL_ADDRESS, "you", PHONE_NUMBER),
                watcher(FIRST_NAME, LAST_NAME, EMAIL_ADDRESS, PHONE_PREFIX, "want"),
                BigDecimal.valueOf(13));
    }

    private static Watcher watcher(String firstName, String lastName, String email, String phonePrefix, String phoneNumber) {
        Watcher watcher = new Watcher();
        watcher.setFirstName(firstName);
        watcher.setLastName(lastName);
        EmailAddress emailAddress = emailAddress(email);
        watcher.setEmailAddress(emailAddress);
        watcher.setPhoneNumber(phoneNumber(phonePrefix, phoneNumber));

        return watcher;
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