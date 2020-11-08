package com.smalaca.taskamanager.processor;

import com.smalaca.taskamanager.events.EpicReadyToPrioritize;
import com.smalaca.taskamanager.events.StoryApprovedEvent;
import com.smalaca.taskamanager.events.StoryDoneEvent;
import com.smalaca.taskamanager.events.TaskApprovedEvent;
import com.smalaca.taskamanager.events.ToDoItemReleasedEvent;
import com.smalaca.taskamanager.exception.UnsupportedToDoItemType;
import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.ProductOwner;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Sprint;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskamanager.model.interfaces.ToDoItem;
import com.smalaca.taskamanager.registry.EventsRegistry;
import com.smalaca.taskamanager.service.CommunicationService;
import com.smalaca.taskamanager.service.ProjectBacklogService;
import com.smalaca.taskamanager.service.SprintBacklogService;
import com.smalaca.taskamanager.service.StoryService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.APPROVED;
import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.DEFINED;
import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.DONE;
import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.IN_PROGRESS;
import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.RELEASED;
import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.TO_BE_DEFINED;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ToDoItemProcessorTest {
    private final StoryService storyService = mock(StoryService.class);
    private final EventsRegistry eventsRegistry = mock(EventsRegistry.class);
    private final ProjectBacklogService projectBacklogService = mock(ProjectBacklogService.class);
    private final CommunicationService communicationService = mock(CommunicationService.class);
    private final SprintBacklogService sprintBacklogService = mock(SprintBacklogService.class);

    private final ToDoItemProcessor processor = new ToDoItemProcessor(
            storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);

    @Test
    void shouldDoNothingForToBeDefined() {
        ToDoItem toDoItem = toDoItem(TO_BE_DEFINED);

        processor.processFor(toDoItem);

        then(toDoItem).should().getStatus();
        verifyNoMoreInteractions(toDoItem, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldProcessDefinedStoryWithoutTasks() {
        Project project = mock(Project.class);
        Story story = story(DEFINED);
        given(story.getTasks()).willReturn(emptyList());
        given(story.getProject()).willReturn(project);

        processor.processFor(story);

        then(story).should().getStatus();
        then(story).should().getTasks();
        then(story).should().getProject();
        then(projectBacklogService).should().moveToReadyForDevelopment(story, project);
        verifyNoMoreInteractions(story, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldProcessDefinedNotAssignedStoryWithTasks() {
        Project project = mock(Project.class);
        Story story = story(DEFINED);
        List<Task> tasks = asList(mock(Task.class), mock(Task.class));
        given(story.getTasks()).willReturn(tasks);
        given(story.isAssigned()).willReturn(false);
        given(story.getProject()).willReturn(project);

        processor.processFor(story);

        then(story).should().getStatus();
        then(story).should().getTasks();
        then(story).should().isAssigned();
        then(story).should().getProject();
        then(communicationService).should().notifyTeamsAbout(story, project);
        verifyNoMoreInteractions(story, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldProcessDefinedAssignedStoryWithTasks() {
        Story story = story(DEFINED);
        List<Task> tasks = asList(mock(Task.class), mock(Task.class));
        given(story.getTasks()).willReturn(tasks);
        given(story.isAssigned()).willReturn(true);

        processor.processFor(story);

        then(story).should().getStatus();
        then(story).should().getTasks();
        then(story).should().isAssigned();
        verifyNoMoreInteractions(story, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldProcessDefinedTask() {
        Sprint sprint = mock(Sprint.class);
        Task task = task(DEFINED);
        given(task.getCurrentSprint()).willReturn(sprint);

        processor.processFor(task);

        then(task).should().getStatus();
        then(task).should().getCurrentSprint();
        then(sprintBacklogService).should().moveToReadyForDevelopment(task, sprint);
        verifyNoMoreInteractions(task, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldDoNothingWhenProcessDefinedEpic() {
        Project project = mock(Project.class);
        ProductOwner productOwner = mock(ProductOwner.class);
        given(project.getProductOwner()).willReturn(productOwner);
        long epicId = 123;
        Epic epic = epic(DEFINED);
        given(epic.getProject()).willReturn(project);
        given(epic.getId()).willReturn(epicId);

        processor.processFor(epic);

        then(epic).should().getStatus();
        then(epic).should().getId();
        then(epic).should().getProject();
        then(projectBacklogService).should().putOnTop(epic);
        ArgumentCaptor<EpicReadyToPrioritize> captor = ArgumentCaptor.forClass(EpicReadyToPrioritize.class);
        then(eventsRegistry).should().publish(captor.capture());
        assertThat(captor.getValue().getEpicId()).isEqualTo(epicId);
        then(communicationService).should().notify(epic, productOwner);
        verifyNoMoreInteractions(epic, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldThrowUnsupportedToDoItemType() {
        ToDoItem toDoItem = toDoItem(DEFINED);

        assertThrows(UnsupportedToDoItemType.class, () ->processor.processFor(toDoItem));

        then(toDoItem).should().getStatus();
        verifyNoMoreInteractions(toDoItem, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldDoNothingWhenProcessInProgressStory() {
        Story story = story(IN_PROGRESS);

        processor.processFor(story);

        then(story).should().getStatus();
        verifyNoMoreInteractions(story, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldDoNothingWhenProcessInProgressEpic() {
        Epic epic = epic(IN_PROGRESS);

        processor.processFor(epic);

        then(epic).should().getStatus();
        verifyNoMoreInteractions(epic, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldProcessInProgressTask() {
        Task task = task(IN_PROGRESS);
        Story story = mock(Story.class);
        given(task.getStory()).willReturn(story);

        processor.processFor(task);

        then(task).should().getStatus();
        then(task).should().getStory();
        then(storyService).should().updateProgressOf(story, task);
        verifyNoMoreInteractions(task, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldProcessDoneTaskWhenStoryIsNotDone() {
        Task task = task(DONE);
        Story story = story(IN_PROGRESS);
        given(task.getStory()).willReturn(story);

        processor.processFor(task);

        then(task).should().getStatus();
        then(task).should(times(2)).getStory();
        then(storyService).should().updateProgressOf(story, task);
        verifyNoMoreInteractions(task, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldProcessDoneTaskWhenStoryIsDone() {
        long storyId = 987;
        Story story = story(DONE);
        given(story.getId()).willReturn(storyId);
        Task task = task(DONE);
        given(task.getStory()).willReturn(story);

        processor.processFor(task);

        then(task).should().getStatus();
        then(task).should(times(2)).getStory();
        then(storyService).should().updateProgressOf(story, task);
        ArgumentCaptor<StoryDoneEvent> captor = ArgumentCaptor.forClass(StoryDoneEvent.class);
        then(eventsRegistry).should().publish(captor.capture());
        assertThat(captor.getValue().getStoryId()).isEqualTo(storyId);
        verifyNoMoreInteractions(task, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldProcessDoneStory() {
        long storyId = 13;
        Story story = story(DONE);
        given(story.getId()).willReturn(storyId);

        processor.processFor(story);

        then(story).should().getStatus();
        then(story).should().getId();
        ArgumentCaptor<StoryDoneEvent> captor = ArgumentCaptor.forClass(StoryDoneEvent.class);
        then(eventsRegistry).should().publish(captor.capture());
        assertThat(captor.getValue().getStoryId()).isEqualTo(storyId);
        verifyNoMoreInteractions(story, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldDoNothingWhenProcessDoneEpic() {
        Epic epic = epic(DONE);

        processor.processFor(epic);

        then(epic).should().getStatus();
        verifyNoMoreInteractions(epic, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldProcessApprovedStory() {
        Story story = story(APPROVED);
        long storyId = 7;
        given(story.getId()).willReturn(storyId);

        processor.processFor(story);

        then(story).should().getStatus();
        then(story).should().getId();
        ArgumentCaptor<StoryApprovedEvent> captor = ArgumentCaptor.forClass(StoryApprovedEvent.class);
        then(eventsRegistry).should().publish(captor.capture());
        assertThat(captor.getValue().getStoryId()).isEqualTo(storyId);
        verifyNoMoreInteractions(story, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldProcessApprovedTaskThatIsSubtask() {
        long taskId = 987;
        Task task = task(APPROVED);
        given(task.isSubtask()).willReturn(true);
        given(task.getId()).willReturn(taskId);

        processor.processFor(task);

        then(task).should().getStatus();
        then(task).should().isSubtask();
        then(task).should().getId();
        ArgumentCaptor<TaskApprovedEvent> captor = ArgumentCaptor.forClass(TaskApprovedEvent.class);
        then(eventsRegistry).should().publish(captor.capture());
        assertThat(captor.getValue().getTaskId()).isEqualTo(taskId);
        verifyNoMoreInteractions(task, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldProcessApprovedTaskThatIsNotSubtask() {
        long storyId = 13;
        long taskId = 42;
        Story story = mock(Story.class);
        given(story.getId()).willReturn(storyId);
        Task task = task(APPROVED);
        given(task.getStory()).willReturn(story);
        given(task.getId()).willReturn(taskId);
        given(task.isSubtask()).willReturn(false);

        processor.processFor(task);

        then(task).should().getStatus();
        then(task).should().isSubtask();
        then(task).should().getStory();
        then(task).should().getId();
        then(storyService).should().attachPartialApprovalFor(storyId, taskId);
        verifyNoMoreInteractions(task, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldDoNothingWhenProcessApprovedEpic() {
        Epic epic = epic(APPROVED);

        processor.processFor(epic);

        then(epic).should().getStatus();
        verifyNoMoreInteractions(epic, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    @Test
    void shouldProcessInReleased() {
        long toDoItemId = 42;
        ToDoItem toDoItem = toDoItem(RELEASED);
        given(toDoItem.getId()).willReturn(toDoItemId);

        processor.processFor(toDoItem);

        then(toDoItem).should().getStatus();
        then(toDoItem).should().getId();
        ArgumentCaptor<ToDoItemReleasedEvent> captor = ArgumentCaptor.forClass(ToDoItemReleasedEvent.class);
        then(eventsRegistry).should().publish(captor.capture());
        assertThat(captor.getValue().getToDoItemId()).isEqualTo(toDoItemId);
        verifyNoMoreInteractions(toDoItem, storyService, eventsRegistry, projectBacklogService, communicationService, sprintBacklogService);
    }

    private ToDoItem toDoItem(ToDoItemStatus status) {
        ToDoItem toDoItem = mock(ToDoItem.class);
        given(toDoItem.getStatus()).willReturn(status);
        return toDoItem;
    }

    private Story story(ToDoItemStatus status) {
        Story story = mock(Story.class);
        given(story.getStatus()).willReturn(status);
        return story;
    }

    private Epic epic(ToDoItemStatus status) {
        Epic epic = mock(Epic.class);
        given(epic.getStatus()).willReturn(status);
        return epic;
    }

    private Task task(ToDoItemStatus status) {
        Task task = mock(Task.class);
        given(task.getStatus()).willReturn(status);
        return task;
    }
}