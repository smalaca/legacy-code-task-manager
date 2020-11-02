package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Epic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EpicRelationsTest {
    @Autowired private EpicRepository repository;

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateAddWatchersToEpic() {
        Epic epic = existingEpic("Empyre");

        epic.addWatcher(watcher("Steve", "Rogers"));
        epic.addWatcher(watcher("Natasha", "Romanoff"));
        repository.save(epic);

        assertThat(findEpic(epic).getWatchers())
                .hasSize(2)
                .anySatisfy(hasWatcher("Steve", "Rogers"))
                .anySatisfy(hasWatcher("Natasha", "Romanoff"));
    }

    private Epic findEpic(Epic epic) {
        return repository.findById(epic.getId()).get();
    }

    @Test
    void shouldRemoveWatchersFromEpic() {
        Epic epic = existingEpic("Empyre");
        epic.addWatcher(watcher("Steve", "Rogers"));
        epic.addWatcher(watcher("Natasha", "Romanoff"));
        epic.addWatcher(watcher("Wanda", "Maximoff"));
        repository.save(epic);

        Epic saved = findEpic(epic);
        saved.removeWatcher(watcher("Natasha", "Romanoff"));
        repository.save(saved);

        assertThat(findEpic(epic).getWatchers())
                .hasSize(2)
                .anySatisfy(hasWatcher("Steve", "Rogers"))
                .anySatisfy(hasWatcher("Wanda", "Maximoff"));
    }

    private Epic existingEpic(String title) {
        Long id = repository.save(epic(title)).getId();
        return repository.findById(id).get();
    }

    private Epic epic(String title) {
        Epic epic = new Epic();
        epic.setTitle(title);
        return epic;
    }

    private Watcher watcher(String firstName, String lastName) {
        Watcher watcher = new Watcher();
        watcher.setFirstName(firstName);
        watcher.setLastName(lastName);
        return watcher;
    }

    private Consumer<Watcher> hasWatcher(String firstName, String lastName) {
        return actual -> {
            assertThat(actual.getFirstName()).isEqualTo(firstName);
            assertThat(actual.getLastName()).isEqualTo(lastName);
        };
    }
}
