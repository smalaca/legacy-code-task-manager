package com.smalaca.taskamanager.service;

import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.processor.ToDoItemProcessor;
import com.smalaca.taskamanager.repository.EpicRepository;
import com.smalaca.taskamanager.repository.StoryRepository;
import com.smalaca.taskamanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class ToDoItemServiceTest {
    private static final long ID = 13;

    private final ToDoItemProcessor processor = mock(ToDoItemProcessor.class);
    private final EpicRepository epicRepository = mock(EpicRepository.class);
    private final StoryRepository storyRepository = mock(StoryRepository.class);
    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final ToDoItemService service = new ToDoItemService(processor, epicRepository, storyRepository, taskRepository);

    @Test
    void shouldProcessTask() {
        Task task = new Task();
        given(taskRepository.findById(ID)).willReturn(Optional.of(task));

        int actual = service.processTask(ID);

        assertThat(actual).isEqualTo(1);
        then(processor).should().processFor(task);
    }

    @Test
    void shouldNotProcessNotExistingTask() {
        given(taskRepository.findById(ID)).willReturn(Optional.empty());

        int actual = service.processTask(ID);

        assertThat(actual).isEqualTo(-1);
        then(processor).should(never()).processFor(any());
    }

    @Test
    void shouldCatchExceptionDuringTaskProcessing() {
        given(taskRepository.findById(ID)).willReturn(Optional.of(new Task()));
        doThrow(new RuntimeException()).when(processor).processFor(any());

        int actual = service.processTask(ID);

        assertThat(actual).isEqualTo(-2);
    }

    @Test
    void shouldProcessStory() {
        Story story = new Story();
        given(storyRepository.findById(ID)).willReturn(Optional.of(story));

        int actual = service.processStory(ID);

        assertThat(actual).isEqualTo(1);
        then(processor).should().processFor(story);
    }

    @Test
    void shouldNotProcessNotExistingStory() {
        given(storyRepository.findById(ID)).willReturn(Optional.empty());

        int actual = service.processStory(ID);

        assertThat(actual).isEqualTo(-1);
        then(processor).should(never()).processFor(any());
    }

    @Test
    void shouldCatchExceptionDuringStoryProcessing() {
        given(storyRepository.findById(ID)).willReturn(Optional.of(new Story()));
        doThrow(new RuntimeException()).when(processor).processFor(any());

        int actual = service.processStory(ID);

        assertThat(actual).isEqualTo(-2);
    }

    @Test
    void shouldProcessEpic() {
        Epic epic = new Epic();
        given(epicRepository.findById(ID)).willReturn(Optional.of(epic));

        int actual = service.processEpic(ID);

        assertThat(actual).isEqualTo(1);
        then(processor).should().processFor(epic);
    }

    @Test
    void shouldNotProcessNotExistingEpic() {
        given(epicRepository.findById(ID)).willReturn(Optional.empty());

        int actual = service.processEpic(ID);

        assertThat(actual).isEqualTo(-1);
        then(processor).should(never()).processFor(any());
    }

    @Test
    void shouldCatchExceptionDuringEpicProcessing() {
        given(epicRepository.findById(ID)).willReturn(Optional.of(new Epic()));
        doThrow(new RuntimeException()).when(processor).processFor(any());

        int actual = service.processEpic(ID);

        assertThat(actual).isEqualTo(-2);
    }
}