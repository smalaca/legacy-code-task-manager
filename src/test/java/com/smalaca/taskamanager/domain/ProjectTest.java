package com.smalaca.taskamanager.domain;

import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Team;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectTest {
    @Test
    void shouldRecognizeSelfAsEqual() {
        Project actual = project(13, "AVX");

        assertThat(actual.equals(actual)).isTrue();
        assertThat(actual.hashCode()).isEqualTo(actual.hashCode());
    }

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

    @Test
    void shouldAddTeamToProject() {
        Team team = team("Avengers");
        Project project = new Project();

        project.addTeam(team);

        assertThat(project.getTeams()).containsExactly(team);
    }

    @Test
    void shouldRemoveTeamFromProject() {
        Team team1 = team("Avengers");
        Team team2 = team("X-Men");
        Team team3 = team("X Force");
        Project project = new Project();
        project.addTeam(team1);
        project.addTeam(team2);
        project.addTeam(team3);

        project.removeTeam(team("X Force"));

        assertThat(project.getTeams()).containsExactlyInAnyOrder(team1, team2);
    }

    @Test
    void shouldRecognizeWhenRemovingNotTeamAssignedToProject() {
        Team team1 = team("Avengers");
        Team team2 = team("X-Men");
        Project project = new Project();
        project.addTeam(team1);
        project.addTeam(team2);

        assertThrows(RuntimeException.class, () -> project.removeTeam(team("X Force")));

        assertThat(project.getTeams()).containsExactlyInAnyOrder(team1, team2);
    }

    private Team team(String name) {
        Team team = new Team();
        team.setName(name);
        return team;
    }
}