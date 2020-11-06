package com.smalaca.taskamanager.model.entities;

import com.smalaca.taskamanager.model.embedded.Assignee;
import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.Watcher;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskTest {
    @Test
    void shouldCreateTaskAsPartOfStory() {
        Story story = new Story();

        Task actual = new Task();
        actual.setStory(story);

        assertThat(actual.isSubtask()).isTrue();
    }
    @Test
    void shouldCreateStandaloneTask() {
        Task actual = new Task();

        assertThat(actual.isSubtask()).isFalse();
    }

    @Test
    void shouldCreateTaskWithProject() {
        Story story = new Story();
        Epic epic = new Epic();
        Project project = new Project();
        epic.setProject(project);
        story.setEpic(epic);
        Task actual = new Task();
        actual.setStory(story);

        assertThat(actual.getProject()).isSameAs(project);
    }

    @Test
    void shouldCreateTaskWithAssignee() {
        Task task = new Task();
        task.setAssignee(assignee("Tony", "Stark"));

        assertThat(task.getAssignee().getFirstName()).isEqualTo("Tony");
        assertThat(task.getAssignee().getLastName()).isEqualTo("Stark");
        assertThat(task.isAssigned()).isTrue();
    }

    @Test
    void shouldUnsetAssigneeFromTask() {
        Task task = new Task();
        task.setAssignee(assignee("Tony", "Stark"));

        task.setAssignee(null);

        assertThat(task.getAssignee()).isNull();
        assertThat(task.isAssigned()).isFalse();
    }

    private Assignee assignee(String firstName, String lastName) {
        Assignee assignee = new Assignee();
        assignee.setFirstName(firstName);
        assignee.setLastName(lastName);
        return assignee;
    }

    @Test
    void shouldCreateTaskWithWatchers() {
        Task task = new Task();

        task.addWatcher(watcher("Tony", "Stark"));
        task.addWatcher(watcher("Steve", "Rogers"));
        task.addWatcher(watcher("Thor", "Odison"));

        assertThat(task.getWatchers()).containsExactlyInAnyOrder(watcher("Tony", "Stark"), watcher("Steve", "Rogers"), watcher("Thor", "Odison"));
    }

    @Test
    void shouldRemoveWatcherFromTask() {
        Task task = new Task();
        task.addWatcher(watcher("Tony", "Stark"));
        task.addWatcher(watcher("Steve", "Rogers"));
        task.addWatcher(watcher("Thor", "Odison"));

        task.removeWatcher(watcher("Steve", "Rogers"));

        assertThat(task.getWatchers()).containsExactlyInAnyOrder(watcher("Tony", "Stark"), watcher("Thor", "Odison"));
    }

    @Test
    void shouldRecognizeWhenRemovingNotWatcherOfTask() {
        Task task = new Task();
        task.addWatcher(watcher("Tony", "Stark"));
        task.addWatcher(watcher("Steve", "Rogers"));

        assertThrows(RuntimeException.class, () -> task.removeWatcher(watcher("Thor", "Odison")));

        assertThat(task.getWatchers()).containsExactlyInAnyOrder(watcher("Tony", "Stark"), watcher("Steve", "Rogers"));
    }

    private Watcher watcher(String firstName, String lastName) {
        Watcher watcher = new Watcher();
        watcher.setFirstName(firstName);
        watcher.setLastName(lastName);
        return watcher;
    }

    @Test
    void shouldCreateTaskWithStakeholders() {
        Task task = new Task();

        task.addStakeholder(stakeholder("Tony", "Stark"));
        task.addStakeholder(stakeholder("Steve", "Rogers"));
        task.addStakeholder(stakeholder("Thor", "Odison"));

        assertThat(task.getStakeholders()).containsExactlyInAnyOrder(stakeholder("Tony", "Stark"), stakeholder("Steve", "Rogers"), stakeholder("Thor", "Odison"));
    }

    @Test
    void shouldRemoveStakeholderFromTask() {
        Task task = new Task();
        task.addStakeholder(stakeholder("Tony", "Stark"));
        task.addStakeholder(stakeholder("Steve", "Rogers"));
        task.addStakeholder(stakeholder("Thor", "Odison"));

        task.removeStakeholder(stakeholder("Steve", "Rogers"));

        assertThat(task.getStakeholders()).containsExactlyInAnyOrder(stakeholder("Tony", "Stark"), stakeholder("Thor", "Odison"));
    }

    @Test
    void shouldRecognizeWhenRemovingNotStakeholderOfTask() {
        Task task = new Task();
        task.addStakeholder(stakeholder("Tony", "Stark"));
        task.addStakeholder(stakeholder("Steve", "Rogers"));

        assertThrows(RuntimeException.class, () -> task.removeStakeholder(stakeholder("Thor", "Odison")));

        assertThat(task.getStakeholders()).containsExactlyInAnyOrder(stakeholder("Tony", "Stark"), stakeholder("Steve", "Rogers"));
    }

    private Stakeholder stakeholder(String firstName, String lastName) {
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.setFirstName(firstName);
        stakeholder.setLastName(lastName);
        return stakeholder;
    }
}