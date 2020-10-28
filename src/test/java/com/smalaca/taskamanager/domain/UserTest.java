package com.smalaca.taskamanager.domain;

import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.UserName;
import com.smalaca.taskamanager.model.entities.Team;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.model.enums.TeamRole;
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
        UserName userName = new UserName();
        userName.setFirstName("Scott");
        userName.setLastName("Lang");
        user.setUserName(userName);
        user.setTeamRole(TeamRole.DEVELOPER);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix("4NT");
        phoneNumber.setNumber("421399999");
        user.setPhoneNumber(phoneNumber);
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress("antman@avengers.com");
        user.setEmailAddress(emailAddress);

        assertThat(user.getId()).isNull();
        assertThat(user.getLogin()).isEqualTo("antman");
        assertThat(user.getPassword()).isEqualTo("4ntZ__Ru13Z");
        assertThat(user.getUserName().getFirstName()).isEqualTo("Scott");
        assertThat(user.getUserName().getLastName()).isEqualTo("Lang");
        assertThat(user.getTeamRole()).isEqualTo(TeamRole.DEVELOPER);
        assertThat(user.getPhoneNumber().getPrefix()).isEqualTo("4NT");
        assertThat(user.getPhoneNumber().getNumber()).isEqualTo("421399999");
        assertThat(user.getEmailAddress().getEmailAddress()).isEqualTo("antman@avengers.com");
    }

    @Test
    void shouldCreateUserWithTeams() {
        Team team1 = team("Avengers");
        Team team2 = team("X-Men");
        Team team3 = team("X Force");
        User user = new User();

        user.setTeams(asList(team1, team2, team3));

        assertThat(user.getTeams()).containsExactlyInAnyOrder(team1, team2, team3);
    }

    @Test
    void shouldRemoveUserFromTeam() {
        Team team1 = team("Avengers");
        Team team2 = team("X-Men");
        Team team3 = team("X Force");
        User user = new User();
        user.setTeams(asList(team1, team2, team3));

        user.removeFrom(team("X Force"));

        assertThat(user.getTeams()).containsExactlyInAnyOrder(team1, team2);
    }

    @Test
    void shouldRecognizeWhenRemovingNotMemberOfTeam() {
        Team team1 = team("Avengers");
        Team team2 = team("X-Men");
        User user = new User();
        user.setTeams(asList(team1, team2));

        assertThrows(RuntimeException.class, () -> user.removeFrom(team("X Force")));

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
        expected.addToTeam(team("Brotherhood of Mutants"));
        expected.addToTeam(team("X-Men"));

        User actual = user();
        actual.addToTeam(team("Avengers"));

        assertThat(actual.equals(expected)).isTrue();
        assertThat(actual.hashCode()).isEqualTo(expected.hashCode());
    }

    private Team team(String name) {
        Team team = new Team();
        team.setName(name);
        return team;
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
        UserName userName = new UserName();
        userName.setFirstName("Peter");
        userName.setLastName("Parker");
        user.setUserName(userName);
        user.setLogin("spiderman");
        user.setPassword("responsibility");
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress("spider.in.the.web@gmail.com");
        user.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix("000");
        phoneNumber.setNumber("098765432");
        user.setPhoneNumber(phoneNumber);
        user.setTeamRole(TeamRole.TESTER);

        return user;
    }

    private User user() {
        User user = new User();
        UserName userName = new UserName();
        userName.setFirstName("Wanda");
        userName.setLastName("Maximoff");
        user.setUserName(userName);
        user.setLogin("Scarlet Witch");
        user.setPassword("qw3rty");
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress("wanda@gmail.com");
        user.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix("000");
        phoneNumber.setNumber("123456789");
        user.setPhoneNumber(phoneNumber);
        user.setTeamRole(TeamRole.DEVELOPER);

        return user;
    }
}