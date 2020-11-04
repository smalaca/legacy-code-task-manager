package com.smalaca.taskamanager.api.rest;

import com.smalaca.taskamanager.dto.AssigneeDto;
import com.smalaca.taskamanager.dto.StakeholderDto;
import com.smalaca.taskamanager.dto.StoryDto;
import com.smalaca.taskamanager.dto.WatcherDto;
import com.smalaca.taskamanager.model.embedded.Assignee;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.UserName;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Team;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskamanager.repository.EpicRepository;
import com.smalaca.taskamanager.repository.StoryRepository;
import com.smalaca.taskamanager.repository.TeamRepository;
import com.smalaca.taskamanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.util.Optional;

import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.RELEASED;
import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.TO_BE_DEFINED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class StoryControllerTest {
    private static final String TITLE = "Title like all the others";
    private static final String DESCRIPTION = "Something have to be done";
    private static final ToDoItemStatus STATUS = RELEASED;
    private static final String FIRST_NAME = "Nick";
    private static final String LAST_NAME = "Fury";
    private static final String EMAIL_ADDRESS = "nick.fury@shield.marvel.com";
    private static final String PHONE_PREFIX = "567";
    private static final String PHONE_NUMBER = "133131313";
    private static final String ANOTHER_FIRST_NAME = "Maria";
    private static final String ANOTHER_LAST_NAME = "Hill";
    private static final String ANOTHER_EMAIL_ADDRESS = "maria.hill@shield.com";
    private static final String ANOTHER_PHONE_PREFIX = "909";
    private static final String ANOTHER_PHONE_NUMBER = "982478438743";
    private static final long STORY_ID = 13;
    private static final long OWNER_ID = 42;
    private static final long EPIC_ID = 69;
    private static final long WATCHER_ID = 5;
    private static final long STAKEHOLDER_ID = 17;
    private static final long TEAM_ID = 987;
    private static final long ASSIGNEE_ID = 476;

    private final StoryRepository storyRepository = mock(StoryRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final TeamRepository teamRepository = mock(TeamRepository.class);
    private final EpicRepository epicRepository = mock(EpicRepository.class);
    private final StoryController controller = new StoryController(storyRepository, userRepository, teamRepository, epicRepository);
    private final ArgumentCaptor<Story> storyCaptor = ArgumentCaptor.forClass(Story.class);

    @Test
    void shouldNotFindStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.empty());

        ResponseEntity<StoryDto> actual = controller.findById(STORY_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFindStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(existingStory()));

        ResponseEntity<StoryDto> actual = controller.findById(STORY_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        StoryDto dto = actual.getBody();
        assertThat(dto.getId()).isEqualTo(STORY_ID);
        assertThat(dto.getTitle()).isEqualTo(TITLE);
        assertThat(dto.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(dto.getStatus()).isEqualTo(STATUS.name());
        assertThat(dto.getOwnerFirstName()).isEqualTo(FIRST_NAME);
        assertThat(dto.getOwnerLastName()).isEqualTo(LAST_NAME);
        assertThat(dto.getOwnerEmailAddress()).isEqualTo(EMAIL_ADDRESS);
        assertThat(dto.getOwnerPhoneNumberPrefix()).isEqualTo(PHONE_PREFIX);
        assertThat(dto.getOwnerPhoneNumberNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(dto.getEpicId()).isEqualTo(EPIC_ID);
        assertThat(dto.getWatchers())
                .hasSize(1)
                .anySatisfy(watcherDto -> {
                    assertThat(watcherDto.getFirstName()).isEqualTo(ANOTHER_FIRST_NAME);
                    assertThat(watcherDto.getLastName()).isEqualTo(ANOTHER_LAST_NAME);
                    assertThat(watcherDto.getEmailAddress()).isEqualTo(ANOTHER_EMAIL_ADDRESS);
                    assertThat(watcherDto.getPhonePrefix()).isEqualTo(ANOTHER_PHONE_PREFIX);
                    assertThat(watcherDto.getPhoneNumber()).isEqualTo(ANOTHER_PHONE_NUMBER);
                });
        assertThat(dto.getStakeholders())
                .hasSize(1)
                .anySatisfy(stakeholderDto -> {
                    assertThat(stakeholderDto.getFirstName()).isEqualTo(ANOTHER_FIRST_NAME);
                    assertThat(stakeholderDto.getLastName()).isEqualTo(ANOTHER_LAST_NAME);
                    assertThat(stakeholderDto.getEmailAddress()).isEqualTo(ANOTHER_EMAIL_ADDRESS);
                    assertThat(stakeholderDto.getPhonePrefix()).isEqualTo(ANOTHER_PHONE_PREFIX);
                    assertThat(stakeholderDto.getPhoneNumber()).isEqualTo(ANOTHER_PHONE_NUMBER);
                });
        assertThat(dto.getAssignee().getFirstName()).isEqualTo(ANOTHER_FIRST_NAME);
        assertThat(dto.getAssignee().getLastName()).isEqualTo(ANOTHER_LAST_NAME);
        assertThat(dto.getAssignee().getTeamId()).isEqualTo(TEAM_ID);
    }

    private Story existingStory() {
        Story story = storyWithoutOwner();
        Owner owner = new Owner();
        owner.setFirstName(FIRST_NAME);
        owner.setLastName(LAST_NAME);
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress(EMAIL_ADDRESS);
        owner.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix(PHONE_PREFIX);
        phoneNumber.setNumber(PHONE_NUMBER);
        owner.setPhoneNumber(phoneNumber);
        story.setOwner(owner);
        story.addWatcher(watcher());
        story.addStakeholder(stakeholder());
        story.setAssignee(assignee());

        return story;
    }

    @Test
    void shouldNotCreateInCaseOfNotExistingEpic() {
        given(epicRepository.existsById(EPIC_ID)).willReturn(false);
        given(epicRepository.findById(EPIC_ID)).willReturn(Optional.of(epic()));
        given(userRepository.findById(OWNER_ID)).willReturn(Optional.of(owner()));

        ResponseEntity<Long> actual = controller.create(newStoryDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldNotCreateInCaseOfNotExistingUser() {
        given(epicRepository.existsById(EPIC_ID)).willReturn(true);
        given(epicRepository.findById(EPIC_ID)).willReturn(Optional.of(epic()));
        given(userRepository.findById(OWNER_ID)).willReturn(Optional.empty());

        ResponseEntity<Long> actual = controller.create(newStoryDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldCreateEpic() {
        given(epicRepository.existsById(EPIC_ID)).willReturn(true);
        given(epicRepository.findById(EPIC_ID)).willReturn(Optional.of(epic()));
        given(userRepository.findById(OWNER_ID)).willReturn(Optional.of(owner()));
        given(storyRepository.save(any())).willReturn(storyWithId());

        ResponseEntity<Long> actual = controller.create(newStoryDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(STORY_ID);
        then(storyRepository).should().save(storyCaptor.capture());
        Story story = storyCaptor.getValue();
        assertThat(story.getTitle()).isEqualTo(TITLE);
        assertThat(story.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(story.getStatus()).isEqualTo(STATUS);
        assertThat(story.getOwner().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(story.getOwner().getLastName()).isEqualTo(LAST_NAME);
        assertThat(story.getOwner().getEmailAddress().getEmailAddress()).isEqualTo(EMAIL_ADDRESS);
        assertThat(story.getOwner().getPhoneNumber().getPrefix()).isEqualTo(PHONE_PREFIX);
        assertThat(story.getOwner().getPhoneNumber().getNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(story.getEpic().getId()).isEqualTo(EPIC_ID);
    }

    private StoryDto newStoryDto() {
        StoryDto dto = new StoryDto();
        dto.setTitle(TITLE);
        dto.setDescription(DESCRIPTION);
        dto.setStatus(STATUS.name());
        dto.setOwnerId(OWNER_ID);
        dto.setEpicId(EPIC_ID);

        return dto;
    }

    @Test
    void shouldNotUpdateNotExistingStory() {
        given(storyRepository.existsById(STORY_ID)).willReturn(false);

        ResponseEntity<Void> actual = controller.update(STORY_ID, updateStoryDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotUpdateWithNonExistingUser() {
        given(storyRepository.existsById(STORY_ID)).willReturn(true);
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(storyWithoutOwner()));
        given(userRepository.existsById(OWNER_ID)).willReturn(false);

        ResponseEntity<Void> actual = controller.update(STORY_ID, updateStoryDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldUpdateExistingStoryWithOwner() {
        given(storyRepository.existsById(STORY_ID)).willReturn(true);
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(story()));

        ResponseEntity<Void> actual = controller.update(STORY_ID, updateStoryDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(storyRepository).should().save(storyCaptor.capture());
        Story story = storyCaptor.getValue();
        assertThat(story.getTitle()).isEqualTo(TITLE);
        assertThat(story.getDescription()).isEqualTo("new description");
        assertThat(story.getStatus()).isEqualTo(TO_BE_DEFINED);
        assertThat(story.getOwner().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(story.getOwner().getLastName()).isEqualTo(LAST_NAME);
        assertThat(story.getOwner().getEmailAddress().getEmailAddress()).isEqualTo("john.doe@test.com");
        assertThat(story.getOwner().getPhoneNumber().getPrefix()).isEqualTo("9900");
        assertThat(story.getOwner().getPhoneNumber().getNumber()).isEqualTo("8877665544");
        assertThat(story.getEpic().getId()).isEqualTo(EPIC_ID);
    }

    @Test
    void shouldUpdateExistingStoryWithoutOwner() {
        given(storyRepository.existsById(STORY_ID)).willReturn(true);
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(storyWithoutOwner()));
        given(userRepository.existsById(OWNER_ID)).willReturn(true);
        given(userRepository.findById(OWNER_ID)).willReturn(Optional.of(owner()));

        ResponseEntity<Void> actual = controller.update(STORY_ID, updateStoryDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(storyRepository).should().save(storyCaptor.capture());
        Story story = storyCaptor.getValue();
        assertThat(story.getTitle()).isEqualTo(TITLE);
        assertThat(story.getDescription()).isEqualTo("new description");
        assertThat(story.getStatus()).isEqualTo(TO_BE_DEFINED);
        assertThat(story.getOwner().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(story.getOwner().getLastName()).isEqualTo(LAST_NAME);
        assertThat(story.getOwner().getEmailAddress().getEmailAddress()).isEqualTo(EMAIL_ADDRESS);
        assertThat(story.getOwner().getPhoneNumber().getPrefix()).isEqualTo(PHONE_PREFIX);
        assertThat(story.getOwner().getPhoneNumber().getNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(story.getEpic().getId()).isEqualTo(EPIC_ID);
    }

    private StoryDto updateStoryDto() {
        StoryDto dto = new StoryDto();
        dto.setDescription("new description");
        dto.setStatus("TO_BE_DEFINED");
        dto.setOwnerEmailAddress("john.doe@test.com");
        dto.setOwnerPhoneNumberPrefix("9900");
        dto.setOwnerPhoneNumberNumber("8877665544");
        dto.setOwnerId(OWNER_ID);

        return dto;
    }

    private Story story() {
        Story story = storyWithoutOwner();
        Owner owner = new Owner();
        owner.setFirstName(FIRST_NAME);
        owner.setLastName(LAST_NAME);
        EmailAddress emailAddress = new EmailAddress();
        owner.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        owner.setPhoneNumber(phoneNumber);
        story.setOwner(owner);

        return story;
    }

    private Story storyWithoutOwner() {
        Story story = storyWithId();
        story.setTitle(TITLE);
        story.setDescription(DESCRIPTION);
        story.setStatus(STATUS);
        story.setEpic(epic());

        return story;
    }

    @Test
    void shouldNotDeleteNotExistingStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.delete(STORY_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldDeleteStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(storyWithId()));

        ResponseEntity<Void> actual = controller.delete(STORY_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(storyRepository).should().delete(storyCaptor.capture());
        assertThat(storyCaptor.getValue().getId()).isEqualTo(STORY_ID);
    }

    @Test
    void shouldNotAddWatcherToNotExistingStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addWatcher(STORY_ID, watcherDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAddNotExistingWatcherToStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(storyWithId()));
        given(userRepository.findById(WATCHER_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addWatcher(STORY_ID, watcherDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldAddWatcherToStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(storyWithId()));
        given(userRepository.findById(WATCHER_ID)).willReturn(Optional.of(userWithId(WATCHER_ID)));

        ResponseEntity<Void> actual = controller.addWatcher(STORY_ID, watcherDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(storyRepository).should().save(storyCaptor.capture());
        assertThat(storyCaptor.getValue().getWatchers())
                .hasSize(1)
                .anySatisfy(watcher -> {
                    assertThat(watcher.getFirstName()).isEqualTo(ANOTHER_FIRST_NAME);
                    assertThat(watcher.getLastName()).isEqualTo(ANOTHER_LAST_NAME);
                    assertThat(watcher.getEmailAddress().getEmailAddress()).isEqualTo(ANOTHER_EMAIL_ADDRESS);
                    assertThat(watcher.getPhoneNumber().getPrefix()).isEqualTo(ANOTHER_PHONE_PREFIX);
                    assertThat(watcher.getPhoneNumber().getNumber()).isEqualTo(ANOTHER_PHONE_NUMBER);
                });
    }

    @Test
    void shouldNotRemoveWatcherFromNotExistingStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.removeWatcher(STORY_ID, WATCHER_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void shouldNotRemoveNotExistingWatcherFromStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(storyWithWatcher()));
        given(userRepository.findById(WATCHER_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.removeWatcher(STORY_ID, WATCHER_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldRemoveWatcherFromStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(storyWithWatcher()));
        given(userRepository.findById(WATCHER_ID)).willReturn(Optional.of(userWithId(WATCHER_ID)));

        ResponseEntity<Void> actual = controller.removeWatcher(STORY_ID, WATCHER_ID);


        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(storyRepository).should().save(storyCaptor.capture());
        assertThat(storyCaptor.getValue().getWatchers()).isEmpty();
    }

    private Story storyWithWatcher() {
        Story story = storyWithId();
        story.addWatcher(watcher());

        return story;
    }

    private Watcher watcher() {
        Watcher watcher = new Watcher();
        watcher.setFirstName(ANOTHER_FIRST_NAME);
        watcher.setLastName(ANOTHER_LAST_NAME);
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress(ANOTHER_EMAIL_ADDRESS);
        watcher.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix(ANOTHER_PHONE_PREFIX);
        phoneNumber.setNumber(ANOTHER_PHONE_NUMBER);
        watcher.setPhoneNumber(phoneNumber);
        return watcher;
    }

    private WatcherDto watcherDto() {
        WatcherDto dto = new WatcherDto();
        dto.setId(WATCHER_ID);
        return dto;
    }

    @Test
    void shouldNotAddStakeholderToNotExistingStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addStakeholder(STORY_ID, stakeholderDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAddNotExistingStakeholderToStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(storyWithId()));
        given(userRepository.findById(STAKEHOLDER_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addStakeholder(STORY_ID, stakeholderDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldAddStakeholderToStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(storyWithId()));
        given(userRepository.findById(STAKEHOLDER_ID)).willReturn(Optional.of(userWithId(STAKEHOLDER_ID)));

        ResponseEntity<Void> actual = controller.addStakeholder(STORY_ID, stakeholderDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(storyRepository).should().save(storyCaptor.capture());
        assertThat(storyCaptor.getValue().getStakeholders())
                .hasSize(1)
                .anySatisfy(stakeholder -> {
                    assertThat(stakeholder.getFirstName()).isEqualTo(ANOTHER_FIRST_NAME);
                    assertThat(stakeholder.getLastName()).isEqualTo(ANOTHER_LAST_NAME);
                    assertThat(stakeholder.getEmailAddress().getEmailAddress()).isEqualTo(ANOTHER_EMAIL_ADDRESS);
                    assertThat(stakeholder.getPhoneNumber().getPrefix()).isEqualTo(ANOTHER_PHONE_PREFIX);
                    assertThat(stakeholder.getPhoneNumber().getNumber()).isEqualTo(ANOTHER_PHONE_NUMBER);
                });
    }

    @Test
    void shouldNotRemoveStakeholderFromNotExistingStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.removeStakeholder(STORY_ID, STAKEHOLDER_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotRemoveNotExistingStakeholderFromStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(storyWithStakeholder()));
        given(userRepository.findById(STAKEHOLDER_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.removeStakeholder(STORY_ID, STAKEHOLDER_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldRemoveStakeholderFromStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(storyWithStakeholder()));
        given(userRepository.findById(STAKEHOLDER_ID)).willReturn(Optional.of(userWithId(STAKEHOLDER_ID)));

        ResponseEntity<Void> actual = controller.removeStakeholder(STORY_ID, STAKEHOLDER_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(storyRepository).should().save(storyCaptor.capture());
        assertThat(storyCaptor.getValue().getStakeholders()).isEmpty();
    }

    private Story storyWithStakeholder() {
        Story story = storyWithId();
        story.addStakeholder(stakeholder());

        return story;
    }

    private Stakeholder stakeholder() {
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.setFirstName(ANOTHER_FIRST_NAME);
        stakeholder.setLastName(ANOTHER_LAST_NAME);
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress(ANOTHER_EMAIL_ADDRESS);
        stakeholder.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix(ANOTHER_PHONE_PREFIX);
        phoneNumber.setNumber(ANOTHER_PHONE_NUMBER);
        stakeholder.setPhoneNumber(phoneNumber);
        return stakeholder;
    }

    private StakeholderDto stakeholderDto() {
        StakeholderDto dto = new StakeholderDto();
        dto.setId(STAKEHOLDER_ID);
        return dto;
    }

    @Test
    void shouldNotAddAssigneeToNotExistingStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addAssignee(STORY_ID, assigneeDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAddNotExistingAssigneeToStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(storyWithId()));
        given(userRepository.findById(ASSIGNEE_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addAssignee(STORY_ID, assigneeDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldNotAddAssigneeWithNotExistingTeamToStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(storyWithId()));
        given(userRepository.findById(ASSIGNEE_ID)).willReturn(Optional.of(userWithId(ASSIGNEE_ID)));
        given(teamRepository.findById(TEAM_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addAssignee(STORY_ID, assigneeDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FAILED_DEPENDENCY);
    }

    @Test
    void shouldAddAssigneeToStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(storyWithId()));
        given(userRepository.findById(ASSIGNEE_ID)).willReturn(Optional.of(userWithId(ASSIGNEE_ID)));
        given(teamRepository.findById(TEAM_ID)).willReturn(Optional.of(teamWithId(TEAM_ID)));

        ResponseEntity<Void> actual = controller.addAssignee(STORY_ID, assigneeDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(storyRepository).should().save(storyCaptor.capture());
        assertThat(storyCaptor.getValue().getAssignee().getFirstName()).isEqualTo(ANOTHER_FIRST_NAME);
        assertThat(storyCaptor.getValue().getAssignee().getLastName()).isEqualTo(ANOTHER_LAST_NAME);
        assertThat(storyCaptor.getValue().getAssignee().getTeamId()).isEqualTo(TEAM_ID);
    }

    @Test
    void shouldNotRemoveAssigneeFromNotExistingStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.removeAssignee(STORY_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldRemoveAssigneeFromStory() {
        given(storyRepository.findById(STORY_ID)).willReturn(Optional.of(storyWithAssignee()));

        ResponseEntity<Void> actual = controller.removeAssignee(STORY_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(storyRepository).should().save(storyCaptor.capture());
        assertThat(storyCaptor.getValue().getAssignee()).isNull();
    }

    private Story storyWithAssignee() {
        Story story = storyWithId();
        story.setAssignee(assignee());

        return story;
    }

    private Assignee assignee() {
        Assignee assignee = new Assignee();
        assignee.setFirstName(ANOTHER_FIRST_NAME);
        assignee.setLastName(ANOTHER_LAST_NAME);
        assignee.setTeamId(TEAM_ID);
        return assignee;
    }

    private AssigneeDto assigneeDto() {
        AssigneeDto dto = new AssigneeDto();
        dto.setId(ASSIGNEE_ID);
        dto.setTeamId(TEAM_ID);
        return dto;
    }

    private Team teamWithId(long teamId) {
        return withId(new Team(), teamId);
    }

    private User userWithId(long userId) {
        return user(userId, ANOTHER_FIRST_NAME, ANOTHER_LAST_NAME, ANOTHER_EMAIL_ADDRESS, ANOTHER_PHONE_PREFIX, ANOTHER_PHONE_NUMBER);
    }

    private Story storyWithId() {
        Story story = withId(new Story(), STORY_ID);
        return story;
    }

    private Epic epic() {
        return withId(new Epic(), EPIC_ID);
    }

    private User owner() {
        return user(OWNER_ID, FIRST_NAME, LAST_NAME, EMAIL_ADDRESS, PHONE_PREFIX, PHONE_NUMBER);
    }

    private User user(long id, String firstName, String lastName, String email, String prefix, String number) {
        User user = withId(new User(), id);
        UserName userName = new UserName();
        userName.setFirstName(firstName);
        userName.setLastName(lastName);
        user.setUserName(userName);
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress(email);
        user.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix(prefix);
        phoneNumber.setNumber(number);
        user.setPhoneNumber(phoneNumber);
        return user;
    }

    private <T> T withId(T entity, long id) {
        try {
            Field fieldId = entity.getClass().getDeclaredField("id");
            fieldId.setAccessible(true);
            fieldId.set(entity, id);
            return entity;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}