package com.smalaca.taskamanager.model.entities;

import com.smalaca.taskamanager.model.embedded.Watcher;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EpicTest {
    @Test
    void shouldCreateEpicWithWatchers() {
        Epic epic = new Epic();

        epic.addWatcher(user("Tony", "Stark"));
        epic.addWatcher(user("Steve", "Rogers"));
        epic.addWatcher(user("Thor", "Odison"));

        assertThat(epic.getWatchers()).containsExactlyInAnyOrder(user("Tony", "Stark"), user("Steve", "Rogers"), user("Thor", "Odison"));
    }

    @Test
    void shouldRemoveEpicMemberFromEpic() {
        Epic epic = new Epic();
        epic.addWatcher(user("Tony", "Stark"));
        epic.addWatcher(user("Steve", "Rogers"));
        epic.addWatcher(user("Thor", "Odison"));

        epic.removeWatcher(user("Steve", "Rogers"));

        assertThat(epic.getWatchers()).containsExactlyInAnyOrder(user("Tony", "Stark"), user("Thor", "Odison"));
    }

    @Test
    void shouldRecognizeWhenRemovingNotMemberOfEpic() {
        Epic epic = new Epic();
        epic.addWatcher(user("Tony", "Stark"));
        epic.addWatcher(user("Steve", "Rogers"));

        assertThrows(RuntimeException.class, () -> epic.removeWatcher(user("Thor", "Odison")));

        assertThat(epic.getWatchers()).containsExactlyInAnyOrder(user("Tony", "Stark"), user("Steve", "Rogers"));
    }

    private Watcher user(String firstName, String lastName) {
        Watcher watcher = new Watcher();
        watcher.setFirstName(firstName);
        watcher.setLastName(lastName);
        return watcher;
    }
}