package com.smalaca.taskamanager.api.rest;

import com.smalaca.taskamanager.dto.SprintDto;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Sprint;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.repository.ProjectRepository;
import com.smalaca.taskamanager.repository.SprintRepository;
import com.smalaca.taskamanager.repository.StoryRepository;
import com.smalaca.taskamanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FAILED_DEPENDENCY;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

class SprintControllerTest {
    private static final long SPRINT_ID = 42;
    private static final String SPRINT_NAME = "The greatest show";
    private static final long PROJECT_ID = 13;
    private static final String PROJECT_NAME = "Epic project";
    private static final long TASK_ID = 17;
    private static final long STORY_ID = 29;

    private final SprintRepository sprintRepository = mock(SprintRepository.class);
    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final StoryRepository storyRepository = mock(StoryRepository.class);
    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final SprintController controller = new SprintController(
            sprintRepository, taskRepository, storyRepository, projectRepository);

    @Test
    void shouldNotFoundNotExistingSprint() {
        given(sprintRepository.findById(SPRINT_ID)).willReturn(Optional.empty());

        ResponseEntity<SprintDto> response = controller.findById(SPRINT_ID);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldFindSprint() {
        given(sprintRepository.findById(SPRINT_ID)).willReturn(Optional.of(existingSprint()));

        ResponseEntity<SprintDto> response = controller.findById(SPRINT_ID);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        SprintDto dto = response.getBody();
        assertThat(dto.getId()).isEqualTo(SPRINT_ID);
        assertThat(dto.getName()).isEqualTo(SPRINT_NAME);
        assertThat(dto.getProjectId()).isEqualTo(PROJECT_ID);
    }

    @Test
    void shouldNotCreateSprintIfSprintWithTheSameNameForProjectAlreadyExist() {
        given(sprintRepository.findByNameAndProjectId(SPRINT_NAME, PROJECT_ID)).willReturn(Optional.of(existingSprint()));

        ResponseEntity<Long> response = controller.create(newSprintDto());

        assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
    }

    @Test
    void shouldNotCreateWhenProjectDoesNotExist() {
        given(sprintRepository.findByNameAndProjectId(SPRINT_NAME, PROJECT_ID)).willReturn(Optional.empty());
        given(projectRepository.findById(PROJECT_ID)).willReturn(Optional.empty());

        ResponseEntity<Long> response = controller.create(newSprintDto());

        assertThat(response.getStatusCode()).isEqualTo(FAILED_DEPENDENCY);
    }

    @Test
    void shouldCreateSprint() {
        given(sprintRepository.findByNameAndProjectId(SPRINT_NAME, PROJECT_ID)).willReturn(Optional.empty());
        given(projectRepository.findById(PROJECT_ID)).willReturn(Optional.of(existingProject()));
        given(sprintRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Long> response = controller.create(newSprintDto());

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        ArgumentCaptor<Sprint> sprintCaptor = ArgumentCaptor.forClass(Sprint.class);
        then(sprintRepository).should().save(sprintCaptor.capture());
        Sprint actualSprint = sprintCaptor.getValue();
        assertThat(actualSprint.getName()).isEqualTo(SPRINT_NAME);
        assertThat(actualSprint.getProject().getName()).isEqualTo(PROJECT_NAME);
        assertThat(actualSprint.getProject().getSprints())
                .hasSize(1)
                .allSatisfy(sprint -> assertThat(sprint.getName()).isEqualTo(SPRINT_NAME));
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        then(projectRepository).should().save(projectCaptor.capture());
        Project actualProject = projectCaptor.getValue();
        assertThat(actualProject.getName()).isEqualTo(PROJECT_NAME);
        assertThat(actualProject.getSprints())
                .hasSize(1)
                .allSatisfy(sprint -> assertThat(sprint.getName()).isEqualTo(SPRINT_NAME));
    }

    private SprintDto newSprintDto() {
        SprintDto dto = new SprintDto();
        dto.setName(SPRINT_NAME);
        dto.setProjectId(PROJECT_ID);
        return dto;
    }

    @Test
    void shouldNotDeleteNotExistingSprint() {
        given(sprintRepository.findById(SPRINT_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> response = controller.delete(SPRINT_ID);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldDeleteSprint() {
        Sprint sprint = existingSprint();
        given(sprintRepository.findById(SPRINT_ID)).willReturn(Optional.of(sprint));

        ResponseEntity<Void> response = controller.delete(SPRINT_ID);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        then(sprintRepository).should().delete(sprint);
    }

    private Project existingProject() {
        Project project = withId(new Project(), PROJECT_ID);
        project.setName(PROJECT_NAME);
        return project;
    }

    @Test
    void shouldNotBeAbleToAddTaskToNotExistingSprint() {
        given(sprintRepository.findById(SPRINT_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> response = controller.addTask(SPRINT_ID, TASK_ID);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldNotBeAbleToAddNotExistingTaskToSprint() {
        given(sprintRepository.findById(SPRINT_ID)).willReturn(Optional.of(existingSprint()));
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> response = controller.addTask(SPRINT_ID, TASK_ID);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldAddTaskToSprint() {
        given(sprintRepository.findById(SPRINT_ID)).willReturn(Optional.of(existingSprint()));
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(withId(new Task(), TASK_ID)));

        ResponseEntity<Void> response = controller.addTask(SPRINT_ID, TASK_ID);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        then(taskRepository).should().save(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getCurrentSprint().getId()).isEqualTo(SPRINT_ID);
        ArgumentCaptor<Sprint> sprintCaptor = ArgumentCaptor.forClass(Sprint.class);
        then(sprintRepository).should().save(sprintCaptor.capture());
        Sprint actualSprint = sprintCaptor.getValue();
        assertThat(actualSprint.getTasks())
                .hasSize(1)
                .allSatisfy(task -> assertThat(task.getId()).isEqualTo(TASK_ID));
    }

    @Test
    void shouldNotBeAbleToAddStoryToNotExistingSprint() {
        given(sprintRepository.findById(SPRINT_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> response = controller.addStory(SPRINT_ID, STORY_ID);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldNotBeAbleToAddNotExistingStoryToSprint() {
        given(sprintRepository.findById(SPRINT_ID)).willReturn(Optional.of(existingSprint()));
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> response = controller.addStory(SPRINT_ID, STORY_ID);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldAddStoryToSprint() {
        given(sprintRepository.findById(SPRINT_ID)).willReturn(Optional.of(existingSprint()));
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(withId(new Story(), STORY_ID)));

        ResponseEntity<Void> response = controller.addStory(SPRINT_ID, STORY_ID);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        ArgumentCaptor<Story> storyCaptor = ArgumentCaptor.forClass(Story.class);
        then(storyRepository).should().save(storyCaptor.capture());
        Story actualStory = storyCaptor.getValue();
        assertThat(actualStory.getCurrentSprint().getId()).isEqualTo(SPRINT_ID);
        assertThat(actualStory.getSprints())
                .hasSize(1)
                .allSatisfy(sprint -> assertThat(sprint.getId()).isEqualTo(SPRINT_ID));
        ArgumentCaptor<Sprint> sprintCaptor = ArgumentCaptor.forClass(Sprint.class);
        then(sprintRepository).should().save(sprintCaptor.capture());
        Sprint actualSprint = sprintCaptor.getValue();
        assertThat(actualSprint.getStories())
                .hasSize(1)
                .allSatisfy(story -> assertThat(story.getId()).isEqualTo(STORY_ID));
    }

    private Sprint existingSprint() {
        Sprint sprint = withId(new Sprint(), SPRINT_ID);
        sprint.setName(SPRINT_NAME);
        sprint.setProject(existingProject());
        return sprint;
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