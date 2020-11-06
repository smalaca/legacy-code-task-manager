package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.entities.Sprint;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SprintRelationsTest {
    @Autowired private SprintRepository sprintRepository;
    @Autowired private StoryRepository storyRepository;
    @Autowired private TaskRepository taskRepository;

    @Test
    void shouldAssignStoriesToSprint() {
        Sprint sprint1 = existingSprint("Sprint 1");
        Sprint sprint2 = existingSprint("Sprint 2");
        Sprint sprint3 = existingSprint("Sprint 3");
        Story story1 = existingStory("Story 1");
        Story story2 = existingStory("Story 2");
        Story story3 = existingStory("Story 3");
        Story story4 = existingStory("Story 4");
        Story story5 = existingStory("Story 5");

        sprint1.addStory(story1);
        story1.addSprint(sprint1);
        sprint1.addStory(story2);
        story2.addSprint(sprint1);
        story2.setCurrentSprint(sprint1);
        sprint1.addStory(story3);
        story3.addSprint(sprint1);
        story3.setCurrentSprint(sprint1);

        sprint2.addStory(story1);
        story1.addSprint(sprint2);
        story1.setCurrentSprint(sprint2);
        sprint2.addStory(story4);
        story4.addSprint(sprint2);
        story4.setCurrentSprint(sprint2);
        sprint2.addStory(story5);
        story5.addSprint(sprint2);
        story5.setCurrentSprint(sprint2);

        Long sprintId1 = sprintRepository.save(sprint1).getId();
        Long sprintId2 = sprintRepository.save(sprint2).getId();
        Long sprintId3 = sprintRepository.save(sprint3).getId();
        Long storyId1 = storyRepository.save(story1).getId();
        Long storyId2 = storyRepository.save(story2).getId();
        Long storyId3 = storyRepository.save(story3).getId();
        Long storyId4 = storyRepository.save(story4).getId();
        Long storyId5 = storyRepository.save(story5).getId();

        assertThat(namesOfStoriesFrom(sprintId1)).containsExactlyInAnyOrder("Story 1", "Story 2", "Story 3");
        assertThat(namesOfStoriesFrom(sprintId2)).containsExactlyInAnyOrder("Story 1", "Story 4", "Story 5");
        assertThat(namesOfStoriesFrom(sprintId3)).isEmpty();
        assertThat(namesOfSprintsFrom(storyId1)).containsExactlyInAnyOrder("Sprint 1", "Sprint 2");
        assertThat(storyRepository.findById(storyId1).get().getCurrentSprint().getName()).isEqualTo("Sprint 2");
        assertThat(namesOfSprintsFrom(storyId2)).containsExactlyInAnyOrder("Sprint 1");
        assertThat(storyRepository.findById(storyId2).get().getCurrentSprint().getName()).isEqualTo("Sprint 1");
        assertThat(namesOfSprintsFrom(storyId3)).containsExactlyInAnyOrder("Sprint 1");
        assertThat(storyRepository.findById(storyId3).get().getCurrentSprint().getName()).isEqualTo("Sprint 1");
        assertThat(namesOfSprintsFrom(storyId4)).containsExactlyInAnyOrder("Sprint 2");
        assertThat(storyRepository.findById(storyId4).get().getCurrentSprint().getName()).isEqualTo("Sprint 2");
        assertThat(namesOfSprintsFrom(storyId5)).containsExactlyInAnyOrder("Sprint 2");
        assertThat(storyRepository.findById(storyId5).get().getCurrentSprint().getName()).isEqualTo("Sprint 2");
    }

    private List<String> namesOfStoriesFrom(Long sprintId) {
        return sprintRepository.findById(sprintId).get().getStories().stream().map(Story::getTitle).collect(toList());
    }

    private List<String> namesOfSprintsFrom(Long storyId) {
        return storyRepository.findById(storyId).get().getSprints().stream().map(Sprint::getName).collect(toList());
    }

    private Story existingStory(String title) {
        Story story = new Story();
        story.setTitle(title);
        return storyRepository.save(story);
    }
    
    @Test
    void shouldAssignTasksToSprint() {
        Sprint sprint1 = existingSprint("Sprint 1");
        Sprint sprint2 = existingSprint("Sprint 2");
        Sprint sprint3 = existingSprint("Sprint 3");
        Task task1 = existingTask("Task 1");
        Task task2 = existingTask("Task 2");
        Task task3 = existingTask("Task 3");
        Task task4 = existingTask("Task 4");
        Task task5 = existingTask("Task 5");

        sprint1.addTask(task1);
        sprint1.addTask(task2);
        task2.setCurrentSprint(sprint1);
        sprint1.addTask(task3);
        task3.setCurrentSprint(sprint1);

        sprint2.addTask(task1);
        task1.setCurrentSprint(sprint2);
        sprint2.addTask(task4);
        task4.setCurrentSprint(sprint2);
        sprint2.addTask(task5);
        task5.setCurrentSprint(sprint2);

        Long sprintId1 = sprintRepository.save(sprint1).getId();
        Long sprintId2 = sprintRepository.save(sprint2).getId();
        Long sprintId3 = sprintRepository.save(sprint3).getId();
        Long taskId1 = taskRepository.save(task1).getId();
        Long taskId2 = taskRepository.save(task2).getId();
        Long taskId3 = taskRepository.save(task3).getId();
        Long taskId4 = taskRepository.save(task4).getId();
        Long taskId5 = taskRepository.save(task5).getId();

        assertThat(namesOfTasksFrom(sprintId1)).containsExactlyInAnyOrder("Task 1", "Task 2", "Task 3");
        assertThat(namesOfTasksFrom(sprintId2)).containsExactlyInAnyOrder("Task 1", "Task 4", "Task 5");
        assertThat(namesOfTasksFrom(sprintId3)).isEmpty();
        assertThat(taskRepository.findById(taskId1).get().getCurrentSprint().getName()).isEqualTo("Sprint 2");
        assertThat(taskRepository.findById(taskId2).get().getCurrentSprint().getName()).isEqualTo("Sprint 1");
        assertThat(taskRepository.findById(taskId3).get().getCurrentSprint().getName()).isEqualTo("Sprint 1");
        assertThat(taskRepository.findById(taskId4).get().getCurrentSprint().getName()).isEqualTo("Sprint 2");
        assertThat(taskRepository.findById(taskId5).get().getCurrentSprint().getName()).isEqualTo("Sprint 2");
    }

    private Task existingTask(String title) {
        Task task = new Task();
        task.setTitle(title);
        return taskRepository.save(task);
    }

    private Sprint existingSprint(String name) {
        Sprint sprint = new Sprint();
        sprint.setName(name);
        return sprintRepository.save(sprint);
    }

    private List<String> namesOfTasksFrom(Long sprintId) {
        return sprintRepository.findById(sprintId).get().getTasks().stream().map(Task::getTitle).collect(toList());
    }
}
