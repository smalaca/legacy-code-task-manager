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
    void shouldFindSpecificSprint() {
        sprintRepository.save(sprint("Sprint Zero"));
        Long id = sprintRepository.save(sprint("Sprint ABC")).getId();
        sprintRepository.save(sprint("Sprint QWERTY"));

        Sprint actual = sprintRepository.findById(id).get();

        assertThat(actual.getName()).isEqualTo("Sprint ABC");
    }

    private Sprint sprint(String name) {
        Sprint sprint = new Sprint();
        sprint.setName(name);
        return sprint;
    }
}