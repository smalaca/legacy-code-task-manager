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
@RequestMapping("/task")
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:NestedTryDepth", "checkstyle:NestedIfDepth", "PMD.CollapsibleIfStatements"})
public class TaskController {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final StoryRepository storyRepository;
    private final ToDoItemService toDoItemService;

    public TaskController(
            TaskRepository taskRepository, UserRepository userRepository, TeamRepository teamRepository,
            StoryRepository storyRepository, ToDoItemService toDoItemService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.storyRepository = storyRepository;
        this.toDoItemService = toDoItemService;
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> findById(@PathVariable Long id) {
        Optional<Task> found = taskRepository.findById(id);

        if (found.isPresent()) {
            Task task = found.get();
            TaskDto dto = new TaskDto();

            dto.setId(task.getId());
            dto.setDescription(task.getDescription());
            dto.setTitle(task.getTitle());
            dto.setStatus(task.getStatus().name());

            if (task.getStory() != null) {
                Story project = task.getStory();
                dto.setStoryId(project.getId());
            }

            Owner owner = task.getOwner();

            if (owner != null) {
                dto.setOwnerLastName(owner.getLastName());
                dto.setOwnerFirstName(owner.getFirstName());

                PhoneNumber phoneNumber = owner.getPhoneNumber();

                if (phoneNumber != null) {
                    dto.setOwnerPhoneNumberNumber(phoneNumber.getNumber());
                    dto.setOwnerPhoneNumberPrefix(phoneNumber.getPrefix());
                }

                EmailAddress emailAddress = owner.getEmailAddress();

                if (emailAddress != null) {
                    dto.setOwnerEmailAddress(emailAddress.getEmailAddress());
                }
            }

            List<WatcherDto> watchers = task.getWatchers().stream().map(watcher -> {
                WatcherDto wDto = new WatcherDto();
                wDto.setLastName(watcher.getLastName());
                wDto.setFirstName(watcher.getFirstName());
                if (watcher.getEmailAddress() != null) {
                    wDto.setEmailAddress(watcher.getEmailAddress().getEmailAddress());
                }
                if (watcher.getPhoneNumber() != null) {
                    wDto.setPhoneNumber(watcher.getPhoneNumber().getNumber());
                    wDto.setPhonePrefix(watcher.getPhoneNumber().getPrefix());
                }
                return wDto;
            }).collect(Collectors.toList());
            dto.setWatchers(watchers);
            
            if (task.getAssignee() != null) {
                AssigneeDto aDto = new AssigneeDto();
                aDto.setTeamId(task.getAssignee().getTeamId());
                aDto.setLastName(task.getAssignee().getLastName());
                aDto.setFirstName(task.getAssignee().getFirstName());
                dto.setAssignee(aDto);
            }

            List<StakeholderDto> stakeholders = task.getStakeholders().stream().map(stakeholder -> {
                StakeholderDto sDto = new StakeholderDto();
                sDto.setLastName(stakeholder.getLastName());
                sDto.setFirstName(stakeholder.getFirstName());
                if (stakeholder.getEmailAddress() != null) {
                    sDto.setEmailAddress(stakeholder.getEmailAddress().getEmailAddress());
                }
                if (stakeholder.getPhoneNumber() != null) {
                    sDto.setPhoneNumber(stakeholder.getPhoneNumber().getNumber());
                    sDto.setPhonePrefix(stakeholder.getPhoneNumber().getPrefix());
                }
                return sDto;
            }).collect(Collectors.toList());
            dto.setStakeholders(stakeholders);

            return ResponseEntity.ok(dto);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody TaskDto dto) {
        Task t = new Task();
        t.setTitle(dto.getTitle());
        t.setDescription(dto.getDescription());
        t.setStatus(ToDoItemStatus.valueOf(dto.getStatus()));

        if (dto.getOwnerId() != null) {
            Optional<User> found = userRepository.findById(dto.getOwnerId());

            if (found.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            } else {
                User u = found.get();
                Owner o = new Owner();
                o.setLastName(u.getUserName().getLastName());
                o.setFirstName(u.getUserName().getFirstName());

                if (u.getEmailAddress() != null) {
                    EmailAddress ea = new EmailAddress();
                    ea.setEmailAddress(u.getEmailAddress().getEmailAddress());
                    o.setEmailAddress(ea);
                }

                if (u.getPhoneNumber() != null) {
                    PhoneNumber pn = new PhoneNumber();
                    pn.setNumber(u.getPhoneNumber().getNumber());
                    pn.setPrefix(u.getPhoneNumber().getPrefix());
                    o.setPhoneNumber(pn);
                }

                t.setOwner(o);
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

        t.setStory(story);

        Task saved = taskRepository.save(t);

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

        boolean service = false;
        if (dto.getStatus() != null) {
            if (ToDoItemStatus.valueOf(dto.getStatus()) != task.getStatus()) {
                service = true;
                task.setStatus(ToDoItemStatus.valueOf(dto.getStatus()));
            }
        }

        if (task.getOwner() != null) {
            Owner o = new Owner();
            o.setFirstName(task.getOwner().getFirstName());
            o.setLastName(task.getOwner().getLastName());

            if (dto.getOwnerPhoneNumberPrefix() != null && dto.getOwnerPhoneNumberNumber() != null) {
                PhoneNumber pno = new PhoneNumber();
                pno.setNumber(dto.getOwnerPhoneNumberNumber());
                pno.setPrefix(dto.getOwnerPhoneNumberPrefix());
                o.setPhoneNumber(pno);
            }

            if (dto.getOwnerEmailAddress() != null) {
                EmailAddress email = new EmailAddress();
                email.setEmailAddress(dto.getOwnerEmailAddress());
                o.setEmailAddress(email);
            }

            task.setOwner(o);

        } else {
            if (dto.getOwnerId() != null) {
                boolean userExists = userRepository.existsById(dto.getOwnerId());

                if (userExists) {
                    User user = userRepository.findById(dto.getOwnerId()).get();
                    Owner ownr = new Owner();

                    if (user.getPhoneNumber() != null) {
                        PhoneNumber number = new PhoneNumber();
                        number.setNumber(user.getPhoneNumber().getNumber());
                        number.setPrefix(user.getPhoneNumber().getPrefix());
                        ownr.setPhoneNumber(number);
                    }

                    ownr.setLastName(user.getUserName().getLastName());
                    ownr.setFirstName(user.getUserName().getFirstName());

                    if (user.getEmailAddress() != null) {
                        EmailAddress eAdd = new EmailAddress();
                        eAdd.setEmailAddress(user.getEmailAddress().getEmailAddress());
                        ownr.setEmailAddress(eAdd);
                    }

                    task.setOwner(ownr);
                } else {
                    return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
                }
            }
        }
        taskRepository.save(task);
        if (service) {
            toDoItemService.processTask(task.getId());
        }

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
            Task entity1 = findTaskBy(id);

            try {
                User entity2 = findUserBy(dto.getId());
                Watcher entity3 = new Watcher();
                entity3.setFirstName(entity2.getUserName().getFirstName());
                entity3.setLastName(entity2.getUserName().getLastName());

                if (entity2.getEmailAddress() != null) {
                    EmailAddress entity4 = new EmailAddress();
                    entity4.setEmailAddress(entity2.getEmailAddress().getEmailAddress());
                    entity3.setEmailAddress(entity4);
                }

                if (entity2.getPhoneNumber() != null) {
                    PhoneNumber entity5 = new PhoneNumber();
                    entity5.setNumber(entity2.getPhoneNumber().getNumber());
                    entity5.setPrefix(entity2.getPhoneNumber().getPrefix());
                    entity3.setPhoneNumber(entity5);
                }
                entity1.addWatcher(entity3);

                taskRepository.save(entity1);

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
                PhoneNumber no = new PhoneNumber();
                no.setNumber(user.getPhoneNumber().getNumber());
                no.setPrefix(user.getPhoneNumber().getPrefix());
                watcher.setPhoneNumber(no);
            }

            watcher.setLastName(user.getUserName().getLastName());
            watcher.setFirstName(user.getUserName().getFirstName());

            if (user.getEmailAddress() != null) {
                EmailAddress add = new EmailAddress();
                add.setEmailAddress(user.getEmailAddress().getEmailAddress());
                watcher.setEmailAddress(add);
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
                stakeholder.setLastName(user.getUserName().getLastName());
                stakeholder.setFirstName(user.getUserName().getFirstName());

                if (user.getEmailAddress() != null) {
                    EmailAddress address = new EmailAddress();
                    address.setEmailAddress(user.getEmailAddress().getEmailAddress());
                    stakeholder.setEmailAddress(address);
                }

                if (user.getPhoneNumber() != null) {
                    PhoneNumber phNo = new PhoneNumber();
                    phNo.setNumber(user.getPhoneNumber().getNumber());
                    phNo.setPrefix(user.getPhoneNumber().getPrefix());
                    stakeholder.setPhoneNumber(phNo);
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

            Stakeholder stkh = new Stakeholder();

            if (user.getPhoneNumber() != null) {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setNumber(user.getPhoneNumber().getNumber());
                phoneNumber.setPrefix(user.getPhoneNumber().getPrefix());
                stkh.setPhoneNumber(phoneNumber);
            }

            stkh.setLastName(user.getUserName().getLastName());
            stkh.setFirstName(user.getUserName().getFirstName());

            if (user.getEmailAddress() != null) {
                EmailAddress emailAddress = new EmailAddress();
                emailAddress.setEmailAddress(user.getEmailAddress().getEmailAddress());
                stkh.setEmailAddress(emailAddress);
            }

            task.removeStakeholder(stkh);

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
                Assignee asgn = new Assignee();
                asgn.setLastName(user.getUserName().getLastName());
                asgn.setFirstName(user.getUserName().getFirstName());

                try {
                    findTeamBy(dto.getTeamId());
                    asgn.setTeamId(dto.getTeamId());
                    task.setAssignee(asgn);
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
