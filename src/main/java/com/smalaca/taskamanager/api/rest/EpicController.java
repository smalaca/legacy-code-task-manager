package com.smalaca.taskamanager.api.rest;


import com.smalaca.taskamanager.dto.EpicDto;
import com.smalaca.taskamanager.dto.StakeholderDto;
import com.smalaca.taskamanager.dto.WatcherDto;
import com.smalaca.taskamanager.exception.EpicDoesNotExistException;
import com.smalaca.taskamanager.exception.ProjectNotFoundException;
import com.smalaca.taskamanager.exception.UserNotFoundException;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskamanager.repository.EpicRepository;
import com.smalaca.taskamanager.repository.ProjectRepository;
import com.smalaca.taskamanager.repository.UserRepository;
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
@RequestMapping("/epic")
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:NestedIfDepth", "PMD.CollapsibleIfStatements"})
public class EpicController {
    private final EpicRepository epicRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public EpicController(EpicRepository epicRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.epicRepository = epicRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<EpicDto> findById(@PathVariable Long id) {
        Optional<Epic> found = epicRepository.findById(id);

        if (found.isPresent()) {
            Epic epic = found.get();
            EpicDto epicDto = new EpicDto();

            epicDto.setId(epic.getId());
            epicDto.setTitle(epic.getTitle());
            epicDto.setDescription(epic.getDescription());
            epicDto.setStatus(epic.getStatus().name());

            if (epic.getProject() != null) {
                Project project = epic.getProject();
                epicDto.setProjectId(project.getId());
            }

            Owner owner = epic.getOwner();

            if (owner != null) {
                epicDto.setOwnerFirstName(owner.getFirstName());
                epicDto.setOwnerLastName(owner.getLastName());

                PhoneNumber phoneNumber = owner.getPhoneNumber();

                if (phoneNumber != null) {
                    epicDto.setOwnerPhoneNumberPrefix(phoneNumber.getPrefix());
                    epicDto.setOwnerPhoneNumberNumber(phoneNumber.getNumber());
                }

                EmailAddress emailAddress = owner.getEmailAddress();

                if (emailAddress != null) {
                    epicDto.setOwnerEmailAddress(emailAddress.getEmailAddress());
                }
            }

            List<WatcherDto> watchers = epic.getWatchers().stream().map(watcher -> {
                WatcherDto watcherDto = new WatcherDto();
                watcherDto.setFirstName(watcher.getFirstName());
                watcherDto.setLastName(watcher.getLastName());
                if (watcher.getEmailAddress() != null) {
                    watcherDto.setEmailAddress(watcher.getEmailAddress().getEmailAddress());
                }
                if (watcher.getPhoneNumber() != null) {
                    watcherDto.setPhonePrefix(watcher.getPhoneNumber().getPrefix());
                    watcherDto.setPhoneNumber(watcher.getPhoneNumber().getNumber());
                }
                return watcherDto;
            }).collect(Collectors.toList());
            epicDto.setWatchers(watchers);

            return ResponseEntity.ok(epicDto);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody EpicDto dto) {
        Epic epic = new Epic();
        epic.setTitle(dto.getTitle());
        epic.setDescription(dto.getDescription());
        epic.setStatus(ToDoItemStatus.valueOf(dto.getStatus()));

        if (dto.getOwnerId() != null) {
            Optional<User> found = userRepository.findById(dto.getOwnerId());

            if (found.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            } else {
                User user = found.get();
                Owner owner = new Owner();
                owner.setFirstName(user.getUserName().getFirstName());
                owner.setLastName(user.getUserName().getLastName());

                if (user.getEmailAddress() != null) {
                    EmailAddress emailAddress = new EmailAddress();
                    emailAddress.setEmailAddress(user.getEmailAddress().getEmailAddress());
                    owner.setEmailAddress(emailAddress);
                }

                if (user.getPhoneNumber() != null) {
                    PhoneNumber phoneNumber = new PhoneNumber();
                    phoneNumber.setPrefix(user.getPhoneNumber().getPrefix());
                    phoneNumber.setNumber(user.getPhoneNumber().getNumber());
                    owner.setPhoneNumber(phoneNumber);
                }

                epic.setOwner(owner);
            }
        }

        Project project;
        try {
            if (!projectRepository.existsById(dto.getProjectId())) {
                throw new ProjectNotFoundException();
            }

            project = projectRepository.findById(dto.getProjectId()).get();
        } catch (ProjectNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }

        epic.setProject(project);

        Epic saved = epicRepository.save(epic);

        return ResponseEntity.ok(saved.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable long id, @RequestBody EpicDto dto) {
        Epic epic;

        try {
            epic = findById(id);
        } catch (EpicDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }

        if (dto.getDescription() != null) {
            epic.setDescription(dto.getDescription());
        }

        if (dto.getStatus() != null) {
            if (ToDoItemStatus.valueOf(dto.getStatus()) != epic.getStatus()) {
                epic.setStatus(ToDoItemStatus.valueOf(dto.getStatus()));
            }
        }

        if (epic.getOwner() != null) {
            Owner owner = new Owner();
            owner.setFirstName(epic.getOwner().getFirstName());
            owner.setLastName(epic.getOwner().getLastName());

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

            epic.setOwner(owner);

        } else {
            if (dto.getOwnerId() != null) {
                boolean userExists = userRepository.existsById(dto.getOwnerId());

                if (userExists) {
                    User user = userRepository.findById(dto.getOwnerId()).get();
                    Owner owner = new Owner();

                    if (user.getPhoneNumber() != null) {
                        PhoneNumber phoneNumber = new PhoneNumber();
                        phoneNumber.setPrefix(user.getPhoneNumber().getPrefix());
                        phoneNumber.setNumber(user.getPhoneNumber().getNumber());
                        owner.setPhoneNumber(phoneNumber);
                    }

                    owner.setFirstName(user.getUserName().getFirstName());
                    owner.setLastName(user.getUserName().getLastName());

                    if (user.getEmailAddress() != null) {
                        EmailAddress emailAddress = new EmailAddress();
                        emailAddress.setEmailAddress(user.getEmailAddress().getEmailAddress());
                        owner.setEmailAddress(emailAddress);
                    }

                    epic.setOwner(owner);
                } else {
                    return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
                }
            }
        }
        epicRepository.save(epic);

        return ResponseEntity.ok().build();
    }

    private Epic findById(long id) {
        if (epicRepository.existsById(id)) {
            return epicRepository.findById(id).get();
        }

        throw new EpicDoesNotExistException();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        try {
            Optional<Epic> found = epicRepository.findById(id);

            if (found.isEmpty()) {
                throw new EpicDoesNotExistException();
            }

            epicRepository.delete(found.get());

            return ResponseEntity.ok().build();
        } catch (EpicDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/watcher")
    public ResponseEntity<Void> addWatcher(@PathVariable long id, @RequestBody WatcherDto dto) {
        try {
            Epic epic = findEpicBy(id);

            try {
                User user = findUserBy(dto.getId());
                Watcher watcher = new Watcher();
                watcher.setFirstName(user.getUserName().getFirstName());
                watcher.setLastName(user.getUserName().getLastName());

                if (user.getEmailAddress() != null) {
                    EmailAddress emailAddress = new EmailAddress();
                    emailAddress.setEmailAddress(user.getEmailAddress().getEmailAddress());
                    watcher.setEmailAddress(emailAddress);
                }

                if (user.getPhoneNumber() != null) {
                    PhoneNumber phoneNumber = new PhoneNumber();
                    phoneNumber.setPrefix(user.getPhoneNumber().getPrefix());
                    phoneNumber.setNumber(user.getPhoneNumber().getNumber());
                    watcher.setPhoneNumber(phoneNumber);
                }
                epic.addWatcher(watcher);

                epicRepository.save(epic);

            } catch (UserNotFoundException exception) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            }

            return ResponseEntity.ok().build();
        } catch (EpicDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{epicId}/watcher/{watcherId}")
    @Transactional
    public ResponseEntity<Void> removeWatcher(@PathVariable Long epicId, @PathVariable Long watcherId) {
        try {
            Epic epic = findEpicBy(epicId);
            User user = findUserBy(watcherId);

            Watcher watcher = new Watcher();

            if (user.getPhoneNumber() != null) {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setPrefix(user.getPhoneNumber().getPrefix());
                phoneNumber.setNumber(user.getPhoneNumber().getNumber());
                watcher.setPhoneNumber(phoneNumber);
            }

            watcher.setFirstName(user.getUserName().getFirstName());
            watcher.setLastName(user.getUserName().getLastName());

            if (user.getEmailAddress() != null) {
                EmailAddress emailAddress = new EmailAddress();
                emailAddress.setEmailAddress(user.getEmailAddress().getEmailAddress());
                watcher.setEmailAddress(emailAddress);
            }

            epic.removeWatcher(watcher);

            epicRepository.save(epic);

            return ResponseEntity.ok().build();
        } catch (EpicDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        } catch (UserNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @PutMapping("/{id}/stakeholder")
    public ResponseEntity<Void> addStakeholder(@PathVariable long id, @RequestBody StakeholderDto dto) {
        try {
            Epic epic = findEpicBy(id);

            try {
                User user = findUserBy(dto.getId());
                Stakeholder stakeholder = new Stakeholder();
                stakeholder.setFirstName(user.getUserName().getFirstName());
                stakeholder.setLastName(user.getUserName().getLastName());

                if (user.getEmailAddress() != null) {
                    EmailAddress emailAddress = new EmailAddress();
                    emailAddress.setEmailAddress(user.getEmailAddress().getEmailAddress());
                    stakeholder.setEmailAddress(emailAddress);
                }

                if (user.getPhoneNumber() != null) {
                    PhoneNumber phoneNumber = new PhoneNumber();
                    phoneNumber.setPrefix(user.getPhoneNumber().getPrefix());
                    phoneNumber.setNumber(user.getPhoneNumber().getNumber());
                    stakeholder.setPhoneNumber(phoneNumber);
                }
                epic.addStakeholder(stakeholder);

                epicRepository.save(epic);

            } catch (UserNotFoundException exception) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            }

            return ResponseEntity.ok().build();
        } catch (EpicDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{epicId}/stakeholder/{stakeholderId}")
    @Transactional
    public ResponseEntity<Void> removeStakeholder(@PathVariable Long epicId, @PathVariable Long stakeholderId) {
        try {
            Epic epic = findEpicBy(epicId);
            User user = findUserBy(stakeholderId);

            Stakeholder stakeholder = new Stakeholder();

            if (user.getPhoneNumber() != null) {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setPrefix(user.getPhoneNumber().getPrefix());
                phoneNumber.setNumber(user.getPhoneNumber().getNumber());
                stakeholder.setPhoneNumber(phoneNumber);
            }

            stakeholder.setFirstName(user.getUserName().getFirstName());
            stakeholder.setLastName(user.getUserName().getLastName());

            if (user.getEmailAddress() != null) {
                EmailAddress emailAddress = new EmailAddress();
                emailAddress.setEmailAddress(user.getEmailAddress().getEmailAddress());
                stakeholder.setEmailAddress(emailAddress);
            }

            epic.removeStakeholder(stakeholder);

            epicRepository.save(epic);

            return ResponseEntity.ok().build();
        } catch (EpicDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        } catch (UserNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }
    }

    private Epic findEpicBy(long id) {
        Optional<Epic> found = epicRepository.findById(id);

        if (found.isEmpty()) {
            throw new EpicDoesNotExistException();
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
