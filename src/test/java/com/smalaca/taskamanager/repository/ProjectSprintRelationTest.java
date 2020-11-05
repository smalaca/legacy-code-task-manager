package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Sprint;
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
class ProjectSprintRelationTest {
    private final Map<String, Long> projects = new HashMap<>();
    private final Map<String, Long> sprints = new HashMap<>();

    @Autowired private ProjectRepository projectRepository;
    @Autowired private SprintRepository sprintRepository;

    @BeforeEach
    void projectsAndProductOwners() {
        givenProject("Avengers vs. X-Men");
        givenProject("Secret Wars");
        givenProject("Civil War");

        givenSprint("AVX 1");
        givenSprint("AVX 2");
        givenSprint("AVX 3");
        givenSprint("AVX 4");
        givenSprint("Secret Wars 1");
        givenSprint("Secret Wars 2");
        givenSprint("Civil War 1");
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

    private void givenSprint(String name) {
        Sprint sprint = sprint(name);
        Long id = sprintRepository.save(sprint).getId();
        sprints.put(name, id);
    }

    private Sprint sprint(String name) {
        Sprint sprint = new Sprint();
        sprint.setName(name);
        return sprint;
    }

    @AfterEach
    void removeAll() {
        projectRepository.deleteAll();
        sprintRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldAssignProductOwnerToProject() {
        Project avengersVsXmen = findProjectByName("Avengers vs. X-Men");
        Project secretWars = findProjectByName("Secret Wars");
        Project civilWar = findProjectByName("Civil War");

        Sprint avx1 = findSprintByTitle("AVX 1");
        Sprint avx2 = findSprintByTitle("AVX 2");
        Sprint avx3 = findSprintByTitle("AVX 3");
        Sprint avx4 = findSprintByTitle("AVX 4");
        Sprint secretWars1 = findSprintByTitle("Secret Wars 1");
        Sprint secretWars2 = findSprintByTitle("Secret Wars 2");
        Sprint civilWar1 = findSprintByTitle("Civil War 1");

        avengersVsXmen.addSprint(avx1);
        avengersVsXmen.addSprint(avx2);
        avengersVsXmen.addSprint(avx3);
        avengersVsXmen.addSprint(avx4);
        avx1.setProject(avengersVsXmen);
        avx2.setProject(avengersVsXmen);
        avx3.setProject(avengersVsXmen);
        avx4.setProject(avengersVsXmen);

        secretWars.addSprint(secretWars1);
        secretWars.addSprint(secretWars2);
        secretWars1.setProject(secretWars);
        secretWars2.setProject(secretWars);

        civilWar.addSprint(civilWar1);
        civilWar1.setProject(civilWar);

        projectRepository.saveAll(asList(avengersVsXmen, secretWars, civilWar));
        sprintRepository.saveAll(asList(avx1, avx2, avx3, avx4, secretWars1, secretWars2, civilWar1));

        assertThat(asIds(findProjectByName("Avengers vs. X-Men").getSprints()))
                .containsExactlyInAnyOrder(avx1.getId(), avx2.getId(), avx3.getId(), avx4.getId());
        assertThat(asIds(findProjectByName("Secret Wars").getSprints()))
                .containsExactlyInAnyOrder(secretWars1.getId(), secretWars2.getId());
        assertThat(asIds(findProjectByName("Civil War").getSprints()))
                .containsExactlyInAnyOrder(civilWar1.getId());

        assertThat(findSprintByTitle("AVX 1").getProject().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findSprintByTitle("AVX 2").getProject().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findSprintByTitle("AVX 3").getProject().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findSprintByTitle("AVX 4").getProject().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findSprintByTitle("Secret Wars 1").getProject().getId()).isEqualTo(secretWars.getId());
        assertThat(findSprintByTitle("Secret Wars 2").getProject().getId()).isEqualTo(secretWars.getId());
        assertThat(findSprintByTitle("Civil War 1").getProject().getId()).isEqualTo(civilWar.getId());
    }

    private List<Long> asIds(List<Sprint> sprints) {
        return sprints.stream().map(Sprint::getId).collect(toList());
    }

    private Project findProjectByName(String name) {
        return projectRepository.findById(projects.get(name)).get();
    }

    private Sprint findSprintByTitle(String name) {
        return sprintRepository.findById(sprints.get(name)).get();
    }
}
