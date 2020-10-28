package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.enums.ProjectStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProjectRepositoryTest {
    @Autowired private ProjectRepository repository;

    @AfterEach
    void removeAll() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateProject() {
        Project project = new Project();
        project.setName("Guardians of the Galaxy");
        project.setProjectStatus(ProjectStatus.PROOF_OF_CONCEPT);

        Long id = repository.save(project).getId();
        Project actual = repository.findById(id).get();

        assertThat(actual.getName()).isEqualTo("Guardians of the Galaxy");
        assertThat(actual.getProjectStatus()).isEqualTo(ProjectStatus.PROOF_OF_CONCEPT);
    }

    @Test
    void shouldFindSpecificProject() {
        repository.save(project("X-Men", ProjectStatus.IDEA));
        Long id = repository.save(project("Avengers", ProjectStatus.STARTED)).getId();
        repository.save(project("Shi-ar", ProjectStatus.UNDER_MAINTENANCE));

        Project actual = repository.findById(id).get();

        assertThat(actual.getName()).isEqualTo("Avengers");
        assertThat(actual.getProjectStatus()).isEqualTo(ProjectStatus.STARTED);
    }

    private Project project(String name, ProjectStatus status) {
        Project project = new Project();
        project.setName(name);
        project.setProjectStatus(status);
        return project;
    }
}