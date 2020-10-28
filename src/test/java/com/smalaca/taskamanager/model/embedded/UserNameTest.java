package com.smalaca.taskamanager.model.embedded;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class UserNameTest {
    private static final String FIRST_NAME = "Steve";
    private static final String LAST_NAME = "Rogers";

    @Test
    void shouldBeEqual() {
        UserName actual = userName();

        assertThat(actual.equals(userName())).isTrue();
        assertThat(actual.hashCode()).isEqualTo(userName().hashCode());
    }

    @Test
    void shouldBeEqualWithItself() {
        UserName actual = userName();

        assertThat(actual.equals(actual)).isTrue();
        assertThat(actual.hashCode()).isEqualTo(actual.hashCode());
    }

    @Test
    void shouldNotBeEqualToNull() {
        assertThat(userName().equals(null)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("notEqualUserNames")
    void shouldNotBeEqual(Object userNames) {
        UserName actual = userName();

        assertThat(actual.equals(userNames)).isFalse();
        assertThat(actual.hashCode()).isNotEqualTo(userNames.hashCode());
    }

    private static List<Object> notEqualUserNames() {
        return asList(userName(FIRST_NAME, "Strange"), userName("Stephen", LAST_NAME), BigDecimal.valueOf(13));
    }

    private UserName userName() {
        return userName(FIRST_NAME, LAST_NAME);
    }

    private static UserName userName(String firstName, String lastName) {
        UserName userName = new UserName();
        userName.setFirstName(firstName);
        userName.setLastName(lastName);
        return userName;
    }
}