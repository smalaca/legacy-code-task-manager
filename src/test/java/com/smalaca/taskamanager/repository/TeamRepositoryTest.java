package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.embedded.Codename;
import com.smalaca.taskamanager.model.entities.Team;
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

    private Team team(String name) {
        Team team = new Team();
        team.setName(name);
        return team;
    }

    @Test
    void shouldFindTeamByName() {
        teamRepository.saveAll(asList(
                team("Avengers", "A", "Mighty", "description is too long to be written"),
                team("X-Men", "X", "XM", "there is no description good enough"),
                team("Champions", "CH", "CHAMP", "you cannot be better")
        ));

        Team actual = teamRepository.findByName("X-Men").get();

        assertThat(actual.getName()).isEqualTo("X-Men");
        assertThat(actual.getCodename().getShortName()).isEqualTo("X");
        assertThat(actual.getCodename().getFullName()).isEqualTo("XM");
        assertThat(actual.getDescription()).isEqualTo("there is no description good enough");
    }

    private Team team(String name, String codenameShort, String codenameFull, String description) {
        Team team = new Team();
        team.setName(name);
        Codename codename = new Codename();
        codename.setShortName(codenameShort);
        codename.setFullName(codenameFull);
        team.setCodename(codename);
        team.setDescription(description);
        return team;
    }

    private Predicate<Team> hasNameEqualTo(String name) {
        return team -> team.getName().equals(name);
    }
}