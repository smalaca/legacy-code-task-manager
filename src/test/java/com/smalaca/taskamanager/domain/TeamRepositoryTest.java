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
        Team saved = teamRepository.save(new Team("Fantastic Four"));
        teamRepository.saveAll(asList(
                new Team("Avengers"),
                new Team("X-Men"),
                new Team("Champions")
        ));
        Team found = teamRepository.findById(saved.getId()).get();
        found.setName("FF");
        teamRepository.save(found);

        Iterable<Team> teams = teamRepository.findAll();

        assertThat(teams).hasSize(4)
                .anyMatch(hasNameEqualTo("FF"))
                .anyMatch(hasNameEqualTo("Avengers"))
                .anyMatch(hasNameEqualTo("X-Men"))
                .anyMatch(hasNameEqualTo("Champions"));
    }

    private Predicate<Team> hasNameEqualTo(String name) {
        return team -> team.getName().equals(name);
    }
}