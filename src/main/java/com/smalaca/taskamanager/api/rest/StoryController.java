package com.smalaca.taskamanager.api.rest;


import com.smalaca.taskamanager.dto.AssigneeDto;
import com.smalaca.taskamanager.dto.StakeholderDto;
import com.smalaca.taskamanager.dto.StoryDto;
import com.smalaca.taskamanager.dto.WatcherDto;
import com.smalaca.taskamanager.exception.ProjectNotFoundException;
import com.smalaca.taskamanager.exception.StoryDoesNotExistException;
import com.smalaca.taskamanager.exception.TeamNotFoundException;
import com.smalaca.taskamanager.exception.UserNotFoundException;
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
import com.smalaca.taskamanager.service.ToDoItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/story")
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:NestedTryDepth", "checkstyle:NestedIfDepth", "PMD.CollapsibleIfStatements"})
public class StoryController {
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final EpicRepository epicRepository;
    private final ToDoItemService toDoItemService;

    public StoryController(
            StoryRepository storyRepository, UserRepository userRepository, TeamRepository teamRepository,
            EpicRepository epicRepository, ToDoItemService toDoItemService) {
        this.storyRepository = storyRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.epicRepository = epicRepository;
        this.toDoItemService = toDoItemService;
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<StoryDto> findById(@PathVariable Long id) {
        Optional<Story> found = storyRepository.findById(id);

        if (found.isPresent()) {
            Story story = found.get();
            StoryDto storyDto = new StoryDto();

            storyDto.setId(story.getId());
            storyDto.setTitle(story.getTitle());
            storyDto.setDescription(story.getDescription());
            storyDto.setStatus(story.getStatus().name());

            if (story.getEpic() != null) {
                Epic project = story.getEpic();
                storyDto.setEpicId(project.getId());
            }

            Owner owner = story.getOwner();

            if (owner != null) {
                storyDto.setOwnerFirstName(owner.getFirstName());
                storyDto.setOwnerLastName(owner.getLastName());

                PhoneNumber phoneNumber = owner.getPhoneNumber();

                if (phoneNumber != null) {
                    storyDto.setOwnerPhoneNumberPrefix(phoneNumber.getPrefix());
                    storyDto.setOwnerPhoneNumberNumber(phoneNumber.getNumber());
                }

                EmailAddress emailAddress = owner.getEmailAddress();

                if (emailAddress != null) {
                    storyDto.setOwnerEmailAddress(emailAddress.getEmailAddress());
                }
            }

            List<WatcherDto> watchers = story.getWatchers().stream().map(watcher -> {
                WatcherDto watcherDto = new WatcherDto();
                if (watcher.getEmailAddress() != null) {
                    EmailAddress emailAddress = watcher.getEmailAddress();
                    watcherDto.setEmailAddress(emailAddress.getEmailAddress());
                }
                watcherDto.setFirstName(watcher.getFirstName());
                watcherDto.setLastName(watcher.getLastName());
                if (watcher.getPhoneNumber() != null) {
                    PhoneNumber phoneNumber = watcher.getPhoneNumber();
                    watcherDto.setPhonePrefix(phoneNumber.getPrefix());
                    watcherDto.setPhoneNumber(phoneNumber.getNumber());
                }
                return watcherDto;
            }).collect(Collectors.toList());
            storyDto.setWatchers(watchers);
            
            if (story.getAssignee() != null) {
                AssigneeDto assigneeDto = new AssigneeDto();
                assigneeDto.setFirstName(story.getAssignee().getFirstName());
                assigneeDto.setLastName(story.getAssignee().getLastName());
                assigneeDto.setTeamId(story.getAssignee().getTeamId());
                storyDto.setAssignee(assigneeDto);
            }

            List<StakeholderDto> stakeholders = story.getStakeholders().stream().map(stakeholder -> {
                StakeholderDto stakeholderDto = new StakeholderDto();
                if (stakeholder.getEmailAddress() != null) {
                    EmailAddress emailAddress = stakeholder.getEmailAddress();
                    stakeholderDto.setEmailAddress(emailAddress.getEmailAddress());
                }
                stakeholderDto.setFirstName(stakeholder.getFirstName());
                stakeholderDto.setLastName(stakeholder.getLastName());
                if (stakeholder.getPhoneNumber() != null) {
                    PhoneNumber phoneNumber = stakeholder.getPhoneNumber();
                    stakeholderDto.setPhonePrefix(phoneNumber.getPrefix());
                    stakeholderDto.setPhoneNumber(phoneNumber.getNumber());
                }
                return stakeholderDto;
            }).collect(Collectors.toList());
            storyDto.setStakeholders(stakeholders);

            return ResponseEntity.ok(storyDto);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody StoryDto dto) {
        Story story = new Story();
        story.setTitle(dto.getTitle());
        story.setDescription(dto.getDescription());
        story.setStatus(ToDoItemStatus.valueOf(dto.getStatus()));

        if (dto.getOwnerId() != null) {
            Optional<User> found = userRepository.findById(dto.getOwnerId());

            if (found.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            } else {
                User user = found.get();
                Owner owner = new Owner();

                if (user.getEmailAddress() != null) {
                    EmailAddress emailAddress = new EmailAddress();
                    emailAddress.setEmailAddress(user.getEmailAddress().getEmailAddress());
                    owner.setEmailAddress(emailAddress);
                }

                owner.setFirstName(user.getUserName().getFirstName());
                owner.setLastName(user.getUserName().getLastName());

                if (user.getPhoneNumber() != null) {
                    PhoneNumber phoneNumber = new PhoneNumber();
                    phoneNumber.setNumber(user.getPhoneNumber().getNumber());
                    phoneNumber.setPrefix(user.getPhoneNumber().getPrefix());
                    owner.setPhoneNumber(phoneNumber);
                }

                story.setOwner(owner);
            }
        }

        Epic epic;
        try {
            if (!epicRepository.existsById(dto.getEpicId())) {
                throw new ProjectNotFoundException();
            }

            epic = epicRepository.findById(dto.getEpicId()).get();
        } catch (ProjectNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }

        story.setEpic(epic);

        Story saved = storyRepository.save(story);

        return ResponseEntity.ok(saved.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable long id, @RequestBody StoryDto dto) {
        boolean runService = false;
        Story story;

        try {
            story = findById(id);
        } catch (StoryDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }

        if (dto.getDescription() != null) {
            story.setDescription(dto.getDescription());
        }

        if (story.getOwner() != null) {
            Owner owner = new Owner();
            owner.setFirstName(story.getOwner().getFirstName());
            owner.setLastName(story.getOwner().getLastName());

            if (dto.getOwnerPhoneNumberPrefix() != null && dto.getOwnerPhoneNumberNumber() != null) {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setPrefix(dto.getOwnerPhoneNumberPrefix());
                phoneNumber.setNumber(dto.getOwnerPhoneNumberNumber());
                owner.setPhoneNumber(phoneNumber);
            }

            if (dto.getOwnerEmailAddress() != null) {
                EmailAddress emailAddress = new EmailAddress();
                emailAddress.setEmailAddress(dto.getOwnerEmailAddress());
                owner.setEmailAddress(emailAddress);
            }

            story.setOwner(owner);

        } else {
            if (dto.getOwnerId() != null) {
                boolean userExists = userRepository.existsById(dto.getOwnerId());

                if (userExists) {
                    User user = userRepository.findById(dto.getOwnerId()).get();
                    Owner owner = new Owner();

                    if (user.getPhoneNumber() != null) {
                        PhoneNumber phoneNumber = new PhoneNumber();
                        PhoneNumber userPhoneNumber = user.getPhoneNumber();
                        phoneNumber.setNumber(userPhoneNumber.getNumber());
                        phoneNumber.setPrefix(userPhoneNumber.getPrefix());
                        owner.setPhoneNumber(phoneNumber);
                    }

                    UserName userName = user.getUserName();
                    owner.setFirstName(userName.getFirstName());
                    owner.setLastName(userName.getLastName());

                    EmailAddress userEmailAddress = user.getEmailAddress();
                    if (userEmailAddress != null) {
                        EmailAddress emailAddress = new EmailAddress();
                        emailAddress.setEmailAddress(userEmailAddress.getEmailAddress());
                        owner.setEmailAddress(emailAddress);
                    }

                    story.setOwner(owner);
                } else {
                    return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
                }
            }
        }

        if (dto.getStatus() != null) {
            if (ToDoItemStatus.valueOf(dto.getStatus()) != story.getStatus()) {
                runService = true;
                story.setStatus(ToDoItemStatus.valueOf(dto.getStatus()));
            }
        }

        storyRepository.save(story);
        if (runService) {
            toDoItemService.processStory(story.getId());
        }

        return ResponseEntity.ok().build();
    }

    private Story findById(long id) {
        if (storyRepository.existsById(id)) {
            return storyRepository.findById(id).get();
        }

        throw new StoryDoesNotExistException();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        try {
            Optional<Story> found = storyRepository.findById(id);

            if (found.isEmpty()) {
                throw new StoryDoesNotExistException();
            }

            storyRepository.delete(found.get());

            return ResponseEntity.ok().build();
        } catch (StoryDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/watcher")
    public ResponseEntity<Void> addWatcher(@PathVariable long id, @RequestBody WatcherDto dto) {
        try {
            Story story = findStoryBy(id);

            try {
                User user = findUserBy(dto.getId());
                Watcher watcher = new Watcher();
                watcher.setLastName(user.getUserName().getLastName());
                watcher.setFirstName(user.getUserName().getFirstName());

                EmailAddress userEmailAddress = user.getEmailAddress();
                PhoneNumber userPhoneNumber = user.getPhoneNumber();

                if (userPhoneNumber != null) {
                    PhoneNumber phoneNumber = new PhoneNumber();
                    phoneNumber.setNumber(userPhoneNumber.getNumber());
                    phoneNumber.setPrefix(userPhoneNumber.getPrefix());
                    watcher.setPhoneNumber(phoneNumber);
                }

                if (userEmailAddress != null) {
                    EmailAddress emailAddress = new EmailAddress();
                    emailAddress.setEmailAddress(userEmailAddress.getEmailAddress());
                    watcher.setEmailAddress(emailAddress);
                }
                story.addWatcher(watcher);

                storyRepository.save(story);

            } catch (UserNotFoundException exception) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            }

            return ResponseEntity.ok().build();
        } catch (StoryDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{storyId}/watcher/{watcherId}")
    @Transactional
    public ResponseEntity<Void> removeWatcher(@PathVariable Long storyId, @PathVariable Long watcherId) {
        try {
            Story story = findStoryBy(storyId);
            User user = findUserBy(watcherId);

            Watcher watcher = new Watcher();

            if (user.getPhoneNumber() != null) {
                watcher.setPhoneNumber(new PhoneNumber());
                watcher.getPhoneNumber().setPrefix(user.getPhoneNumber().getPrefix());
                watcher.getPhoneNumber().setNumber(user.getPhoneNumber().getNumber());
            }

            watcher.setFirstName(user.getUserName().getFirstName());
            watcher.setLastName(user.getUserName().getLastName());

            if (user.getEmailAddress() != null) {
                watcher.setEmailAddress(new EmailAddress());
                watcher.getEmailAddress().setEmailAddress(user.getEmailAddress().getEmailAddress());
            }

            story.removeWatcher(watcher);

            storyRepository.save(story);

            return ResponseEntity.ok().build();
        } catch (StoryDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        } catch (UserNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @PutMapping("/{id}/stakeholder")
    public ResponseEntity<Void> addStakeholder(@PathVariable long id, @RequestBody StakeholderDto dto) {
        try {
            Story story = findStoryBy(id);

            try {
                User user = findUserBy(dto.getId());
                Stakeholder stakeholder = new Stakeholder();
                stakeholder.setLastName(user.getUserName().getLastName());
                stakeholder.setFirstName(user.getUserName().getFirstName());

                if (user.getPhoneNumber() != null) {
                    PhoneNumber phoneNumber = new PhoneNumber();
                    phoneNumber.setPrefix(user.getPhoneNumber().getPrefix());
                    phoneNumber.setNumber(user.getPhoneNumber().getNumber());
                    stakeholder.setPhoneNumber(phoneNumber);
                }

                if (user.getEmailAddress() != null) {
                    EmailAddress emailAddress = new EmailAddress();
                    emailAddress.setEmailAddress(user.getEmailAddress().getEmailAddress());
                    stakeholder.setEmailAddress(emailAddress);
                }

                story.addStakeholder(stakeholder);

                storyRepository.save(story);

            } catch (UserNotFoundException exception) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            }

            return ResponseEntity.ok().build();
        } catch (StoryDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{storyId}/stakeholder/{stakeholderId}")
    @Transactional
    public ResponseEntity<Void> removeStakeholder(@PathVariable Long storyId, @PathVariable Long stakeholderId) {
        try {
            Story story = findStoryBy(storyId);
            User user = findUserBy(stakeholderId);

            Stakeholder stakeholder = new Stakeholder();

            if (user.getPhoneNumber() != null) {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setNumber(user.getPhoneNumber().getNumber());
                phoneNumber.setPrefix(user.getPhoneNumber().getPrefix());
                stakeholder.setPhoneNumber(phoneNumber);
            }

            UserName userName = user.getUserName();
            stakeholder.setLastName(userName.getLastName());
            stakeholder.setFirstName(userName.getFirstName());

            if (user.getEmailAddress() != null) {
                EmailAddress emailAddress = new EmailAddress();
                emailAddress.setEmailAddress(user.getEmailAddress().getEmailAddress());
                stakeholder.setEmailAddress(emailAddress);
            }

            story.removeStakeholder(stakeholder);

            storyRepository.save(story);

            return ResponseEntity.ok().build();
        } catch (StoryDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        } catch (UserNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @PutMapping("/{id}/assignee")
    public ResponseEntity<Void> addAssignee(@PathVariable long id, @RequestBody AssigneeDto dto) {
        try {
            Story story = findStoryBy(id);

            try {
                User user = findUserBy(dto.getId());
                Assignee assignee = new Assignee();
                assignee.setFirstName(user.getUserName().getFirstName());
                assignee.setLastName(user.getUserName().getLastName());

                try {
                    findTeamBy(dto.getTeamId());
                    assignee.setTeamId(dto.getTeamId());
                    story.setAssignee(assignee);
                    storyRepository.save(story);
                } catch (TeamNotFoundException exception) {
                    return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
                }

            } catch (UserNotFoundException exception) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            }

            return ResponseEntity.ok().build();
        } catch (StoryDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    private void findTeamBy(Long id) {
        Optional<Team> found = teamRepository.findById(id);

        if (found.isEmpty()) {
            throw new TeamNotFoundException();
        }
    }

    @DeleteMapping("/{storyId}/assignee")
    @Transactional
    public ResponseEntity<Void> removeAssignee(@PathVariable Long storyId) {
        try {
            Story story = findStoryBy(storyId);
            story.setAssignee(null);

            storyRepository.save(story);

            return ResponseEntity.ok().build();
        } catch (StoryDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    private Story findStoryBy(long id) {
        Optional<Story> found = storyRepository.findById(id);

        if (found.isEmpty()) {
            throw new StoryDoesNotExistException();
        }

        return found.get();
    }

    private User findUserBy(Long id) {
        Optional<User> found = userRepository.findById(id);

        if (found.isEmpty()) {
            throw new UserNotFoundException();
        }

        return found.get();
    }
}
