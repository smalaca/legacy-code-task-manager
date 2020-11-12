package com.smalaca.taskamanager.api.rest;

import com.smalaca.taskamanager.dto.ProjectDto;
import com.smalaca.taskamanager.model.entities.ProductOwner;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Team;
import com.smalaca.taskamanager.model.enums.ProjectStatus;
import com.smalaca.taskamanager.repository.ProjectRepository;
import com.smalaca.taskamanager.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static com.smalaca.taskamanager.model.enums.ProjectStatus.ANALYSIS_OF_ROI;
import static com.smalaca.taskamanager.model.enums.ProjectStatus.COMPLETED;
import static com.smalaca.taskamanager.model.enums.ProjectStatus.IDEA;
import static com.smalaca.taskamanager.model.enums.ProjectStatus.PROOF_OF_CONCEPT;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

class ProjectControllerTest {
    private static final long PROJECT_ID = 13;
    private static final long TEAM_ID_1 = 98;
    private static final long TEAM_ID_2 = 17;
    private static final long NEW_TEAM_ID = 42;

    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final TeamRepository teamRepository = mock(TeamRepository.class);
    private final ProjectController controller = new ProjectController(projectRepository, teamRepository);

    @Test
    void shouldFindAllProjects() {
        given(projectRepository.findAll()).willReturn(asList(
                project(1, "Avengers vs. X-Men", PROOF_OF_CONCEPT),
                project(2, "Fantastic Four vs. X-Men", IDEA, 13),
                project(3, "Empyre", ANALYSIS_OF_ROI, 42)
        ));

        ResponseEntity<List<ProjectDto>> actual = controller.getAllProjects();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).hasSize(3)
                .anySatisfy(projectDto -> {
                    assertThat(projectDto.getId()).isEqualTo(1);
                    assertThat(projectDto.getName()).isEqualTo("Avengers vs. X-Men");
                    assertThat(projectDto.getProjectStatus()).isEqualTo("PROOF_OF_CONCEPT");
                    assertThat(projectDto.getProductOwnerId()).isNull();
                })
                .anySatisfy(projectDto -> {
                    assertThat(projectDto.getId()).isEqualTo(2);
                    assertThat(projectDto.getName()).isEqualTo("Fantastic Four vs. X-Men");
                    assertThat(projectDto.getProjectStatus()).isEqualTo("IDEA");
                    assertThat(projectDto.getProductOwnerId()).isEqualTo(13);
                })
                .anySatisfy(projectDto -> {
                    assertThat(projectDto.getId()).isEqualTo(3);
                    assertThat(projectDto.getName()).isEqualTo("Empyre");
                    assertThat(projectDto.getProjectStatus()).isEqualTo("ANALYSIS_OF_ROI");
                    assertThat(projectDto.getProductOwnerId()).isEqualTo(42);
                });
    }

    @Test
    void shouldRecognizeProjectIsNotFound() {
        given(projectRepository.findById(PROJECT_ID)).willReturn(Optional.empty());

        ResponseEntity<ProjectDto> actual = controller.getProject(PROJECT_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldFindProject() {
        Project project = project(13, "Schizm", COMPLETED, 113);
        project.addTeam(withId(new Team(), 1));
        project.addTeam(withId(new Team(), 2));
        given(projectRepository.findById(PROJECT_ID)).willReturn(Optional.of(project));

        ResponseEntity<ProjectDto> actual = controller.getProject(PROJECT_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProjectDto actualProject = actual.getBody();
        assertThat(actualProject.getId()).isEqualTo(13);
        assertThat(actualProject.getName()).isEqualTo("Schizm");
        assertThat(actualProject.getProjectStatus()).isEqualTo("COMPLETED");
        assertThat(actualProject.getProductOwnerId()).isEqualTo(113);
        assertThat(actualProject.getTeamIds()).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void shouldRecognizeProjectAlreadyExist() {
        given(projectRepository.findByName("Weapon X")).willReturn(Optional.of(new Project()));
        ProjectDto dto = new ProjectDto();
        dto.setName("Weapon X");

        ResponseEntity<Void> actual = controller.createProject(dto, null);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldCreateProject() {
        UriComponentsBuilder uriComponentsBuilder = fromUriString("/");
        given(projectRepository.findByName("Weapon X")).willReturn(Optional.empty());
        given(projectRepository.save(any())).willReturn(withId(new Project(), 69));
        ProjectDto dto = new ProjectDto();
        dto.setName("Weapon X");

        ResponseEntity<Void> actual = controller.createProject(dto, uriComponentsBuilder);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(actual.getHeaders().getLocation().getPath()).isEqualTo("/project/69");
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        then(projectRepository).should().save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Weapon X");
    }

    @Test
    void shouldRecognizeThereIsNoProjectToUpdate() {
        given(projectRepository.findById(PROJECT_ID)).willReturn(Optional.empty());
        ProjectDto dto = new ProjectDto();
        dto.setProjectStatus("COMPLETED");

        ResponseEntity<ProjectDto> actual = controller.updateProject(PROJECT_ID, dto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldUpdateProject() {
        given(projectRepository.findById(PROJECT_ID)).willReturn(Optional.of(project(2, "Fantastic Four vs. X-Men", IDEA, 13)));
        given(projectRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
        ProjectDto dto = new ProjectDto();
        dto.setProjectStatus("COMPLETED");

        ResponseEntity<ProjectDto> actual = controller.updateProject(PROJECT_ID, dto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProjectDto projectDto = actual.getBody();
        assertThat(projectDto.getId()).isEqualTo(2);
        assertThat(projectDto.getName()).isEqualTo("Fantastic Four vs. X-Men");
        assertThat(projectDto.getProjectStatus()).isEqualTo("COMPLETED");
        assertThat(projectDto.getProductOwnerId()).isEqualTo(13);
    }

    @Test
    void shouldRecognizeThereIsNoProjectToDelete() {
        given(projectRepository.findById(PROJECT_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.deleteProject(PROJECT_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldDeleteProject() {
        Project project = withId(new Project(), 13);
        given(projectRepository.findById(PROJECT_ID)).willReturn(Optional.of(project));

        ResponseEntity<Void> actual = controller.deleteProject(PROJECT_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(projectRepository).should().delete(project);
    }

    @Test
    void shouldRecognizeThereIsNoProjectToAddTeam() {
        given(projectRepository.findById(PROJECT_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addTeam(PROJECT_ID, NEW_TEAM_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldRecognizeTeamToAddDoesNotExist() {
        given(projectRepository.findById(PROJECT_ID)).willReturn(Optional.of(existingProjectWithTeam()));
        given(teamRepository.findById(NEW_TEAM_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addTeam(PROJECT_ID, NEW_TEAM_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldAssignTeamToProject() {
        given(projectRepository.findById(PROJECT_ID)).willReturn(Optional.of(existingProjectWithTeam()));
        given(teamRepository.findById(NEW_TEAM_ID)).willReturn(Optional.of(notAssignedTeam(NEW_TEAM_ID)));

        ResponseEntity<Void> actual = controller.addTeam(PROJECT_ID, NEW_TEAM_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        then(projectRepository).should().save(projectCaptor.capture());
        assertThat(asTeamIds(projectCaptor.getValue())).containsExactlyInAnyOrder(TEAM_ID_1, TEAM_ID_2, NEW_TEAM_ID);
        ArgumentCaptor<Team> teamCaptor = ArgumentCaptor.forClass(Team.class);
        then(teamRepository).should().save(teamCaptor.capture());
        Project teamProject = teamCaptor.getValue().getProject();
        assertThat(teamProject.getId()).isEqualTo(PROJECT_ID);
    }

    private List<Long> asTeamIds(Project actualProject) {
        List<Long> teamIds = actualProject.getTeams().stream().map(Team::getId).collect(toList());
        return teamIds;
    }

    @Test
    void shouldRecognizeThereIsNoProjectToRemoveTeam() {
        given(projectRepository.findById(PROJECT_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.removeTeam(PROJECT_ID, TEAM_ID_2);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldRecognizeTeamToRemoveDoesNotExist() {
        given(projectRepository.findById(PROJECT_ID)).willReturn(Optional.of(existingProjectWithTeam()));
        given(teamRepository.findById(TEAM_ID_2)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.removeTeam(PROJECT_ID, TEAM_ID_2);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldRemoveTeamToProject() {
        Project project = existingProjectWithTeam();
        Team team = withId(new Team(), TEAM_ID_2);
        team.setProject(project);
        given(projectRepository.findById(PROJECT_ID)).willReturn(Optional.of(project));
        given(teamRepository.findById(TEAM_ID_2)).willReturn(Optional.of(team));

        ResponseEntity<Void> actual = controller.removeTeam(PROJECT_ID, TEAM_ID_2);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        then(projectRepository).should().save(projectCaptor.capture());
        assertThat(asTeamIds(projectCaptor.getValue())).containsExactlyInAnyOrder(TEAM_ID_1);
        ArgumentCaptor<Team> teamCaptor = ArgumentCaptor.forClass(Team.class);
        then(teamRepository).should().save(teamCaptor.capture());
        assertThat(teamCaptor.getValue().getProject()).isNull();
    }

    private Team notAssignedTeam(long id) {
        return withId(new Team(), id);
    }

    private Project existingProjectWithTeam() {
        Project project = withId(new Project(), PROJECT_ID);
        project.addTeam(withId(new Team(), TEAM_ID_1));
        project.addTeam(withId(new Team(), TEAM_ID_2));
        return project;
    }

    private Project project(int id, String name, ProjectStatus projectStatus, int productOwnerId) {
        Project project = project(id, name, projectStatus);
        project.setProductOwner(withId(new ProductOwner(), productOwnerId));
        return project;
    }

    private Project project(int id, String name, ProjectStatus projectStatus) {
        Project project = new Project();
        project.setName(name);
        project.setProjectStatus(projectStatus);

        return withId(project, id);
    }

    private <T> T withId(T entity, long id) {
        try {
            Field fieldId = entity.getClass().getDeclaredField("id");
            fieldId.setAccessible(true);
            fieldId.set(entity, id);
            return entity;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}