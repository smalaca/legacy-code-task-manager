package com.smalaca.taskamanager.domain;

import com.smalaca.taskamanager.model.embedded.EmailAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class EmailAddressTest {
    @Test
    void shouldCreateEmailAddress() {
        String emailAddress = "dummy@fake.domain.com";

        EmailAddress actual = new EmailAddress();
        actual.setEmailAddress(emailAddress);

        assertThat(actual.getEmailAddress()).isEqualTo(emailAddress);
    }

    @Test
    void shouldBeEqual() {
        EmailAddress actual = emailAddress();

        assertThat(actual.equals(emailAddress())).isTrue();
        assertThat(actual.hashCode()).isEqualTo(emailAddress().hashCode());
    }

    @Test
    void shouldBeEqualWithItself() {
        EmailAddress actual = emailAddress();

        assertThat(actual.equals(actual)).isTrue();
        assertThat(actual.hashCode()).isEqualTo(actual.hashCode());
    }

    @Test
    void shouldNotBeEqualToNull() {
        assertThat(emailAddress().equals(null)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("notEqualEmailAddresses")
    void shouldNotBeEqual(Object emailAddress) {
        EmailAddress actual = emailAddress();

        assertThat(actual.equals(emailAddress)).isFalse();
        assertThat(actual.hashCode()).isNotEqualTo(emailAddress.hashCode());
    }

    private static List<Object> notEqualEmailAddresses() {
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress("natasha.romanov@avengers.com");

        return asList(emailAddress, BigDecimal.valueOf(13));
    }

    private EmailAddress emailAddress() {
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress("tony.stark@avengers.com");
        return emailAddress;
    }
}