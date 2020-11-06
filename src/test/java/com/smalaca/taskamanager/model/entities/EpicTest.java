package com.smalaca.taskamanager.model.entities;

import com.smalaca.taskamanager.model.embedded.Assignee;
import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.Watcher;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EpicTest {
    @Test
    void shouldCreateEpicWithAssignee() {
        Epic epic = new Epic();
        epic.setAssignee(assignee("Tony", "Stark"));

        assertThat(epic.getAssignee().getFirstName()).isEqualTo("Tony");
        assertThat(epic.getAssignee().getLastName()).isEqualTo("Stark");
        assertThat(epic.isAssigned()).isTrue();
    }

    @Test
    void shouldUnsetAssigneeFromEpic() {
        Epic epic = new Epic();
        epic.setAssignee(assignee("Tony", "Stark"));

        epic.setAssignee(null);

        assertThat(epic.getAssignee()).isNull();
        assertThat(epic.isAssigned()).isFalse();
    }

    private Assignee assignee(String firstName, String lastName) {
        Assignee assignee = new Assignee();
        assignee.setFirstName(firstName);
        assignee.setLastName(lastName);
        return assignee;
    }

    @Test
    void shouldCreateEpicWithWatchers() {
        Epic epic = new Epic();

        epic.addWatcher(watcher("Tony", "Stark"));
        epic.addWatcher(watcher("Steve", "Rogers"));
        epic.addWatcher(watcher("Thor", "Odison"));

        assertThat(epic.getWatchers()).containsExactlyInAnyOrder(watcher("Tony", "Stark"), watcher("Steve", "Rogers"), watcher("Thor", "Odison"));
    }

    @Test
    void shouldRemoveWatcherFromEpic() {
        Epic epic = new Epic();
        epic.addWatcher(watcher("Tony", "Stark"));
        epic.addWatcher(watcher("Steve", "Rogers"));
        epic.addWatcher(watcher("Thor", "Odison"));

        epic.removeWatcher(watcher("Steve", "Rogers"));

        assertThat(epic.getWatchers()).containsExactlyInAnyOrder(watcher("Tony", "Stark"), watcher("Thor", "Odison"));
    }

    @Test
    void shouldRecognizeWhenRemovingNotWatcherOfEpic() {
        Epic epic = new Epic();
        epic.addWatcher(watcher("Tony", "Stark"));
        epic.addWatcher(watcher("Steve", "Rogers"));

        assertThrows(RuntimeException.class, () -> epic.removeWatcher(watcher("Thor", "Odison")));

        assertThat(epic.getWatchers()).containsExactlyInAnyOrder(watcher("Tony", "Stark"), watcher("Steve", "Rogers"));
    }

    private Watcher watcher(String firstName, String lastName) {
        Watcher watcher = new Watcher();
        watcher.setFirstName(firstName);
        watcher.setLastName(lastName);
        return watcher;
    }

    @Test
    void shouldCreateEpicWithStakeholders() {
        Epic epic = new Epic();

        epic.addStakeholder(stakeholder("Tony", "Stark"));
        epic.addStakeholder(stakeholder("Steve", "Rogers"));
        epic.addStakeholder(stakeholder("Thor", "Odison"));

        assertThat(epic.getStakeholders()).containsExactlyInAnyOrder(stakeholder("Tony", "Stark"), stakeholder("Steve", "Rogers"), stakeholder("Thor", "Odison"));
    }

    @Test
    void shouldRemoveStakeholderFromEpic() {
        Epic epic = new Epic();
        epic.addStakeholder(stakeholder("Tony", "Stark"));
        epic.addStakeholder(stakeholder("Steve", "Rogers"));
        epic.addStakeholder(stakeholder("Thor", "Odison"));

        epic.removeStakeholder(stakeholder("Steve", "Rogers"));

        assertThat(epic.getStakeholders()).containsExactlyInAnyOrder(stakeholder("Tony", "Stark"), stakeholder("Thor", "Odison"));
    }

    @Test
    void shouldRecognizeWhenRemovingNotStakeholderOfEpic() {
        Epic epic = new Epic();
        epic.addStakeholder(stakeholder("Tony", "Stark"));
        epic.addStakeholder(stakeholder("Steve", "Rogers"));

        assertThrows(RuntimeException.class, () -> epic.removeStakeholder(stakeholder("Thor", "Odison")));

        assertThat(epic.getStakeholders()).containsExactlyInAnyOrder(stakeholder("Tony", "Stark"), stakeholder("Steve", "Rogers"));
    }

    private Stakeholder stakeholder(String firstName, String lastName) {
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.setFirstName(firstName);
        stakeholder.setLastName(lastName);
        return stakeholder;
    }
}