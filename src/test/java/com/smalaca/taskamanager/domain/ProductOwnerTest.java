package com.smalaca.taskamanager.domain;

import com.smalaca.taskamanager.model.entities.ProductOwner;
import com.smalaca.taskamanager.model.entities.Project;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductOwnerTest {

    @Test
    void shouldAddProjects() {
        ProductOwner actual = new ProductOwner();
        actual.addProject(project(13, "Empyre"));
        actual.addProject(project(42, "Secret Wars"));

        assertThat(actual.getProjects()).containsExactlyInAnyOrder(project(13, "Empyre"), project(42, "Secret Wars"));
    }

    @Test
    void shouldRemoveNotExistingProject() {
        ProductOwner actual = new ProductOwner();

        assertThrows(RuntimeException.class, () -> actual.removeProject(project(42, "Secret Wars")));
    }

    @Test
    void shouldRemoveProject() {
        ProductOwner actual = new ProductOwner();
        actual.addProject(project(13, "Empyre"));
        actual.addProject(project(42, "Secret Wars"));
        actual.addProject(project(69, "AVX"));
        actual.addProject(project(123, "Phoenix Saga"));

        actual.removeProject(project(42, "Secret Wars"));
        actual.removeProject(project(69, "AVX"));

        assertThat(actual.getProjects()).containsExactlyInAnyOrder(project(13, "Empyre"), project(123, "Phoenix Saga"));
    }

    private Project project(long id, String name) {
        Project project = new Project();
        project.setName(name);
        setId(id, project);

        return project;
    }

    private void setId(long id, Project project) {
        try {
            Field fieldId = Project.class.getDeclaredField("id");
            fieldId.setAccessible(true);
            fieldId.set(project, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}