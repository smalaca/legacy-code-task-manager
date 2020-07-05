package com.smalaca.taskamanager.domain;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

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
        User user1 = user("Tony", "Stark");
        User user2 = user("Steve", "Rogers");
        User user3 = user("Thor", "Odison");

        team.setMembers(asList(user1, user2, user3));

        assertThat(team.getMembers()).containsExactlyInAnyOrder(user1, user2, user3);
    }

    private User user(String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }
}