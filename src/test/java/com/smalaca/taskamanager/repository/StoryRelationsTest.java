package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Story;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StoryRelationsTest {
    @Autowired private StoryRepository repository;

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateAddWatchersToStory() {
        Story story = existingStory("Empyre");

        story.addWatcher(watcher("Steve", "Rogers"));
        story.addWatcher(watcher("Natasha", "Romanoff"));
        repository.save(story);

        assertThat(findStory(story).getWatchers())
                .hasSize(2)
                .anySatisfy(hasWatcher("Steve", "Rogers"))
                .anySatisfy(hasWatcher("Natasha", "Romanoff"));
    }

    @Test
    void shouldRemoveWatchersFromStory() {
        Story story = existingStory("Empyre");
        story.addWatcher(watcher("Steve", "Rogers"));
        story.addWatcher(watcher("Natasha", "Romanoff"));
        story.addWatcher(watcher("Wanda", "Maximoff"));
        repository.save(story);

        Story saved = findStory(story);
        saved.removeWatcher(watcher("Natasha", "Romanoff"));
        repository.save(saved);

        assertThat(findStory(story).getWatchers())
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
    void shouldCreateAddStakeholdersToStory() {
        Story story = existingStory("Empyre");

        story.addStakeholder(stakeholder("Steve", "Rogers"));
        story.addStakeholder(stakeholder("Natasha", "Romanoff"));
        repository.save(story);

        assertThat(findStory(story).getStakeholders())
                .hasSize(2)
                .anySatisfy(hasStakeholder("Steve", "Rogers"))
                .anySatisfy(hasStakeholder("Natasha", "Romanoff"));
    }

    @Test
    void shouldRemoveStakeholdersFromStory() {
        Story story = existingStory("Empyre");
        story.addStakeholder(stakeholder("Steve", "Rogers"));
        story.addStakeholder(stakeholder("Natasha", "Romanoff"));
        story.addStakeholder(stakeholder("Wanda", "Maximoff"));
        repository.save(story);

        Story saved = findStory(story);
        saved.removeStakeholder(stakeholder("Natasha", "Romanoff"));
        repository.save(saved);

        assertThat(findStory(story).getStakeholders())
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

    private Story findStory(Story story) {
        return repository.findById(story.getId()).get();
    }

    private Story existingStory(String title) {
        Long id = repository.save(story(title)).getId();
        return repository.findById(id).get();
    }

    private Story story(String title) {
        Story story = new Story();
        story.setTitle(title);
        return story;
    }
}
