package com.smalaca.taskamanager.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;

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

    @Test
    void shouldBeEqual() {
        User actual = user();

        assertThat(actual.equals(user())).isTrue();
        assertThat(actual.hashCode()).isEqualTo(user().hashCode());
    }

    @Test
    void shouldBeEqualWithItself() {
        User actual = user();

        assertThat(actual.equals(actual)).isTrue();
        assertThat(actual.hashCode()).isEqualTo(actual.hashCode());
    }

    @Test
    void shouldBeEqualToUserWithDifferentTeams() {
        User expected = user();
        expected.addToTeam(new Team("Brotherhood of Mutants"));
        expected.addToTeam(new Team("X-Men"));

        User actual = user();
        actual.addToTeam(new Team("Avengers"));

        assertThat(actual.equals(expected)).isTrue();
        assertThat(actual.hashCode()).isEqualTo(expected.hashCode());
    }

    @Test
    void shouldNotBeEqualToNull() {
        User actual = user();

        assertThat(actual.equals(null)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("notEqualUsers")
    void shouldNotBeEqual(Object user) {
        User actual = user();

        assertThat(actual.equals(user)).isFalse();
        assertThat(actual.hashCode()).isNotEqualTo(user.hashCode());
    }

    private static List<Object> notEqualUsers() {
        return asList(differentUser(), new User(), BigDecimal.valueOf(13));
    }

    private static User differentUser() {
        User user = new User();
        user.setFirstName("Peter");
        user.setLastName("Parker");
        user.setLogin("spiderman");
        user.setPassword("responsibility");
        user.setEmailAddress(new EmailAddress("spider.in.the.web@gmail.com"));
        user.setPhoneNumber(new PhoneNumber("000", "098765432"));
        user.setTeamRole(TeamRole.TESTER);

        return user;
    }

    private User user() {
        User user = new User();
        user.setFirstName("Wanda");
        user.setLastName("Maximoff");
        user.setLogin("Scarlet Witch");
        user.setPassword("qw3rty");
        user.setEmailAddress(new EmailAddress("wanda@gmail.com"));
        user.setPhoneNumber(new PhoneNumber("000", "123456789"));
        user.setTeamRole(TeamRole.DEVELOPER);

        return user;
    }
}