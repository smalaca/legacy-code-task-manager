package com.smalaca.taskamanager.api.rest;

import com.smalaca.taskamanager.dto.ProjectDto;
import com.smalaca.taskamanager.model.entities.ProductOwner;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Team;
import com.smalaca.taskamanager.model.enums.ProjectStatus;
import com.smalaca.taskamanager.repository.ProjectRepository;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

class ProjectControllerTest {
    private final ProjectRepository repository = mock(ProjectRepository.class);
    private final ProjectController controller = new ProjectController(repository);

    @Test
    void shouldFindAllProjects() {
        given(repository.findAll()).willReturn(asList(
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
        given(repository.findById(13L)).willReturn(Optional.empty());

        ResponseEntity<ProjectDto> actual = controller.getProject(13L);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldFindProject() {
        Project project = project(13, "Schizm", COMPLETED, 113);
        project.addTeam(withId(new Team(), 1));
        project.addTeam(withId(new Team(), 2));
        given(repository.findById(13L)).willReturn(Optional.of(project));

        ResponseEntity<ProjectDto> actual = controller.getProject(13L);

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
        given(repository.findByName("Weapon X")).willReturn(Optional.of(new Project()));
        ProjectDto dto = new ProjectDto();
        dto.setName("Weapon X");

        ResponseEntity<Void> actual = controller.createProject(dto, null);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldCreateProject() {
        UriComponentsBuilder uriComponentsBuilder = fromUriString("/");
        given(repository.findByName("Weapon X")).willReturn(Optional.empty());
        given(repository.save(any())).willReturn(withId(new Project(), 69));
        ProjectDto dto = new ProjectDto();
        dto.setName("Weapon X");

        ResponseEntity<Void> actual = controller.createProject(dto, uriComponentsBuilder);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(actual.getHeaders().getLocation().getPath()).isEqualTo("/project/69");
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        then(repository).should().save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Weapon X");
    }

    @Test
    void shouldRecognizeThereIsNoProjectToUpdate() {
        given(repository.findById(13L)).willReturn(Optional.empty());
        ProjectDto dto = new ProjectDto();
        dto.setProjectStatus("COMPLETED");

        ResponseEntity<ProjectDto> actual = controller.updateProject(13L, dto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldUpdateProject() {
        given(repository.findById(13L)).willReturn(Optional.of(project(2, "Fantastic Four vs. X-Men", IDEA, 13)));
        given(repository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
        ProjectDto dto = new ProjectDto();
        dto.setProjectStatus("COMPLETED");

        ResponseEntity<ProjectDto> actual = controller.updateProject(13L, dto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProjectDto projectDto = actual.getBody();
        assertThat(projectDto.getId()).isEqualTo(2);
        assertThat(projectDto.getName()).isEqualTo("Fantastic Four vs. X-Men");
        assertThat(projectDto.getProjectStatus()).isEqualTo("COMPLETED");
        assertThat(projectDto.getProductOwnerId()).isEqualTo(13);
    }

    @Test
    void shouldRecognizeThereIsNoProjectToDelete() {
        given(repository.findById(13L)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.deleteProject(13L);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldDeleteProject() {
        Project project = withId(new Project(), 13);
        given(repository.findById(13L)).willReturn(Optional.of(project));

        ResponseEntity<Void> actual = controller.deleteProject(13L);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(repository).should().delete(project);
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