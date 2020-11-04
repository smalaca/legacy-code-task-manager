package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRelationsTest {
    @Autowired private TaskRepository repository;

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateAddWatchersToTask() {
        Task task = existingTask("Empyre");

        task.addWatcher(watcher("Steve", "Rogers"));
        task.addWatcher(watcher("Natasha", "Romanoff"));
        repository.save(task);

        assertThat(findTask(task).getWatchers())
                .hasSize(2)
                .anySatisfy(hasWatcher("Steve", "Rogers"))
                .anySatisfy(hasWatcher("Natasha", "Romanoff"));
    }

    @Test
    void shouldRemoveWatchersFromTask() {
        Task task = existingTask("Empyre");
        task.addWatcher(watcher("Steve", "Rogers"));
        task.addWatcher(watcher("Natasha", "Romanoff"));
        task.addWatcher(watcher("Wanda", "Maximoff"));
        repository.save(task);

        Task saved = findTask(task);
        saved.removeWatcher(watcher("Natasha", "Romanoff"));
        repository.save(saved);

        assertThat(findTask(task).getWatchers())
                .hasSize(2)
                .anySatisfy(hasWatcher("Steve", "Rogers"))
                .anySatisfy(hasWatcher("Wanda", "Maximoff"));
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

    @Test
    void shouldCreateAddStakeholdersToTask() {
        Task task = existingTask("Empyre");

        task.addStakeholder(stakeholder("Steve", "Rogers"));
        task.addStakeholder(stakeholder("Natasha", "Romanoff"));
        repository.save(task);

        assertThat(findTask(task).getStakeholders())
                .hasSize(2)
                .anySatisfy(hasStakeholder("Steve", "Rogers"))
                .anySatisfy(hasStakeholder("Natasha", "Romanoff"));
    }

    @Test
    void shouldRemoveStakeholdersFromTask() {
        Task task = existingTask("Empyre");
        task.addStakeholder(stakeholder("Steve", "Rogers"));
        task.addStakeholder(stakeholder("Natasha", "Romanoff"));
        task.addStakeholder(stakeholder("Wanda", "Maximoff"));
        repository.save(task);

        Task saved = findTask(task);
        saved.removeStakeholder(stakeholder("Natasha", "Romanoff"));
        repository.save(saved);

        assertThat(findTask(task).getStakeholders())
                .hasSize(2)
                .anySatisfy(hasStakeholder("Steve", "Rogers"))
                .anySatisfy(hasStakeholder("Wanda", "Maximoff"));
    }

    private Stakeholder stakeholder(String firstName, String lastName) {
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.setFirstName(firstName);
        stakeholder.setLastName(lastName);
        return stakeholder;
    }

    private Consumer<Stakeholder> hasStakeholder(String firstName, String lastName) {
        return actual -> {
            assertThat(actual.getFirstName()).isEqualTo(firstName);
            assertThat(actual.getLastName()).isEqualTo(lastName);
        };
    }

    private Task findTask(Task task) {
        return repository.findById(task.getId()).get();
    }

    private Task existingTask(String title) {
        Long id = repository.save(task(title)).getId();
        return repository.findById(id).get();
    }

    private Task task(String title) {
        Task task = new Task();
        task.setTitle(title);
        return task;
    }
}
