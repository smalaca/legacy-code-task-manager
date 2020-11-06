package com.smalaca.taskamanager.service;

import com.smalaca.taskamanager.model.entities.Sprint;
import com.smalaca.taskamanager.model.entities.Task;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class SprintBacklogServiceImplTest {
    private final SprintBacklogServiceImpl service = new SprintBacklogServiceImpl();

    @Test
    void shouldDoNothingWhenMovingToReadyForDevelopment() {
        Task task = mock(Task.class);
        Sprint sprint = mock(Sprint.class);

        service.moveToReadyForDevelopment(task, sprint);

        verifyNoInteractions(task, sprint);
    }
}