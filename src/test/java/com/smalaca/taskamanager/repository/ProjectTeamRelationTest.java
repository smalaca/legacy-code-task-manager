package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Team;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjectTeamRelationTest {
    private final Map<String, Long> projects = new HashMap<>();

    @Autowired private ProjectRepository projectRepository;
    @Autowired private TeamRepository teamRepository;

    @BeforeEach
    void projectsAndProductOwners() {
        givenProject("Avengers vs. X-Men");
        givenProject("Secret Wars");
        givenProject("Civil War");

        teamRepository.saveAll(asList(
                team("Avengers"),
                team("Fantastic Four"),
                team("X-Men"),
                team("Champions"),
                team("X-Force"),
                team("Young Avengers"),
                team("Defenders")
        ));
    }

    private void givenProject(String name) {
        Project project = project(name);
        Long id = projectRepository.save(project).getId();

        projects.put(name, id);
    }

    private Project project(String name) {
        Project project = new Project();
        project.setName(name);
        return project;
    }

    private Team team(String name) {
        Team team = new Team();
        team.setName(name);
        return team;
    }

    @AfterEach
    void removeAll() {
        projectRepository.deleteAll();
        teamRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldAssignProductOwnerToProject() {
        Project avengersVsXmen = findProjectByName("Avengers vs. X-Men");
        Project secretWars = findProjectByName("Secret Wars");
        Project civilWar = findProjectByName("Civil War");

        Team avengers = findTeamBy("Avengers");
        Team fantasticFour = findTeamBy("Fantastic Four");
        Team xMen = findTeamBy("X-Men");
        Team champions = findTeamBy("Champions");
        Team xForce = findTeamBy("X-Force");
        Team youngAvengers = findTeamBy("Young Avengers");
        Team defenders = findTeamBy("Defenders");

        avengersVsXmen.addTeam(avengers);
        avengersVsXmen.addTeam(fantasticFour);
        avengersVsXmen.addTeam(xMen);
        avengersVsXmen.addTeam(champions);
        avengers.setProject(avengersVsXmen);
        fantasticFour.setProject(avengersVsXmen);
        xMen.setProject(avengersVsXmen);
        champions.setProject(avengersVsXmen);

        secretWars.addTeam(xForce);
        secretWars.addTeam(youngAvengers);
        xForce.setProject(secretWars);
        youngAvengers.setProject(secretWars);

        civilWar.addTeam(defenders);
        defenders.setProject(civilWar);

        projectRepository.saveAll(asList(avengersVsXmen, secretWars, civilWar));
        teamRepository.saveAll(asList(avengers, fantasticFour, xMen, champions, xForce, youngAvengers, defenders));

        assertThat(asIds(findProjectByName("Avengers vs. X-Men").getTeams()))
                .containsExactlyInAnyOrder(avengers.getId(), fantasticFour.getId(), xMen.getId(), champions.getId());
        assertThat(asIds(findProjectByName("Secret Wars").getTeams()))
                .containsExactlyInAnyOrder(xForce.getId(), youngAvengers.getId());
        assertThat(asIds(findProjectByName("Civil War").getTeams()))
                .containsExactlyInAnyOrder(defenders.getId());

        assertThat(findTeamBy("Avengers").getProject().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findTeamBy("Fantastic Four").getProject().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findTeamBy("X-Men").getProject().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findTeamBy("Champions").getProject().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findTeamBy("X-Force").getProject().getId()).isEqualTo(secretWars.getId());
        assertThat(findTeamBy("Young Avengers").getProject().getId()).isEqualTo(secretWars.getId());
        assertThat(findTeamBy("Defenders").getProject().getId()).isEqualTo(civilWar.getId());
    }

    private List<Long> asIds(List<Team> teams) {
        return teams.stream().map(Team::getId).collect(toList());
    }

    private Project findProjectByName(String name) {
        return projectRepository.findById(projects.get(name)).get();
    }

    private Team findTeamBy(String name) {
        return teamRepository.findByName(name).get();
    }
}
