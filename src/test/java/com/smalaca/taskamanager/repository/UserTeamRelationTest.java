package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.embedded.UserName;
import com.smalaca.taskamanager.model.entities.Team;
import com.smalaca.taskamanager.model.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserTeamRelationTest {
    @Autowired private UserRepository userRepository;
    @Autowired private TeamRepository teamRepository;

    @BeforeEach
    void setUp() {
        teamRepository.saveAll(asList(
                team("Avengers"),
                team("Fantastic Four"),
                team("X-Men"),
                team("Champions")
        ));

        userRepository.saveAll(asList(
                user("Read", "Richards"),
                user("Sue", "Storm"),
                user("Peter", "Parker"),
                user("Charles", "Xavier"),
                user("Tony", "Stark"),
                user("Gwen", "Stacy"),
                user("Miles", "Morales")
        ));
    }

    private Team team(String name) {
        Team team = new Team();
        team.setName(name);
        return team;
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        teamRepository.deleteAll();
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
    @Transactional
    void shouldAssignUsersToTeams() {
        Team avengers = findTeamBy("Avengers");
        Team fantasticFour = findTeamBy("Fantastic Four");
        Team xMen = findTeamBy("X-Men");
        User readRichard = findUserBy("Read", "Richards");
        User sueStorm = findUserBy("Sue", "Storm");
        User peterParker = findUserBy("Peter", "Parker");
        User charlesXavier = findUserBy("Charles", "Xavier");
        User tonyStark = findUserBy("Tony", "Stark");

        avengers.addMember(peterParker);
        peterParker.addToTeam(avengers);
        avengers.addMember(tonyStark);
        tonyStark.addToTeam(avengers);
        fantasticFour.addMember(readRichard);
        readRichard.addToTeam(fantasticFour);
        fantasticFour.addMember(sueStorm);
        sueStorm.addToTeam(fantasticFour);
        fantasticFour.addMember(peterParker);
        peterParker.addToTeam(fantasticFour);
        xMen.addMember(charlesXavier);
        charlesXavier.addToTeam(xMen);

        teamRepository.saveAll(asList(avengers, fantasticFour, xMen));
        userRepository.saveAll(asList(readRichard, sueStorm, charlesXavier, peterParker, tonyStark));

        assertThat(findTeamBy("Avengers").getMembers()).hasSize(2)
                .anyMatch(isUser("Tony", "Stark"))
                .anyMatch(isUser("Peter", "Parker"));
        assertThat(findTeamBy("Fantastic Four").getMembers()).hasSize(3)
                .anyMatch(isUser("Read", "Richards"))
                .anyMatch(isUser("Sue", "Storm"))
                .anyMatch(isUser("Peter", "Parker"));
        assertThat(findTeamBy("X-Men").getMembers()).hasSize(1)
                .anyMatch(isUser("Charles", "Xavier"));
        assertThat(findTeamBy("Champions").getMembers()).isEmpty();

        assertThat(findUserBy("Read", "Richards").getTeams()).hasSize(1)
            .anyMatch(isTeam("Fantastic Four"));
        assertThat(findUserBy("Sue", "Storm").getTeams()).hasSize(1)
            .anyMatch(isTeam("Fantastic Four"));
        assertThat(findUserBy("Peter", "Parker").getTeams()).hasSize(2)
            .anyMatch(isTeam("Avengers"))
            .anyMatch(isTeam("Fantastic Four"));
        assertThat(findUserBy("Charles", "Xavier").getTeams()).hasSize(1)
            .anyMatch(isTeam("X-Men"));
        assertThat(findUserBy("Tony", "Stark").getTeams()).hasSize(1)
            .anyMatch(isTeam("Avengers"));
        assertThat(findUserBy("Gwen", "Stacy").getTeams()).isEmpty();
        assertThat(findUserBy("Miles", "Morales").getTeams()).isEmpty();
    }

    private Predicate<Team> isTeam(String name) {
        return team -> team.getName().equals(name);
    }

    private Predicate<User> isUser(String firstName, String lastName) {
        return user -> user.getUserName().getFirstName().equals(firstName) && user.getUserName().getLastName().equals(lastName);
    }

    private User findUserBy(String firstName, String lastName) {
        return userRepository.findByUserNameFirstNameAndUserNameLastName(firstName, lastName).get();
    }

    private Team findTeamBy(String name) {
        return teamRepository.findByName(name).get();
    }
}
