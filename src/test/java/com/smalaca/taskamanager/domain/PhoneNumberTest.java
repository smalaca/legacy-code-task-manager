package com.smalaca.taskamanager.domain;

import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class PhoneNumberTest {
    private static final String PHONE_NUMBER = "123456789";
    private static final String PHONE_PREFIX = "+48";

    @Test
    void shouldCreatePhoneNumber() {
        String prefix = "+48";
        String number = "123456789";

        PhoneNumber actual = new PhoneNumber();
        actual.setPrefix(prefix);
        actual.setNumber(number);

        assertThat(actual.getPrefix()).isEqualTo(prefix);
        assertThat(actual.getNumber()).isEqualTo(number);
    }

    @Test
    void shouldBeEqual() {
        PhoneNumber actual = phoneNumber();

        assertThat(actual.equals(phoneNumber())).isTrue();
        assertThat(actual.hashCode()).isEqualTo(phoneNumber().hashCode());
    }

    @Test
    void shouldBeEqualWithItself() {
        PhoneNumber actual = phoneNumber();

        assertThat(actual.equals(actual)).isTrue();
        assertThat(actual.hashCode()).isEqualTo(actual.hashCode());
    }

    @Test
    void shouldNotBeEqualToNull() {
        assertThat(phoneNumber().equals(null)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("notEqualPhoneNumbers")
    void shouldNotBeEqual(Object phoneNumbers) {
        PhoneNumber actual = phoneNumber();

        assertThat(actual.equals(phoneNumbers)).isFalse();
        assertThat(actual.hashCode()).isNotEqualTo(phoneNumbers.hashCode());
    }

    private static List<Object> notEqualPhoneNumbers() {
        return asList(phoneNumber(PHONE_PREFIX, "123123213"), phoneNumber("898", PHONE_NUMBER), BigDecimal.valueOf(13));
    }

    private PhoneNumber phoneNumber() {
        return phoneNumber(PHONE_PREFIX, PHONE_NUMBER);
    }

    private static PhoneNumber phoneNumber(String prefix, String number) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix(prefix);
        phoneNumber.setNumber(number);
        return phoneNumber;
    }
}