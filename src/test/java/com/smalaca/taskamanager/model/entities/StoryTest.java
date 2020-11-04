package com.smalaca.taskamanager.model.entities;

import com.smalaca.taskamanager.model.embedded.Assignee;
import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.Watcher;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StoryTest {
    @Test
    void shouldCreateStoryWithAssignee() {
        Story story = new Story();
        story.setAssignee(assignee("Tony", "Stark"));

        assertThat(story.getAssignee().getFirstName()).isEqualTo("Tony");
        assertThat(story.getAssignee().getLastName()).isEqualTo("Stark");
    }

    @Test
    void shouldUnsetAssigneeFromStory() {
        Story story = new Story();
        story.setAssignee(assignee("Tony", "Stark"));

        story.setAssignee(null);

        assertThat(story.getAssignee()).isNull();
    }

    private Assignee assignee(String firstName, String lastName) {
        Assignee assignee = new Assignee();
        assignee.setFirstName(firstName);
        assignee.setLastName(lastName);
        return assignee;
    }

    @Test
    void shouldCreateStoryWithWatchers() {
        Story story = new Story();

        story.addWatcher(watcher("Tony", "Stark"));
        story.addWatcher(watcher("Steve", "Rogers"));
        story.addWatcher(watcher("Thor", "Odison"));

        assertThat(story.getWatchers()).containsExactlyInAnyOrder(watcher("Tony", "Stark"), watcher("Steve", "Rogers"), watcher("Thor", "Odison"));
    }

    @Test
    void shouldRemoveWatcherFromStory() {
        Story story = new Story();
        story.addWatcher(watcher("Tony", "Stark"));
        story.addWatcher(watcher("Steve", "Rogers"));
        story.addWatcher(watcher("Thor", "Odison"));

        story.removeWatcher(watcher("Steve", "Rogers"));

        assertThat(story.getWatchers()).containsExactlyInAnyOrder(watcher("Tony", "Stark"), watcher("Thor", "Odison"));
    }

    @Test
    void shouldRecognizeWhenRemovingNotWatcherOfStory() {
        Story story = new Story();
        story.addWatcher(watcher("Tony", "Stark"));
        story.addWatcher(watcher("Steve", "Rogers"));

        assertThrows(RuntimeException.class, () -> story.removeWatcher(watcher("Thor", "Odison")));

        assertThat(story.getWatchers()).containsExactlyInAnyOrder(watcher("Tony", "Stark"), watcher("Steve", "Rogers"));
    }

    private Watcher watcher(String firstName, String lastName) {
        Watcher watcher = new Watcher();
        watcher.setFirstName(firstName);
        watcher.setLastName(lastName);
        return watcher;
    }

    @Test
    void shouldCreateStoryWithStakeholders() {
        Story story = new Story();

        story.addStakeholder(stakeholder("Tony", "Stark"));
        story.addStakeholder(stakeholder("Steve", "Rogers"));
        story.addStakeholder(stakeholder("Thor", "Odison"));

        assertThat(story.getStakeholders()).containsExactlyInAnyOrder(stakeholder("Tony", "Stark"), stakeholder("Steve", "Rogers"), stakeholder("Thor", "Odison"));
    }

    @Test
    void shouldRemoveStakeholderFromStory() {
        Story story = new Story();
        story.addStakeholder(stakeholder("Tony", "Stark"));
        story.addStakeholder(stakeholder("Steve", "Rogers"));
        story.addStakeholder(stakeholder("Thor", "Odison"));

        story.removeStakeholder(stakeholder("Steve", "Rogers"));

        assertThat(story.getStakeholders()).containsExactlyInAnyOrder(stakeholder("Tony", "Stark"), stakeholder("Thor", "Odison"));
    }

    @Test
    void shouldRecognizeWhenRemovingNotStakeholderOfStory() {
        Story story = new Story();
        story.addStakeholder(stakeholder("Tony", "Stark"));
        story.addStakeholder(stakeholder("Steve", "Rogers"));

        assertThrows(RuntimeException.class, () -> story.removeStakeholder(stakeholder("Thor", "Odison")));

        assertThat(story.getStakeholders()).containsExactlyInAnyOrder(stakeholder("Tony", "Stark"), stakeholder("Steve", "Rogers"));
    }

    private Stakeholder stakeholder(String firstName, String lastName) {
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.setFirstName(firstName);
        stakeholder.setLastName(lastName);
        return stakeholder;
    }
}