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
        Team actual = new Team("Avengers");

        assertThat(actual.getName()).isEqualTo("Avengers");
        assertThat(actual.getId()).isNull();
    }

    @Test
    void shouldCreateTeamWithMembers() {
        Team team = new Team("Avengers");

        team.setMembers(asList(user("Tony", "Stark"), user("Steve", "Rogers"), user("Thor", "Odison")));

        assertThat(team.getMembers()).containsExactlyInAnyOrder(user("Tony", "Stark"), user("Steve", "Rogers"), user("Thor", "Odison"));
    }

    @Test
    void shouldRemoveTeamMemberFromTeam() {
        Team team = new Team("Avengers");
        team.setMembers(asList(user("Tony", "Stark"), user("Steve", "Rogers"), user("Thor", "Odison")));

        team.removeMember(user("Steve", "Rogers"));

        assertThat(team.getMembers()).containsExactlyInAnyOrder(user("Tony", "Stark"), user("Thor", "Odison"));
    }

    @Test
    void shouldRecognizeWhenRemovingNotMemberOfTeam() {
        Team team = new Team("Avengers");
        team.setMembers(asList(user("Tony", "Stark"), user("Steve", "Rogers")));

        assertThrows(RuntimeException.class, () -> team.removeMember(user("Thor", "Odison")));

        assertThat(team.getMembers()).containsExactlyInAnyOrder(user("Tony", "Stark"), user("Steve", "Rogers"));
    }

    @Test
    void shouldBeEqual() {
        Team actual = team();

        assertThat(actual.equals(team())).isTrue();
        assertThat(actual.hashCode()).isEqualTo(team().hashCode());
    }

    @Test
    void shouldBeEqualWithItself() {
        Team actual = team();

        assertThat(actual.equals(actual)).isTrue();
        assertThat(actual.hashCode()).isEqualTo(actual.hashCode());
    }

    @Test
    void shouldBeEqualToTeamWithDifferentTeamMembers() {
        Team expected = team();
        expected.addMember(user("Scott", "Summers"));
        expected.addMember(user("Jean", "Grey"));

        Team actual = team();
        actual.addMember(user("Peter", "Parker"));

        assertThat(actual.equals(expected)).isTrue();
        assertThat(actual.hashCode()).isEqualTo(expected.hashCode());
    }

    @Test
    void shouldNotBeEqualToNull() {
        assertThat(team().equals(null)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("notEqualTeams")
    void shouldNotBeEqual(Object team) {
        Team actual = team();

        assertThat(actual.equals(team)).isFalse();
        assertThat(actual.hashCode()).isNotEqualTo(team.hashCode());
    }

    private static List<Object> notEqualTeams() {
        return asList(new Team("X-Men"), BigDecimal.valueOf(13));
    }

    private Team team() {
        return new Team("Avengers");
    }

    private User user(String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }
}