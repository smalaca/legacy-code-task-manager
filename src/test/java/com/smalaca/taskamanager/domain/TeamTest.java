package com.smalaca.taskamanager.domain;

import com.smalaca.taskamanager.model.embedded.Codename;
import com.smalaca.taskamanager.model.embedded.UserName;
import com.smalaca.taskamanager.model.entities.Team;
import com.smalaca.taskamanager.model.entities.User;
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
        Codename codename = new Codename();
        codename.setShortName("A");
        codename.setFullName("Mighty Avengers");
        actual.setCodename(codename);
        actual.setDescription("Some fancy description");

        assertThat(actual.getName()).isEqualTo("Avengers");
        assertThat(actual.getCodename().getShortName()).isEqualTo("A");
        assertThat(actual.getCodename().getFullName()).isEqualTo("Mighty Avengers");
        assertThat(actual.getDescription()).isEqualTo("Some fancy description");
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

    private User user(String firstName, String lastName) {
        User user = new User();
        UserName userName = new UserName();
        userName.setFirstName(firstName);
        userName.setLastName(lastName);
        user.setUserName(userName);
        return user;
    }

    @Test
    void shouldNotBeEqualToNull() {
        assertThat(new Team().equals(null)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("notEqualTeams")
    void shouldNotBeEqual(Object team) {
        Team actual = new Team();
        actual.setName("Dream Team");

        assertThat(actual.equals(team)).isFalse();
        assertThat(actual.hashCode()).isNotEqualTo(team.hashCode());
    }

    private static List<Object> notEqualTeams() {
        return asList(differentTeam(), new Team(), BigDecimal.valueOf(13));
    }

    private static Team differentTeam() {
        Team team = new Team();
        team.setName("X-Men");
        Codename codename = new Codename();
        codename.setShortName("X");
        codename.setFullName("XM");
        team.setCodename(codename);
        team.setDescription("Mutants");
        return team;
    }
}