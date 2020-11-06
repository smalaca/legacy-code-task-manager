package com.smalaca.taskamanager.registry;

import com.smalaca.taskamanager.events.EpicReadyToPrioritize;
import com.smalaca.taskamanager.events.StoryApprovedEvent;
import com.smalaca.taskamanager.events.StoryDoneEvent;
import com.smalaca.taskamanager.events.TaskApprovedEvent;
import com.smalaca.taskamanager.events.ToDoItemReleasedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class EventsRegistryTest {
    private static final long ID = 13;

    private final ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
    private final EventsRegistry registry = new EventsRegistry(publisher);

    @Test
    void shouldPublishStoryDoneEvent() {
        StoryDoneEvent event = new StoryDoneEvent();
        event.setStoryId(ID);

        registry.publish(event);

        ArgumentCaptor<StoryDoneEvent> captor = ArgumentCaptor.forClass(StoryDoneEvent.class);
        then(publisher).should().publishEvent(captor.capture());
        StoryDoneEvent actual = captor.getValue();
        assertThat(actual.getStoryId()).isEqualTo(ID);
    }

    @Test
    void shouldPublishStoryApprovedEvent() {
        StoryApprovedEvent event = new StoryApprovedEvent();
        event.setStoryId(ID);

        registry.publish(event);

        ArgumentCaptor<StoryApprovedEvent> captor = ArgumentCaptor.forClass(StoryApprovedEvent.class);
        then(publisher).should().publishEvent(captor.capture());
        StoryApprovedEvent actual = captor.getValue();
        assertThat(actual.getStoryId()).isEqualTo(ID);
    }

    @Test
    void shouldPublishTaskApprovedEvent() {
        TaskApprovedEvent event = new TaskApprovedEvent();
        event.setTaskId(ID);

        registry.publish(event);

        ArgumentCaptor<TaskApprovedEvent> captor = ArgumentCaptor.forClass(TaskApprovedEvent.class);
        then(publisher).should().publishEvent(captor.capture());
        TaskApprovedEvent actual = captor.getValue();
        assertThat(actual.getTaskId()).isEqualTo(ID);
    }

    @Test
    void shouldPublishEpicReadyToPrioritize() {
        EpicReadyToPrioritize event = new EpicReadyToPrioritize();
        event.setEpicId(ID);

        registry.publish(event);

        ArgumentCaptor<EpicReadyToPrioritize> captor = ArgumentCaptor.forClass(EpicReadyToPrioritize.class);
        then(publisher).should().publishEvent(captor.capture());
        EpicReadyToPrioritize actual = captor.getValue();
        assertThat(actual.getEpicId()).isEqualTo(ID);
    }

    @Test
    void shouldPublishToDoItemReleasedEvent() {
        ToDoItemReleasedEvent event = new ToDoItemReleasedEvent();
        event.setToDoItemId(ID);

        registry.publish(event);

        ArgumentCaptor<ToDoItemReleasedEvent> captor = ArgumentCaptor.forClass(ToDoItemReleasedEvent.class);
        then(publisher).should().publishEvent(captor.capture());
        ToDoItemReleasedEvent actual = captor.getValue();
        assertThat(actual.getToDoItemId()).isEqualTo(ID);
    }
}