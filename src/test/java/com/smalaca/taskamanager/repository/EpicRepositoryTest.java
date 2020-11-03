package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.embedded.Assignee;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.Project;
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
class EpicRepositoryTest {
    @Autowired
    private EpicRepository epicRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @AfterEach
    void deleteAll() {
        epicRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    void shouldCreateEpic() {
        Epic epic = new Epic();
        epic.setTitle("That's epic");
        epic.setDescription("Hold my beer");
        epic.setStatus(APPROVED);
        Owner owner = new Owner();
        owner.setFirstName("The");
        owner.setLastName("Owner");
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress("the.owner@epic.fail.com");
        owner.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix("5678");
        phoneNumber.setNumber("1313131313");
        owner.setPhoneNumber(phoneNumber);
        epic.setOwner(owner);
        Project project = new Project();
        project.setName("Mysterious Project");
        projectRepository.save(project).getId();
        epic.setProject(project);
        Assignee assignee = new Assignee();
        assignee.setFirstName("Winter");
        assignee.setLastName("Soldier");
        assignee.setTeamId(123L);
        epic.setAssignee(assignee);

        Long id = epicRepository.save(epic).getId();
        Epic actual = epicRepository.findById(id).get();

        assertThat(actual.getTitle()).isEqualTo("That's epic");
        assertThat(actual.getDescription()).isEqualTo("Hold my beer");
        assertThat(actual.getStatus()).isEqualTo(APPROVED);
        assertThat(actual.getOwner().getFirstName()).isEqualTo("The");
        assertThat(actual.getOwner().getLastName()).isEqualTo("Owner");
        assertThat(actual.getOwner().getEmailAddress().getEmailAddress()).isEqualTo("the.owner@epic.fail.com");
        assertThat(actual.getOwner().getPhoneNumber().getPrefix()).isEqualTo("5678");
        assertThat(actual.getOwner().getPhoneNumber().getNumber()).isEqualTo("1313131313");
        assertThat(actual.getProject().getName()).isEqualTo("Mysterious Project");
        assertThat(actual.getAssignee().getFirstName()).isEqualTo("Winter");
        assertThat(actual.getAssignee().getLastName()).isEqualTo("Soldier");
        assertThat(actual.getAssignee().getTeamId()).isEqualTo(123L);
    }

    @Test
    void shouldFindSpecificEpic() {
        epicRepository.save(epic("Epic 1", TO_BE_DEFINED));
        epicRepository.save(epic("Epic 2", DEFINED));
        Long id = epicRepository.save(epic("Epic 3", IN_PROGRESS)).getId();
        epicRepository.save(epic("Epic 4", DONE));
        epicRepository.save(epic("Epic 5", APPROVED));
        epicRepository.save(epic("Epic 6", RELEASED));

        Epic actual = epicRepository.findById(id).get();

        assertThat(actual.getTitle()).isEqualTo("Epic 3");
        assertThat(actual.getStatus()).isEqualTo(IN_PROGRESS);
    }

    private Epic epic(String title, ToDoItemStatus status) {
        Epic epic = new Epic();
        epic.setTitle(title);
        epic.setStatus(status);
        return epic;
    }
}