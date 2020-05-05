package com.smalaca.taskamanager.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneNumberTest {
    @Test
    void shouldCreatePhoneNumber() {
        String prefix = "+48";
        String number = "123456789";

        PhoneNumber actual = new PhoneNumber(prefix, number);

        assertThat(actual.getPrefix()).isEqualTo(prefix);
        assertThat(actual.getNumber()).isEqualTo(number);
    }
}