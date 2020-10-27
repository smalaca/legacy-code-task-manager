package com.smalaca.taskamanager.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectTest {
    @Test
    void shouldRecognizeEqualProjects() {
        Project actual = project(13, "AVX");

        assertThat(actual.equals(project(13, "AVX"))).isTrue();
        assertThat(actual.hashCode()).isEqualTo(project(13, "AVX").hashCode());
    }

    @Test
    void shouldNotBeEqualToNull() {
        assertThat(new Project().equals(null)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("differentProjects")
    void shouldRecognizeDifferentProjects(Object object) {
        Project actual = project(13, "AVX");

        assertThat(actual.equals(object)).isFalse();
        assertThat(actual.hashCode()).isNotEqualTo(object.hashCode());
    }

    private static Stream<Object> differentProjects() {
        return Stream.of(project(13, "Project Phoenix"), project(42, "AVX"), new Object());
    }

    private static Project project(long id, String name) {
        Project project = new Project();
        project.setName(name);
        setId(id, project);

        return project;
    }

    private static void setId(long id, Project project) {
        try {
            Field fieldId = Project.class.getDeclaredField("id");
            fieldId.setAccessible(true);
            fieldId.set(project, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}