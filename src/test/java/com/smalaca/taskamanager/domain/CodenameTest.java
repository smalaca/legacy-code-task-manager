package com.smalaca.taskamanager.domain;

import com.smalaca.taskamanager.model.embedded.Codename;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class CodenameTest {
    private static final String SHORT_NAME = "FF";
    private static final String FULL_NAME = "Fantastic 4";

    @Test
    void shouldBeEqualWithInstanceWithTheSameValues() {
        Codename actual = codename();

        assertThat(actual.equals(codename())).isTrue();
        assertThat(actual.hashCode()).isEqualTo(codename().hashCode());
    }

    @Test
    void shouldBeEqualWithItself() {
        Codename actual = codename();

        assertThat(actual.equals(actual)).isTrue();
        assertThat(actual.hashCode()).isEqualTo(actual.hashCode());
    }

    @Test
    void shouldNotBeEqualToNull() {
        assertThat(codename().equals(null)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("notEqualCodenames")
    void shouldNotBeEqual(Object codename) {
        Codename actual = codename();

        assertThat(actual.equals(codename)).isFalse();
        assertThat(actual.hashCode()).isNotEqualTo(codename.hashCode());
    }

    private static List<Object> notEqualCodenames() {
        return asList(codename(SHORT_NAME, "Fantastic Four"), codename("F4", FULL_NAME), BigDecimal.valueOf(13));
    }

    private Codename codename() {
        return codename(SHORT_NAME, FULL_NAME);
    }

    private static Codename codename(String shortName, String fullName) {
        Codename codename = new Codename();
        codename.setShortName(shortName);
        codename.setFullName(fullName);
        return codename;
    }
}