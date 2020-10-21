package com.smalaca.taskamanager.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TeamTest {
    @Test
    void shouldCreateTeam() {
        Team actual = new Team();
        actual.setName("Avengers");

        assertThat(actual.getName()).isEqualTo("Avengers");
        assertThat(actual.getId()).isNull();
    }

    @Test
    void shouldCreateTeamWithMembers() {
        Team team = new Team();

        team.setMembers(asList(user("Tony", "Stark"), user("Steve", "Rogers"), user("Thor", "Odison")));

        assertThat(team.getMembers()).containsExactlyInAnyOrder(user("Tony", "Stark"), user("Steve", "Rogers"), user("Thor", "Odison"));
    }

    @Test
    void shouldRemoveTeamMemberFromTeam() {
        Team team = new Team();
        team.setMembers(asList(user("Tony", "Stark"), user("Steve", "Rogers"), user("Thor", "Odison")));

        team.removeMember(user("Steve", "Rogers"));

        assertThat(team.getMembers()).containsExactlyInAnyOrder(user("Tony", "Stark"), user("Thor", "Odison"));
    }

    @Test
    void shouldRecognizeWhenRemovingNotMemberOfTeam() {
        Team team = new Team();
        team.setMembers(asList(user("Tony", "Stark"), user("Steve", "Rogers")));

        assertThrows(RuntimeException.class, () -> team.removeMember(user("Thor", "Odison")));

        assertThat(team.getMembers()).containsExactlyInAnyOrder(user("Tony", "Stark"), user("Steve", "Rogers"));
    }

    @Test
    void shouldBeEqual() {
        Team actual = new Team();

        assertThat(actual.equals(new Team())).isTrue();
        assertThat(actual.hashCode()).isEqualTo(new Team().hashCode());
    }

    @Test
    void shouldBeEqualWithItself() {
        Team actual = new Team();

        assertThat(actual.equals(actual)).isTrue();
        assertThat(actual.hashCode()).isEqualTo(actual.hashCode());
    }

    @Test
    void shouldBeEqualToTeamWithDifferentTeamMembers() {
        Team expected = new Team();
        expected.addMember(user("Scott", "Summers"));
        expected.addMember(user("Jean", "Grey"));

        Team actual = new Team();
        actual.addMember(user("Peter", "Parker"));

        assertThat(actual.equals(expected)).isTrue();
        assertThat(actual.hashCode()).isEqualTo(expected.hashCode());
    }

    @Test
    void shouldNotBeEqualToNull() {
        assertThat(new Team().equals(null)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("notEqualTeams")
    void shouldNotBeEqual(Object team) {
        Team actual = new Team();

        assertThat(actual.equals(team)).isFalse();
        assertThat(actual.hashCode()).isNotEqualTo(team.hashCode());
    }

    private static List<Object> notEqualTeams() {
        Team team = new Team();
        team.setName("X-Men");

        return asList(team, BigDecimal.valueOf(13));
    }

    private User user(String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }
}