package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.Project;
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
class ProjectEpicRelationTest {
    private final Map<String, Long> projects = new HashMap<>();
    private final Map<String, Long> epics = new HashMap<>();

    @Autowired private ProjectRepository projectRepository;
    @Autowired private EpicRepository epicRepository;

    @BeforeEach
    void projectsAndProductOwners() {
        givenProject("Avengers vs. X-Men");
        givenProject("Secret Wars");
        givenProject("Civil War");

        givenEpic("AVX 1");
        givenEpic("AVX 2");
        givenEpic("AVX 3");
        givenEpic("AVX 4");
        givenEpic("Secret Wars 1");
        givenEpic("Secret Wars 2");
        givenEpic("Civil War 1");
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

    private void givenEpic(String name) {
        Epic epic = epic(name);
        Long id = epicRepository.save(epic).getId();
        epics.put(name, id);
    }

    private Epic epic(String title) {
        Epic epic = new Epic();
        epic.setTitle(title);
        return epic;
    }

    @AfterEach
    void removeAll() {
        projectRepository.deleteAll();
        epicRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldAssignProductOwnerToProject() {
        Project avengersVsXmen = findProjectByName("Avengers vs. X-Men");
        Project secretWars = findProjectByName("Secret Wars");
        Project civilWar = findProjectByName("Civil War");

        Epic avx1 = findEpicByTitle("AVX 1");
        Epic avx2 = findEpicByTitle("AVX 2");
        Epic avx3 = findEpicByTitle("AVX 3");
        Epic avx4 = findEpicByTitle("AVX 4");
        Epic secretWars1 = findEpicByTitle("Secret Wars 1");
        Epic secretWars2 = findEpicByTitle("Secret Wars 2");
        Epic civilWar1 = findEpicByTitle("Civil War 1");

        avengersVsXmen.addEpic(avx1);
        avengersVsXmen.addEpic(avx2);
        avengersVsXmen.addEpic(avx3);
        avengersVsXmen.addEpic(avx4);
        avx1.setProject(avengersVsXmen);
        avx2.setProject(avengersVsXmen);
        avx3.setProject(avengersVsXmen);
        avx4.setProject(avengersVsXmen);

        secretWars.addEpic(secretWars1);
        secretWars.addEpic(secretWars2);
        secretWars1.setProject(secretWars);
        secretWars2.setProject(secretWars);

        civilWar.addEpic(civilWar1);
        civilWar1.setProject(civilWar);

        projectRepository.saveAll(asList(avengersVsXmen, secretWars, civilWar));
        epicRepository.saveAll(asList(avx1, avx2, avx3, avx4, secretWars1, secretWars2, civilWar1));

        assertThat(asIds(findProjectByName("Avengers vs. X-Men").getEpics()))
                .containsExactlyInAnyOrder(avx1.getId(), avx2.getId(), avx3.getId(), avx4.getId());
        assertThat(asIds(findProjectByName("Secret Wars").getEpics()))
                .containsExactlyInAnyOrder(secretWars1.getId(), secretWars2.getId());
        assertThat(asIds(findProjectByName("Civil War").getEpics()))
                .containsExactlyInAnyOrder(civilWar1.getId());

        assertThat(findEpicByTitle("AVX 1").getProject().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findEpicByTitle("AVX 2").getProject().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findEpicByTitle("AVX 3").getProject().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findEpicByTitle("AVX 4").getProject().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findEpicByTitle("Secret Wars 1").getProject().getId()).isEqualTo(secretWars.getId());
        assertThat(findEpicByTitle("Secret Wars 2").getProject().getId()).isEqualTo(secretWars.getId());
        assertThat(findEpicByTitle("Civil War 1").getProject().getId()).isEqualTo(civilWar.getId());
    }

    private List<Long> asIds(List<Epic> epics) {
        return epics.stream().map(Epic::getId).collect(toList());
    }

    private Project findProjectByName(String name) {
        return projectRepository.findById(projects.get(name)).get();
    }

    private Epic findEpicByTitle(String name) {
        return epicRepository.findById(epics.get(name)).get();
    }
}
