package com.smalaca.taskamanager.api.rest;

import com.smalaca.taskamanager.dto.SprintDto;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Sprint;
import com.smalaca.taskamanager.repository.ProjectRepository;
import com.smalaca.taskamanager.repository.SprintRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/sprint")
public class SprintController {
    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;

    public SprintController(SprintRepository sprintRepository, ProjectRepository projectRepository) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<SprintDto> findById(@PathVariable Long id) {
        Optional<Sprint> found = sprintRepository.findById(id);

        if (found.isPresent()) {
            Sprint sprint = found.get();
            SprintDto dto = new SprintDto();
            dto.setId(sprint.getId());
            dto.setName(sprint.getName());
            dto.setProjectId(sprint.getProject().getId());
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody SprintDto dto) {
        if (sprintRepository.findByNameAndProjectId(dto.getName(), dto.getProjectId()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            Sprint sprint = new Sprint();
            sprint.setName(dto.getName());

            Optional<Project> found = projectRepository.findById(dto.getProjectId());

            if (found.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            }

            Project project = found.get();
            sprint.setProject(project);
            project.addSprint(sprint);
            projectRepository.save(project);
            Long id = sprintRepository.save(sprint).getId();
            return new ResponseEntity<>(id, HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<Sprint> found = sprintRepository.findById(id);

        if (found.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            sprintRepository.delete(found.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
