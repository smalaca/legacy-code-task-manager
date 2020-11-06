package com.smalaca.taskamanager.service;

import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class StoryServiceImplTest {
    private final StoryServiceImpl service = new StoryServiceImpl();

    @Test
    void shouldDoNothingWhenUpdatingProgress() {
        Story story = mock(Story.class);
        Task task = mock(Task.class);

        service.updateProgressOf(story, task);

        verifyNoInteractions(story, task);
    }

    @Test
    void shouldDoNothingWhenAttachingPartialApproval() {
        service.attachPartialApprovalFor(13, 42);
    }
}