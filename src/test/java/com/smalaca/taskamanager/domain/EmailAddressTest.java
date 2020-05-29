package com.smalaca.taskamanager.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailAddressTest {
    @Test
    void shouldCreateEmailAddress() {
        String emailAddress = "dummy@fake.domain.com";

        EmailAddress actual = new EmailAddress(emailAddress);

        assertThat(actual.getEmailAddress()).isEqualTo(emailAddress);
    }
}