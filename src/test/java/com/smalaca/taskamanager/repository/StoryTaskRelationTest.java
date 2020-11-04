package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StoryTaskRelationTest {
    private final Map<String, Long> storys = new HashMap<>();
    private final Map<String, Long> tasks = new HashMap<>();

    @Autowired private StoryRepository storyRepository;
    @Autowired private TaskRepository taskRepository;

    @BeforeEach
    void storysAndProductOwners() {
        givenStory("Avengers vs. X-Men");
        givenStory("Secret Wars");
        givenStory("Civil War");

        givenTask("AVX 1");
        givenTask("AVX 2");
        givenTask("AVX 3");
        givenTask("AVX 4");
        givenTask("Secret Wars 1");
        givenTask("Secret Wars 2");
        givenTask("Civil War 1");
    }

    private void givenStory(String name) {
        Story story = story(name);
        Long id = storyRepository.save(story).getId();

        storys.put(name, id);
    }

    private Story story(String name) {
        Story story = new Story();
        story.setTitle(name);
        return story;
    }

    private void givenTask(String name) {
        Task task = task(name);
        Long id = taskRepository.save(task).getId();
        tasks.put(name, id);
    }

    private Task task(String title) {
        Task task = new Task();
        task.setTitle(title);
        return task;
    }

    @AfterEach
    void removeAll() {
        storyRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldAssignProductOwnerToStory() {
        Story avengersVsXmen = findStoryByName("Avengers vs. X-Men");
        Story secretWars = findStoryByName("Secret Wars");
        Story civilWar = findStoryByName("Civil War");

        Task avx1 = findTaskByTitle("AVX 1");
        Task avx2 = findTaskByTitle("AVX 2");
        Task avx3 = findTaskByTitle("AVX 3");
        Task avx4 = findTaskByTitle("AVX 4");
        Task secretWars1 = findTaskByTitle("Secret Wars 1");
        Task secretWars2 = findTaskByTitle("Secret Wars 2");
        Task civilWar1 = findTaskByTitle("Civil War 1");

        avengersVsXmen.addTask(avx1);
        avengersVsXmen.addTask(avx2);
        avengersVsXmen.addTask(avx3);
        avengersVsXmen.addTask(avx4);
        avx1.setStory(avengersVsXmen);
        avx2.setStory(avengersVsXmen);
        avx3.setStory(avengersVsXmen);
        avx4.setStory(avengersVsXmen);

        secretWars.addTask(secretWars1);
        secretWars.addTask(secretWars2);
        secretWars1.setStory(secretWars);
        secretWars2.setStory(secretWars);

        civilWar.addTask(civilWar1);
        civilWar1.setStory(civilWar);

        storyRepository.saveAll(asList(avengersVsXmen, secretWars, civilWar));
        taskRepository.saveAll(asList(avx1, avx2, avx3, avx4, secretWars1, secretWars2, civilWar1));

        assertThat(asIds(findStoryByName("Avengers vs. X-Men").getTasks()))
                .containsExactlyInAnyOrder(avx1.getId(), avx2.getId(), avx3.getId(), avx4.getId());
        assertThat(asIds(findStoryByName("Secret Wars").getTasks()))
                .containsExactlyInAnyOrder(secretWars1.getId(), secretWars2.getId());
        assertThat(asIds(findStoryByName("Civil War").getTasks()))
                .containsExactlyInAnyOrder(civilWar1.getId());

        assertThat(findTaskByTitle("AVX 1").getStory().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findTaskByTitle("AVX 2").getStory().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findTaskByTitle("AVX 3").getStory().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findTaskByTitle("AVX 4").getStory().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findTaskByTitle("Secret Wars 1").getStory().getId()).isEqualTo(secretWars.getId());
        assertThat(findTaskByTitle("Secret Wars 2").getStory().getId()).isEqualTo(secretWars.getId());
        assertThat(findTaskByTitle("Civil War 1").getStory().getId()).isEqualTo(civilWar.getId());
    }

    private List<Long> asIds(List<Task> tasks) {
        return tasks.stream().map(Task::getId).collect(toList());
    }

    private Story findStoryByName(String name) {
        return storyRepository.findById(storys.get(name)).get();
    }

    private Task findTaskByTitle(String name) {
        return taskRepository.findById(tasks.get(name)).get();
    }
}
