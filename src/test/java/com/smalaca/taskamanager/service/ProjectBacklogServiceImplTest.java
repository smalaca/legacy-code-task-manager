package com.smalaca.taskamanager.service;

import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Story;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class ProjectBacklogServiceImplTest {
    private final ProjectBacklogServiceImpl service = new ProjectBacklogServiceImpl();

    @Test
    void shouldDoNothingWhenMoveToReadyForDevelopment() {
        Story story = mock(Story.class);
        Project project = mock(Project.class);

        service.moveToReadyForDevelopment(story, project);

        verifyNoInteractions(story, project);
    }

    @Test
    void shouldDoNothingWhenPutOnTop() {
        Epic epic = mock(Epic.class);

        service.putOnTop(epic);

        verifyNoInteractions(epic);
    }

    @Test
    void shouldDoNothingWhenLinkFor() {
        String actual = service.linkFor(13);

        assertThat(actual).isEqualTo("link to: 13");
    }
}