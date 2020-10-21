package com.smalaca.taskamanager.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TeamRepositoryTest {
    @Autowired private TeamRepository teamRepository;

    @AfterEach
    void tearDown() {
        teamRepository.deleteAll();
    }

    @Test
    void shouldChangeNameOfSpecificTeam() {
        Team saved = teamRepository.save(team("Fantastic Four"));
        String name = "Champions";
        teamRepository.saveAll(asList(
                team("Avengers"),
                team("X-Men"),
                team(name)
        ));
        Team found = teamRepository.findById(saved.getId()).get();
        found.setName("FF");
        teamRepository.save(found);

        Iterable<Team> actual = teamRepository.findAll();

        assertThat(actual).hasSize(4)
                .anyMatch(hasNameEqualTo("FF"))
                .anyMatch(hasNameEqualTo("Avengers"))
                .anyMatch(hasNameEqualTo("X-Men"))
                .anyMatch(hasNameEqualTo("Champions"));
    }

    @Test
    void shouldFindTeamByName() {
        teamRepository.saveAll(asList(
                team("Avengers"),
                team("X-Men"),
                team("Champions")
        ));

        Team actual = teamRepository.findByName("X-Men").get();

        assertThat(actual.getName()).isEqualTo("X-Men");
    }

    private Team team(String name) {
        Team team = new Team();
        team.setName(name);
        return team;
    }

    private Predicate<Team> hasNameEqualTo(String name) {
        return team -> team.getName().equals(name);
    }
}