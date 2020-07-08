package com.smalaca.taskamanager.domain;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @Test
    void shouldCreateUser() {
        User user = new User();
        user.setLogin("antman");
        user.setPassword("4ntZ__Ru13Z");
        user.setFirstName("Scott");
        user.setLastName("Lang");
        user.setTeamRole(TeamRole.DEVELOPER);
        user.setPhoneNumber(new PhoneNumber("4NT", "421399999"));
        user.setEmailAddress(new EmailAddress("antman@avengers.com"));

        assertThat(user.getId()).isNull();
        assertThat(user.getLogin()).isEqualTo("antman");
        assertThat(user.getPassword()).isEqualTo("4ntZ__Ru13Z");
        assertThat(user.getFirstName()).isEqualTo("Scott");
        assertThat(user.getLastName()).isEqualTo("Lang");
        assertThat(user.getTeamRole()).isEqualTo(TeamRole.DEVELOPER);
        assertThat(user.getPhoneNumber().getPrefix()).isEqualTo("4NT");
        assertThat(user.getPhoneNumber().getNumber()).isEqualTo("421399999");
        assertThat(user.getEmailAddress().getEmailAddress()).isEqualTo("antman@avengers.com");
    }

    @Test
    void shouldCreateUserWithTeams() {
        Team team1 = new Team("Avengers");
        Team team2 = new Team("X-Men");
        Team team3 = new Team("X Force");
        User user = new User();

        user.setTeams(asList(team1, team2, team3));

        assertThat(user.getTeams()).containsExactlyInAnyOrder(team1, team2, team3);
    }

    @Test
    void shouldRemoveUserFromTeam() {
        Team team1 = new Team("Avengers");
        Team team2 = new Team("X-Men");
        Team team3 = new Team("X Force");
        User user = new User();
        user.setTeams(asList(team1, team2, team3));

        user.removeFrom(new Team("X Force"));

        assertThat(user.getTeams()).containsExactlyInAnyOrder(team1, team2);
    }

    @Test
    void shouldRecognizeWhenRemovingNotMemberOfTeam() {
        Team team1 = new Team("Avengers");
        Team team2 = new Team("X-Men");
        User user = new User();
        user.setTeams(asList(team1, team2));

        assertThrows(RuntimeException.class, () -> user.removeFrom(new Team("X Force")));

        assertThat(user.getTeams()).containsExactlyInAnyOrder(team1, team2);
    }
}