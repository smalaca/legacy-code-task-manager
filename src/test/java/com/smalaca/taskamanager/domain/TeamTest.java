package com.smalaca.taskamanager.domain;

import org.junit.jupiter.api.Test;

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

    private User user(String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }
}