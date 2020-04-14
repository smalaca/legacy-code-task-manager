package com.smalaca.taskamanager.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TeamTest {

    @Test
    void shouldCreateTeam() {
        Team actual = new Team("Avengers");

        assertThat(actual.getName()).isEqualTo("Avengers");
        assertThat(actual.getId()).isNull();
    }
}