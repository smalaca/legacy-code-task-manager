package com.smalaca.taskamanager.api.rest;


import com.smalaca.taskamanager.dto.EpicDto;
import com.smalaca.taskamanager.exception.EpicDoesNotExistException;
import com.smalaca.taskamanager.exception.ProjectNotFoundException;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskamanager.repository.EpicRepository;
import com.smalaca.taskamanager.repository.ProjectRepository;
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

import java.util.Optional;

@RestController
@RequestMapping("/epic")
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:NestedIfDepth", "PMD.CollapsibleIfStatements"})
public class EpicController {
    private final EpicRepository epicRepository;
    private final ProjectRepository projectRepository;

    public EpicController(EpicRepository epicRepository, ProjectRepository projectRepository) {
        this.epicRepository = epicRepository;
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

        if (dto.getOwnerFirstName() != null) {
            if (dto.getOwnerLastName() != null) {
                Owner owner = new Owner();
                owner.setFirstName(dto.getOwnerFirstName());
                owner.setLastName(dto.getOwnerLastName());

                if (dto.getOwnerEmailAddress() != null) {
                    EmailAddress emailAddress = new EmailAddress();
                    emailAddress.setEmailAddress(dto.getOwnerEmailAddress());
                    owner.setEmailAddress(emailAddress);
                }

                if (dto.getOwnerPhoneNumberPrefix() != null) {
                    if (dto.getOwnerPhoneNumberNumber() != null) {
                        PhoneNumber phoneNumber = new PhoneNumber();
                        phoneNumber.setPrefix(dto.getOwnerPhoneNumberPrefix());
                        phoneNumber.setNumber(dto.getOwnerPhoneNumberNumber());
                        owner.setPhoneNumber(phoneNumber);
                    }
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

            if (dto.getOwnerEmailAddress() != null) {
                EmailAddress emailAddress = new EmailAddress();
                emailAddress.setEmailAddress(dto.getOwnerEmailAddress());
                owner.setEmailAddress(emailAddress);
            }

            if (dto.getOwnerPhoneNumberPrefix() != null && dto.getOwnerPhoneNumberNumber() != null) {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setPrefix(dto.getOwnerPhoneNumberPrefix());
                phoneNumber.setNumber(dto.getOwnerPhoneNumberNumber());
                owner.setPhoneNumber(phoneNumber);
            }

            epic.setOwner(owner);

        } else {
            if (dto.getOwnerFirstName() != null) {
                if (dto.getOwnerLastName() != null) {
                    Owner owner = new Owner();
                    owner.setFirstName(dto.getOwnerFirstName());
                    owner.setLastName(dto.getOwnerLastName());

                    if (dto.getOwnerEmailAddress() != null) {
                        EmailAddress emailAddress = new EmailAddress();
                        emailAddress.setEmailAddress(dto.getOwnerEmailAddress());
                        owner.setEmailAddress(emailAddress);
                    }

                    if (dto.getOwnerPhoneNumberPrefix() != null) {
                        if (dto.getOwnerPhoneNumberNumber() != null) {
                            PhoneNumber phoneNumber = new PhoneNumber();
                            phoneNumber.setPrefix(dto.getOwnerPhoneNumberPrefix());
                            phoneNumber.setNumber(dto.getOwnerPhoneNumberNumber());
                            owner.setPhoneNumber(phoneNumber);
                        }
                    }

                    epic.setOwner(owner);
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
}
