package com.smalaca.taskamanager.api.rest;

import com.smalaca.taskamanager.dto.AssigneeDto;
import com.smalaca.taskamanager.dto.StakeholderDto;
import com.smalaca.taskamanager.dto.TaskDto;
import com.smalaca.taskamanager.dto.WatcherDto;
import com.smalaca.taskamanager.model.embedded.Assignee;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.UserName;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.entities.Team;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskamanager.repository.StoryRepository;
import com.smalaca.taskamanager.repository.TaskRepository;
import com.smalaca.taskamanager.repository.TeamRepository;
import com.smalaca.taskamanager.repository.UserRepository;
import com.smalaca.taskamanager.service.ToDoItemService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.util.Optional;

import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.IN_PROGRESS;
import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.RELEASED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class TaskControllerTest {
    private static final String TITLE = "Title like all the others";
    private static final String DESCRIPTION = "Something have to be done";
    private static final ToDoItemStatus STATUS = IN_PROGRESS;
    private static final String FIRST_NAME = "Nick";
    private static final String LAST_NAME = "Fury";
    private static final String EMAIL_ADDRESS = "nick.fury@shield.marvel.com";
    private static final String PHONE_PREFIX = "567";
    private static final String PHONE_NUMBER = "133131313";
    private static final String ANOTHER_FIRST_NAME = "Maria";
    private static final String ANOTHER_LAST_NAME = "Hill";
    private static final String ANOTHER_EMAIL_ADDRESS = "maria.hill@shield.com";
    private static final String ANOTHER_PHONE_PREFIX = "909";
    private static final String ANOTHER_PHONE_NUMBER = "982478438743";
    private static final long TASK_ID = 13;
    private static final long OWNER_ID = 42;
    private static final long STORY_ID = 69;
    private static final long WATCHER_ID = 5;
    private static final long STAKEHOLDER_ID = 17;
    private static final long TEAM_ID = 987;
    private static final long ASSIGNEE_ID = 476;

    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final TeamRepository teamRepository = mock(TeamRepository.class);
    private final StoryRepository storyRepository = mock(StoryRepository.class);
    private final ToDoItemService toDoItemService = mock(ToDoItemService.class);
    private final TaskController controller = new TaskController(
            taskRepository, userRepository, teamRepository, storyRepository, toDoItemService);
    private final ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);

    @Test
    void shouldNotFindTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.empty());

        ResponseEntity<TaskDto> actual = controller.findById(TASK_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFindTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(existingTask()));

        ResponseEntity<TaskDto> actual = controller.findById(TASK_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskDto dto = actual.getBody();
        assertThat(dto.getId()).isEqualTo(TASK_ID);
        assertThat(dto.getTitle()).isEqualTo(TITLE);
        assertThat(dto.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(dto.getStatus()).isEqualTo(STATUS.name());
        assertThat(dto.getOwnerFirstName()).isEqualTo(FIRST_NAME);
        assertThat(dto.getOwnerLastName()).isEqualTo(LAST_NAME);
        assertThat(dto.getOwnerEmailAddress()).isEqualTo(EMAIL_ADDRESS);
        assertThat(dto.getOwnerPhoneNumberPrefix()).isEqualTo(PHONE_PREFIX);
        assertThat(dto.getOwnerPhoneNumberNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(dto.getStoryId()).isEqualTo(STORY_ID);
        assertThat(dto.getWatchers())
                .hasSize(1)
                .anySatisfy(watcherDto -> {
                    assertThat(watcherDto.getFirstName()).isEqualTo(ANOTHER_FIRST_NAME);
                    assertThat(watcherDto.getLastName()).isEqualTo(ANOTHER_LAST_NAME);
                    assertThat(watcherDto.getEmailAddress()).isEqualTo(ANOTHER_EMAIL_ADDRESS);
                    assertThat(watcherDto.getPhonePrefix()).isEqualTo(ANOTHER_PHONE_PREFIX);
                    assertThat(watcherDto.getPhoneNumber()).isEqualTo(ANOTHER_PHONE_NUMBER);
                });
        assertThat(dto.getStakeholders())
                .hasSize(1)
                .anySatisfy(stakeholderDto -> {
                    assertThat(stakeholderDto.getFirstName()).isEqualTo(ANOTHER_FIRST_NAME);
                    assertThat(stakeholderDto.getLastName()).isEqualTo(ANOTHER_LAST_NAME);
                    assertThat(stakeholderDto.getEmailAddress()).isEqualTo(ANOTHER_EMAIL_ADDRESS);
                    assertThat(stakeholderDto.getPhonePrefix()).isEqualTo(ANOTHER_PHONE_PREFIX);
                    assertThat(stakeholderDto.getPhoneNumber()).isEqualTo(ANOTHER_PHONE_NUMBER);
                });
        assertThat(dto.getAssignee().getFirstName()).isEqualTo(ANOTHER_FIRST_NAME);
        assertThat(dto.getAssignee().getLastName()).isEqualTo(ANOTHER_LAST_NAME);
        assertThat(dto.getAssignee().getTeamId()).isEqualTo(TEAM_ID);
    }

    private Task existingTask() {
        Task task = taskWithoutOwner();
        Owner owner = new Owner();
        owner.setFirstName(FIRST_NAME);
        owner.setLastName(LAST_NAME);
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress(EMAIL_ADDRESS);
        owner.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix(PHONE_PREFIX);
        phoneNumber.setNumber(PHONE_NUMBER);
        owner.setPhoneNumber(phoneNumber);
        task.setOwner(owner);
        task.addWatcher(watcher());
        task.addStakeholder(stakeholder());
        task.setAssignee(assignee());

        return task;
    }

    @Test
    void shouldNotCreateInCaseOfNotExistingStory() {
        given(storyRepository.existsById(STORY_ID)).willReturn(false);
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(story()));
        given(userRepository.findById(OWNER_ID)).willReturn(Optional.of(owner()));

        ResponseEntity<Long> actual = controller.create(newTaskDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldNotCreateInCaseOfNotExistingUser() {
        given(storyRepository.existsById(STORY_ID)).willReturn(true);
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(story()));
        given(userRepository.findById(OWNER_ID)).willReturn(Optional.empty());

        ResponseEntity<Long> actual = controller.create(newTaskDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldCreateTask() {
        given(storyRepository.existsById(STORY_ID)).willReturn(true);
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(story()));
        given(userRepository.findById(OWNER_ID)).willReturn(Optional.of(owner()));
        given(taskRepository.save(any())).willReturn(taskWithId());

        ResponseEntity<Long> actual = controller.create(newTaskDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(TASK_ID);
        then(taskRepository).should().save(taskCaptor.capture());
        Task task = taskCaptor.getValue();
        assertThat(task.getTitle()).isEqualTo(TITLE);
        assertThat(task.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(task.getStatus()).isEqualTo(STATUS);
        assertThat(task.getOwner().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(task.getOwner().getLastName()).isEqualTo(LAST_NAME);
        assertThat(task.getOwner().getEmailAddress().getEmailAddress()).isEqualTo(EMAIL_ADDRESS);
        assertThat(task.getOwner().getPhoneNumber().getPrefix()).isEqualTo(PHONE_PREFIX);
        assertThat(task.getOwner().getPhoneNumber().getNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(task.getStory().getId()).isEqualTo(STORY_ID);
        ArgumentCaptor<Story> storyCaptor = ArgumentCaptor.forClass(Story.class);
        then(storyRepository).should().save(storyCaptor.capture());
        assertThat(storyCaptor.getValue().getTasks())
                .hasSize(1)
                .allSatisfy(actualTask -> assertThat(actualTask).isSameAs(task));
    }

    private TaskDto newTaskDto() {
        TaskDto dto = newStandaloneTaskDto();
        dto.setStoryId(STORY_ID);

        return dto;
    }

    @Test
    void shouldCreateStandaloneTask() {
        given(userRepository.findById(OWNER_ID)).willReturn(Optional.of(owner()));
        given(taskRepository.save(any())).willReturn(taskWithId());

        ResponseEntity<Long> actual = controller.create(newStandaloneTaskDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(TASK_ID);
        then(taskRepository).should().save(taskCaptor.capture());
        Task task = taskCaptor.getValue();
        assertThat(task.getTitle()).isEqualTo(TITLE);
        assertThat(task.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(task.getStatus()).isEqualTo(STATUS);
        assertThat(task.getOwner().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(task.getOwner().getLastName()).isEqualTo(LAST_NAME);
        assertThat(task.getOwner().getEmailAddress().getEmailAddress()).isEqualTo(EMAIL_ADDRESS);
        assertThat(task.getOwner().getPhoneNumber().getPrefix()).isEqualTo(PHONE_PREFIX);
        assertThat(task.getOwner().getPhoneNumber().getNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(task.getStory()).isNull();
    }

    private TaskDto newStandaloneTaskDto() {
        TaskDto dto = new TaskDto();
        dto.setTitle(TITLE);
        dto.setDescription(DESCRIPTION);
        dto.setStatus(STATUS.name());
        dto.setOwnerId(OWNER_ID);

        return dto;
    }

    @Test
    void shouldNotUpdateNotExistingTask() {
        given(taskRepository.existsById(TASK_ID)).willReturn(false);

        ResponseEntity<Void> actual = controller.update(TASK_ID, updateTaskDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotUpdateWithNonExistingUser() {
        given(taskRepository.existsById(TASK_ID)).willReturn(true);
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(taskWithoutOwner()));
        given(userRepository.existsById(OWNER_ID)).willReturn(false);

        ResponseEntity<Void> actual = controller.update(TASK_ID, updateTaskDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldUpdateExistingTaskWithoutStatusChange() {
        given(taskRepository.existsById(TASK_ID)).willReturn(true);
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(task()));

        ResponseEntity<Void> actual = controller.update(TASK_ID, updateTaskDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(taskRepository).should().save(taskCaptor.capture());
        Task task = taskCaptor.getValue();
        assertThat(task.getStatus()).isEqualTo(IN_PROGRESS);
        then(toDoItemService).should(never()).processTask(any());
    }

    @Test
    void shouldUpdateExistingTaskWithStatusChange() {
        given(taskRepository.existsById(TASK_ID)).willReturn(true);
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(task()));
        TaskDto dto = updateTaskDto();
        dto.setStatus("RELEASED");

        ResponseEntity<Void> actual = controller.update(TASK_ID, dto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(taskRepository).should().save(taskCaptor.capture());
        Task task = taskCaptor.getValue();
        assertThat(task.getStatus()).isEqualTo(RELEASED);
        then(toDoItemService).should().processTask(TASK_ID);
    }

    @Test
    void shouldUpdateExistingTaskWithOwner() {
        given(taskRepository.existsById(TASK_ID)).willReturn(true);
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(task()));

        ResponseEntity<Void> actual = controller.update(TASK_ID, updateTaskDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(taskRepository).should().save(taskCaptor.capture());
        Task task = taskCaptor.getValue();
        assertThat(task.getTitle()).isEqualTo(TITLE);
        assertThat(task.getDescription()).isEqualTo("new description");
        assertThat(task.getStatus()).isEqualTo(IN_PROGRESS);
        assertThat(task.getOwner().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(task.getOwner().getLastName()).isEqualTo(LAST_NAME);
        assertThat(task.getOwner().getEmailAddress().getEmailAddress()).isEqualTo("john.doe@test.com");
        assertThat(task.getOwner().getPhoneNumber().getPrefix()).isEqualTo("9900");
        assertThat(task.getOwner().getPhoneNumber().getNumber()).isEqualTo("8877665544");
        assertThat(task.getStory().getId()).isEqualTo(STORY_ID);
    }

    @Test
    void shouldUpdateExistingTaskWithoutOwner() {
        given(taskRepository.existsById(TASK_ID)).willReturn(true);
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(taskWithoutOwner()));
        given(userRepository.existsById(OWNER_ID)).willReturn(true);
        given(userRepository.findById(OWNER_ID)).willReturn(Optional.of(owner()));

        ResponseEntity<Void> actual = controller.update(TASK_ID, updateTaskDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(taskRepository).should().save(taskCaptor.capture());
        Task task = taskCaptor.getValue();
        assertThat(task.getTitle()).isEqualTo(TITLE);
        assertThat(task.getDescription()).isEqualTo("new description");
        assertThat(task.getStatus()).isEqualTo(IN_PROGRESS);
        assertThat(task.getOwner().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(task.getOwner().getLastName()).isEqualTo(LAST_NAME);
        assertThat(task.getOwner().getEmailAddress().getEmailAddress()).isEqualTo(EMAIL_ADDRESS);
        assertThat(task.getOwner().getPhoneNumber().getPrefix()).isEqualTo(PHONE_PREFIX);
        assertThat(task.getOwner().getPhoneNumber().getNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(task.getStory().getId()).isEqualTo(STORY_ID);
    }

    private TaskDto updateTaskDto() {
        TaskDto dto = new TaskDto();
        dto.setDescription("new description");
        dto.setStatus("IN_PROGRESS");
        dto.setOwnerEmailAddress("john.doe@test.com");
        dto.setOwnerPhoneNumberPrefix("9900");
        dto.setOwnerPhoneNumberNumber("8877665544");
        dto.setOwnerId(OWNER_ID);

        return dto;
    }

    private Task task() {
        Task task = taskWithoutOwner();
        Owner owner = new Owner();
        owner.setFirstName(FIRST_NAME);
        owner.setLastName(LAST_NAME);
        EmailAddress emailAddress = new EmailAddress();
        owner.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        owner.setPhoneNumber(phoneNumber);
        task.setOwner(owner);

        return task;
    }

    private Task taskWithoutOwner() {
        Task task = taskWithId();
        task.setTitle(TITLE);
        task.setDescription(DESCRIPTION);
        task.setStatus(STATUS);
        task.setStory(story());

        return task;
    }

    @Test
    void shouldNotDeleteNotExistingTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.delete(TASK_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldDeleteTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(taskWithId()));

        ResponseEntity<Void> actual = controller.delete(TASK_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(taskRepository).should().delete(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getId()).isEqualTo(TASK_ID);
    }

    @Test
    void shouldNotAddWatcherToNotExistingTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addWatcher(TASK_ID, watcherDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAddNotExistingWatcherToTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(taskWithId()));
        given(userRepository.findById(WATCHER_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addWatcher(TASK_ID, watcherDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldAddWatcherToTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(taskWithId()));
        given(userRepository.findById(WATCHER_ID)).willReturn(Optional.of(userWithId(WATCHER_ID)));

        ResponseEntity<Void> actual = controller.addWatcher(TASK_ID, watcherDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(taskRepository).should().save(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getWatchers())
                .hasSize(1)
                .anySatisfy(watcher -> {
                    assertThat(watcher.getFirstName()).isEqualTo(ANOTHER_FIRST_NAME);
                    assertThat(watcher.getLastName()).isEqualTo(ANOTHER_LAST_NAME);
                    assertThat(watcher.getEmailAddress().getEmailAddress()).isEqualTo(ANOTHER_EMAIL_ADDRESS);
                    assertThat(watcher.getPhoneNumber().getPrefix()).isEqualTo(ANOTHER_PHONE_PREFIX);
                    assertThat(watcher.getPhoneNumber().getNumber()).isEqualTo(ANOTHER_PHONE_NUMBER);
                });
    }

    @Test
    void shouldNotRemoveWatcherFromNotExistingTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.removeWatcher(TASK_ID, WATCHER_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void shouldNotRemoveNotExistingWatcherFromTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(taskWithWatcher()));
        given(userRepository.findById(WATCHER_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.removeWatcher(TASK_ID, WATCHER_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldRemoveWatcherFromTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(taskWithWatcher()));
        given(userRepository.findById(WATCHER_ID)).willReturn(Optional.of(userWithId(WATCHER_ID)));

        ResponseEntity<Void> actual = controller.removeWatcher(TASK_ID, WATCHER_ID);


        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(taskRepository).should().save(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getWatchers()).isEmpty();
    }

    private Task taskWithWatcher() {
        Task task = taskWithId();
        task.addWatcher(watcher());

        return task;
    }

    private Watcher watcher() {
        Watcher watcher = new Watcher();
        watcher.setFirstName(ANOTHER_FIRST_NAME);
        watcher.setLastName(ANOTHER_LAST_NAME);
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress(ANOTHER_EMAIL_ADDRESS);
        watcher.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix(ANOTHER_PHONE_PREFIX);
        phoneNumber.setNumber(ANOTHER_PHONE_NUMBER);
        watcher.setPhoneNumber(phoneNumber);
        return watcher;
    }

    private WatcherDto watcherDto() {
        WatcherDto dto = new WatcherDto();
        dto.setId(WATCHER_ID);
        return dto;
    }

    @Test
    void shouldNotAddStakeholderToNotExistingTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addStakeholder(TASK_ID, stakeholderDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAddNotExistingStakeholderToTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(taskWithId()));
        given(userRepository.findById(STAKEHOLDER_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addStakeholder(TASK_ID, stakeholderDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldAddStakeholderToTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(taskWithId()));
        given(userRepository.findById(STAKEHOLDER_ID)).willReturn(Optional.of(userWithId(STAKEHOLDER_ID)));

        ResponseEntity<Void> actual = controller.addStakeholder(TASK_ID, stakeholderDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(taskRepository).should().save(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getStakeholders())
                .hasSize(1)
                .anySatisfy(stakeholder -> {
                    assertThat(stakeholder.getFirstName()).isEqualTo(ANOTHER_FIRST_NAME);
                    assertThat(stakeholder.getLastName()).isEqualTo(ANOTHER_LAST_NAME);
                    assertThat(stakeholder.getEmailAddress().getEmailAddress()).isEqualTo(ANOTHER_EMAIL_ADDRESS);
                    assertThat(stakeholder.getPhoneNumber().getPrefix()).isEqualTo(ANOTHER_PHONE_PREFIX);
                    assertThat(stakeholder.getPhoneNumber().getNumber()).isEqualTo(ANOTHER_PHONE_NUMBER);
                });
    }

    @Test
    void shouldNotRemoveStakeholderFromNotExistingTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.removeStakeholder(TASK_ID, STAKEHOLDER_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotRemoveNotExistingStakeholderFromTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(taskWithStakeholder()));
        given(userRepository.findById(STAKEHOLDER_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.removeStakeholder(TASK_ID, STAKEHOLDER_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldRemoveStakeholderFromTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(taskWithStakeholder()));
        given(userRepository.findById(STAKEHOLDER_ID)).willReturn(Optional.of(userWithId(STAKEHOLDER_ID)));

        ResponseEntity<Void> actual = controller.removeStakeholder(TASK_ID, STAKEHOLDER_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(taskRepository).should().save(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getStakeholders()).isEmpty();
    }

    private Task taskWithStakeholder() {
        Task task = taskWithId();
        task.addStakeholder(stakeholder());

        return task;
    }

    private Stakeholder stakeholder() {
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.setFirstName(ANOTHER_FIRST_NAME);
        stakeholder.setLastName(ANOTHER_LAST_NAME);
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress(ANOTHER_EMAIL_ADDRESS);
        stakeholder.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix(ANOTHER_PHONE_PREFIX);
        phoneNumber.setNumber(ANOTHER_PHONE_NUMBER);
        stakeholder.setPhoneNumber(phoneNumber);
        return stakeholder;
    }

    private StakeholderDto stakeholderDto() {
        StakeholderDto dto = new StakeholderDto();
        dto.setId(STAKEHOLDER_ID);
        return dto;
    }

    @Test
    void shouldNotAddAssigneeToNotExistingTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addAssignee(TASK_ID, assigneeDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAddNotExistingAssigneeToTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(taskWithId()));
        given(userRepository.findById(ASSIGNEE_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addAssignee(TASK_ID, assigneeDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldNotAddAssigneeWithNotExistingTeamToTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(taskWithId()));
        given(userRepository.findById(ASSIGNEE_ID)).willReturn(Optional.of(userWithId(ASSIGNEE_ID)));
        given(teamRepository.findById(TEAM_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addAssignee(TASK_ID, assigneeDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldAddAssigneeToTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(taskWithId()));
        given(userRepository.findById(ASSIGNEE_ID)).willReturn(Optional.of(userWithId(ASSIGNEE_ID)));
        given(teamRepository.findById(TEAM_ID)).willReturn(Optional.of(teamWithId(TEAM_ID)));

        ResponseEntity<Void> actual = controller.addAssignee(TASK_ID, assigneeDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(taskRepository).should().save(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getAssignee().getFirstName()).isEqualTo(ANOTHER_FIRST_NAME);
        assertThat(taskCaptor.getValue().getAssignee().getLastName()).isEqualTo(ANOTHER_LAST_NAME);
        assertThat(taskCaptor.getValue().getAssignee().getTeamId()).isEqualTo(TEAM_ID);
    }

    @Test
    void shouldNotRemoveAssigneeFromNotExistingTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.removeAssignee(TASK_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldRemoveAssigneeFromTask() {
        given(taskRepository.findById(TASK_ID)).willReturn(Optional.of(taskWithAssignee()));

        ResponseEntity<Void> actual = controller.removeAssignee(TASK_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(taskRepository).should().save(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getAssignee()).isNull();
    }

    private Task taskWithAssignee() {
        Task task = taskWithId();
        task.setAssignee(assignee());

        return task;
    }

    private Assignee assignee() {
        Assignee assignee = new Assignee();
        assignee.setFirstName(ANOTHER_FIRST_NAME);
        assignee.setLastName(ANOTHER_LAST_NAME);
        assignee.setTeamId(TEAM_ID);
        return assignee;
    }

    private AssigneeDto assigneeDto() {
        AssigneeDto dto = new AssigneeDto();
        dto.setId(ASSIGNEE_ID);
        dto.setTeamId(TEAM_ID);
        return dto;
    }

    private Team teamWithId(long teamId) {
        return withId(new Team(), teamId);
    }

    private User userWithId(long userId) {
        return user(userId, ANOTHER_FIRST_NAME, ANOTHER_LAST_NAME, ANOTHER_EMAIL_ADDRESS, ANOTHER_PHONE_PREFIX, ANOTHER_PHONE_NUMBER);
    }

    private Task taskWithId() {
        Task task = withId(new Task(), TASK_ID);
        return task;
    }

    private Story story() {
        return withId(new Story(), STORY_ID);
    }

    private User owner() {
        return user(OWNER_ID, FIRST_NAME, LAST_NAME, EMAIL_ADDRESS, PHONE_PREFIX, PHONE_NUMBER);
    }

    private User user(long id, String firstName, String lastName, String email, String prefix, String number) {
        User user = withId(new User(), id);
        UserName userName = new UserName();
        userName.setFirstName(firstName);
        userName.setLastName(lastName);
        user.setUserName(userName);
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress(email);
        user.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix(prefix);
        phoneNumber.setNumber(number);
        user.setPhoneNumber(phoneNumber);
        return user;
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