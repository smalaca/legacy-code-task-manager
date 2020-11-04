package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.embedded.Assignee;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.APPROVED;
import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.DEFINED;
import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.DONE;
import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.IN_PROGRESS;
import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.RELEASED;
import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.TO_BE_DEFINED;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StoryRepository storyRepository;

    @AfterEach
    void deleteAll() {
        taskRepository.deleteAll();
        storyRepository.deleteAll();
    }

    @Test
    void shouldCreateTask() {
        Task task = new Task();
        task.setTitle("That's task");
        task.setDescription("Hold my beer");
        task.setStatus(APPROVED);
        Owner owner = new Owner();
        owner.setFirstName("The");
        owner.setLastName("Owner");
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress("the.owner@task.fail.com");
        owner.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix("5678");
        phoneNumber.setNumber("1313131313");
        owner.setPhoneNumber(phoneNumber);
        task.setOwner(owner);
        Story story = new Story();
        story.setTitle("That's an story");
        storyRepository.save(story).getId();
        task.setStory(story);
        Assignee assignee = new Assignee();
        assignee.setFirstName("Winter");
        assignee.setLastName("Soldier");
        assignee.setTeamId(123L);
        task.setAssignee(assignee);

        Long id = taskRepository.save(task).getId();
        Task actual = taskRepository.findById(id).get();

        assertThat(actual.getTitle()).isEqualTo("That's task");
        assertThat(actual.getDescription()).isEqualTo("Hold my beer");
        assertThat(actual.getStatus()).isEqualTo(APPROVED);
        assertThat(actual.getOwner().getFirstName()).isEqualTo("The");
        assertThat(actual.getOwner().getLastName()).isEqualTo("Owner");
        assertThat(actual.getOwner().getEmailAddress().getEmailAddress()).isEqualTo("the.owner@task.fail.com");
        assertThat(actual.getOwner().getPhoneNumber().getPrefix()).isEqualTo("5678");
        assertThat(actual.getOwner().getPhoneNumber().getNumber()).isEqualTo("1313131313");
        assertThat(actual.getStory().getTitle()).isEqualTo("That's an story");
        assertThat(actual.getAssignee().getFirstName()).isEqualTo("Winter");
        assertThat(actual.getAssignee().getLastName()).isEqualTo("Soldier");
        assertThat(actual.getAssignee().getTeamId()).isEqualTo(123L);
    }

    @Test
    void shouldFindSpecificTask() {
        taskRepository.save(task("Task 1", TO_BE_DEFINED));
        taskRepository.save(task("Task 2", DEFINED));
        Long id = taskRepository.save(task("Task 3", IN_PROGRESS)).getId();
        taskRepository.save(task("Task 4", DONE));
        taskRepository.save(task("Task 5", APPROVED));
        taskRepository.save(task("Task 6", RELEASED));

        Task actual = taskRepository.findById(id).get();

        assertThat(actual.getTitle()).isEqualTo("Task 3");
        assertThat(actual.getStatus()).isEqualTo(IN_PROGRESS);
    }

    private Task task(String title, ToDoItemStatus status) {
        Task task = new Task();
        task.setTitle(title);
        task.setStatus(status);
        return task;
    }
}