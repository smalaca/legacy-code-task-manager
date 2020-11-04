package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.embedded.Assignee;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.Story;
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
class StoryRepositoryTest {
    @Autowired
    private StoryRepository storyRepository;
    @Autowired
    private EpicRepository epicRepository;

    @AfterEach
    void deleteAll() {
        storyRepository.deleteAll();
        epicRepository.deleteAll();
    }

    @Test
    void shouldCreateStory() {
        Story story = new Story();
        story.setTitle("That's story");
        story.setDescription("Hold my beer");
        story.setStatus(APPROVED);
        Owner owner = new Owner();
        owner.setFirstName("The");
        owner.setLastName("Owner");
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress("the.owner@story.fail.com");
        owner.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix("5678");
        phoneNumber.setNumber("1313131313");
        owner.setPhoneNumber(phoneNumber);
        story.setOwner(owner);
        Epic epic = new Epic();
        epic.setTitle("That's an epic");
        epicRepository.save(epic).getId();
        story.setEpic(epic);
        Assignee assignee = new Assignee();
        assignee.setFirstName("Winter");
        assignee.setLastName("Soldier");
        assignee.setTeamId(123L);
        story.setAssignee(assignee);

        Long id = storyRepository.save(story).getId();
        Story actual = storyRepository.findById(id).get();

        assertThat(actual.getTitle()).isEqualTo("That's story");
        assertThat(actual.getDescription()).isEqualTo("Hold my beer");
        assertThat(actual.getStatus()).isEqualTo(APPROVED);
        assertThat(actual.getOwner().getFirstName()).isEqualTo("The");
        assertThat(actual.getOwner().getLastName()).isEqualTo("Owner");
        assertThat(actual.getOwner().getEmailAddress().getEmailAddress()).isEqualTo("the.owner@story.fail.com");
        assertThat(actual.getOwner().getPhoneNumber().getPrefix()).isEqualTo("5678");
        assertThat(actual.getOwner().getPhoneNumber().getNumber()).isEqualTo("1313131313");
        assertThat(actual.getEpic().getTitle()).isEqualTo("That's an epic");
        assertThat(actual.getAssignee().getFirstName()).isEqualTo("Winter");
        assertThat(actual.getAssignee().getLastName()).isEqualTo("Soldier");
        assertThat(actual.getAssignee().getTeamId()).isEqualTo(123L);
    }

    @Test
    void shouldFindSpecificStory() {
        storyRepository.save(story("Story 1", TO_BE_DEFINED));
        storyRepository.save(story("Story 2", DEFINED));
        Long id = storyRepository.save(story("Story 3", IN_PROGRESS)).getId();
        storyRepository.save(story("Story 4", DONE));
        storyRepository.save(story("Story 5", APPROVED));
        storyRepository.save(story("Story 6", RELEASED));

        Story actual = storyRepository.findById(id).get();

        assertThat(actual.getTitle()).isEqualTo("Story 3");
        assertThat(actual.getStatus()).isEqualTo(IN_PROGRESS);
    }

    private Story story(String title, ToDoItemStatus status) {
        Story story = new Story();
        story.setTitle(title);
        story.setStatus(status);
        return story;
    }
}