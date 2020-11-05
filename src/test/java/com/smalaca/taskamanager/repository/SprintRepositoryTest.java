package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Sprint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SprintRepositoryTest {
    @Autowired private SprintRepository sprintRepository;
    @Autowired private ProjectRepository projectRepository;

    @AfterEach
    void deleteAll() {
        sprintRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    void shouldCreateSprint() {
        Sprint sprint = new Sprint();
        sprint.setName("Sprint Zero");
        Project project = new Project();
        project.setName("Empyre");
        projectRepository.save(project);
        sprint.setProject(project);

        Long id = sprintRepository.save(sprint).getId();
        Sprint actual = sprintRepository.findById(id).get();

        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getName()).isEqualTo("Sprint Zero");
        assertThat(actual.getProject().getName()).isEqualTo("Empyre");
    }

    @Test
    void shouldFindSpecificSprintById() {
        sprintRepository.save(sprint("Sprint Zero"));
        Long id = sprintRepository.save(sprint("Sprint ABC")).getId();
        sprintRepository.save(sprint("Sprint QWERTY"));

        Sprint actual = sprintRepository.findById(id).get();

        assertThat(actual.getName()).isEqualTo("Sprint ABC");
    }

    @Test
    void shouldFindSpecificSprintByNameAndProjectId() {
        Project project1 = existingProject("Project 1");
        Project project2 = existingProject("Project 2");
        existingSprint("Sprint 1", project1);
        Long id = existingSprint("Sprint 2", project1).getId();
        existingSprint("Sprint 1", project2);

        Sprint actual = sprintRepository.findByNameAndProjectId("Sprint 2", project1.getId()).get();

        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getName()).isEqualTo("Sprint 2");
        assertThat(actual.getProject().getName()).isEqualTo("Project 1");
    }

    private Project existingProject(String name) {
        Project project = new Project();
        project.setName(name);
        return projectRepository.save(project);
    }

    private Sprint existingSprint(String name, Project project) {
        Sprint sprint = sprint(name);
        sprint.setProject(project);

        return sprintRepository.save(sprint);
    }

    private Sprint sprint(String name) {
        Sprint sprint = new Sprint();
        sprint.setName(name);
        return sprint;
    }
}