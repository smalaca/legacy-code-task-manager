package com.smalaca.taskamanager.api.rest;


import com.smalaca.taskamanager.dto.AssigneeDto;
import com.smalaca.taskamanager.dto.StakeholderDto;
import com.smalaca.taskamanager.dto.TaskDto;
import com.smalaca.taskamanager.dto.WatcherDto;
import com.smalaca.taskamanager.exception.ProjectNotFoundException;
import com.smalaca.taskamanager.exception.TaskDoesNotExistException;
import com.smalaca.taskamanager.exception.TeamNotFoundException;
import com.smalaca.taskamanager.exception.UserNotFoundException;
import com.smalaca.taskamanager.model.embedded.Assignee;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.entities.Team;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskamanager.repository.StoryRepository;
import com.smalaca.taskamanager.repository.TaskRepository;
import com.smalaca.taskamanager.repository.TeamRepository;
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
@RequestMapping("/task")
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:NestedTryDepth", "checkstyle:NestedIfDepth", "PMD.CollapsibleIfStatements"})
public class TaskController {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final StoryRepository storyRepository;

    public TaskController(
            TaskRepository taskRepository, UserRepository userRepository, TeamRepository teamRepository, StoryRepository storyRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.storyRepository = storyRepository;
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> findById(@PathVariable Long id) {
        Optional<Task> found = taskRepository.findById(id);

        if (found.isPresent()) {
            Task task = found.get();
            TaskDto taskDto = new TaskDto();

            taskDto.setId(task.getId());
            taskDto.setTitle(task.getTitle());
            taskDto.setDescription(task.getDescription());
            taskDto.setStatus(task.getStatus().name());

            if (task.getStory() != null) {
                Story project = task.getStory();
                taskDto.setStoryId(project.getId());
            }

            Owner owner = task.getOwner();

            if (owner != null) {
                taskDto.setOwnerFirstName(owner.getFirstName());
                taskDto.setOwnerLastName(owner.getLastName());

                PhoneNumber phoneNumber = owner.getPhoneNumber();

                if (phoneNumber != null) {
                    taskDto.setOwnerPhoneNumberPrefix(phoneNumber.getPrefix());
                    taskDto.setOwnerPhoneNumberNumber(phoneNumber.getNumber());
                }

                EmailAddress emailAddress = owner.getEmailAddress();

                if (emailAddress != null) {
                    taskDto.setOwnerEmailAddress(emailAddress.getEmailAddress());
                }
            }

            List<WatcherDto> watchers = task.getWatchers().stream().map(watcher -> {
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
            taskDto.setWatchers(watchers);
            
            if (task.getAssignee() != null) {
                AssigneeDto assigneeDto = new AssigneeDto();
                assigneeDto.setFirstName(task.getAssignee().getFirstName());
                assigneeDto.setLastName(task.getAssignee().getLastName());
                assigneeDto.setTeamId(task.getAssignee().getTeamId());
                taskDto.setAssignee(assigneeDto);
            }

            List<StakeholderDto> stakeholders = task.getStakeholders().stream().map(stakeholder -> {
                StakeholderDto stakeholderDto = new StakeholderDto();
                stakeholderDto.setFirstName(stakeholder.getFirstName());
                stakeholderDto.setLastName(stakeholder.getLastName());
                if (stakeholder.getEmailAddress() != null) {
                    stakeholderDto.setEmailAddress(stakeholder.getEmailAddress().getEmailAddress());
                }
                if (stakeholder.getPhoneNumber() != null) {
                    stakeholderDto.setPhonePrefix(stakeholder.getPhoneNumber().getPrefix());
                    stakeholderDto.setPhoneNumber(stakeholder.getPhoneNumber().getNumber());
                }
                return stakeholderDto;
            }).collect(Collectors.toList());
            taskDto.setStakeholders(stakeholders);

            return ResponseEntity.ok(taskDto);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody TaskDto dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(ToDoItemStatus.valueOf(dto.getStatus()));

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

                task.setOwner(owner);
            }
        }

        Story story;
        try {
            if (!storyRepository.existsById(dto.getStoryId())) {
                throw new ProjectNotFoundException();
            }

            story = storyRepository.findById(dto.getStoryId()).get();
        } catch (ProjectNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }

        task.setStory(story);

        Task saved = taskRepository.save(task);

        return ResponseEntity.ok(saved.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable long id, @RequestBody TaskDto dto) {
        Task task;

        try {
            task = findById(id);
        } catch (TaskDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }

        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }

        if (dto.getStatus() != null) {
            if (ToDoItemStatus.valueOf(dto.getStatus()) != task.getStatus()) {
                task.setStatus(ToDoItemStatus.valueOf(dto.getStatus()));
            }
        }

        if (task.getOwner() != null) {
            Owner owner = new Owner();
            owner.setFirstName(task.getOwner().getFirstName());
            owner.setLastName(task.getOwner().getLastName());

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

            task.setOwner(owner);

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

                    task.setOwner(owner);
                } else {
                    return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
                }
            }
        }
        taskRepository.save(task);

        return ResponseEntity.ok().build();
    }

    private Task findById(long id) {
        if (taskRepository.existsById(id)) {
            return taskRepository.findById(id).get();
        }

        throw new TaskDoesNotExistException();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        try {
            Optional<Task> found = taskRepository.findById(id);

            if (found.isEmpty()) {
                throw new TaskDoesNotExistException();
            }

            taskRepository.delete(found.get());

            return ResponseEntity.ok().build();
        } catch (TaskDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/watcher")
    public ResponseEntity<Void> addWatcher(@PathVariable long id, @RequestBody WatcherDto dto) {
        try {
            Task task = findTaskBy(id);

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
                task.addWatcher(watcher);

                taskRepository.save(task);

            } catch (UserNotFoundException exception) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            }

            return ResponseEntity.ok().build();
        } catch (TaskDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{taskId}/watcher/{watcherId}")
    @Transactional
    public ResponseEntity<Void> removeWatcher(@PathVariable Long taskId, @PathVariable Long watcherId) {
        try {
            Task task = findTaskBy(taskId);
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

            task.removeWatcher(watcher);

            taskRepository.save(task);

            return ResponseEntity.ok().build();
        } catch (TaskDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        } catch (UserNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @PutMapping("/{id}/stakeholder")
    public ResponseEntity<Void> addStakeholder(@PathVariable long id, @RequestBody StakeholderDto dto) {
        try {
            Task task = findTaskBy(id);

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
                task.addStakeholder(stakeholder);

                taskRepository.save(task);

            } catch (UserNotFoundException exception) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            }

            return ResponseEntity.ok().build();
        } catch (TaskDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{taskId}/stakeholder/{stakeholderId}")
    @Transactional
    public ResponseEntity<Void> removeStakeholder(@PathVariable Long taskId, @PathVariable Long stakeholderId) {
        try {
            Task task = findTaskBy(taskId);
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

            task.removeStakeholder(stakeholder);

            taskRepository.save(task);

            return ResponseEntity.ok().build();
        } catch (TaskDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        } catch (UserNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @PutMapping("/{id}/assignee")
    public ResponseEntity<Void> addAssignee(@PathVariable long id, @RequestBody AssigneeDto dto) {
        try {
            Task task = findTaskBy(id);

            try {
                User user = findUserBy(dto.getId());
                Assignee assignee = new Assignee();
                assignee.setFirstName(user.getUserName().getFirstName());
                assignee.setLastName(user.getUserName().getLastName());

                try {
                    findTeamBy(dto.getTeamId());
                    assignee.setTeamId(dto.getTeamId());
                    task.setAssignee(assignee);
                    taskRepository.save(task);
                } catch (TeamNotFoundException exception) {
                    return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
                }

            } catch (UserNotFoundException exception) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            }

            return ResponseEntity.ok().build();
        } catch (TaskDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    private void findTeamBy(Long id) {
        Optional<Team> found = teamRepository.findById(id);

        if (found.isEmpty()) {
            throw new TeamNotFoundException();
        }
    }

    @DeleteMapping("/{taskId}/assignee")
    @Transactional
    public ResponseEntity<Void> removeAssignee(@PathVariable Long taskId) {
        try {
            Task task = findTaskBy(taskId);
            task.setAssignee(null);

            taskRepository.save(task);

            return ResponseEntity.ok().build();
        } catch (TaskDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    private Task findTaskBy(long id) {
        Optional<Task> found = taskRepository.findById(id);

        if (found.isEmpty()) {
            throw new TaskDoesNotExistException();
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
