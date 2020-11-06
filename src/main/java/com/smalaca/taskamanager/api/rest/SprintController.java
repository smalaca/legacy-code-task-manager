package com.smalaca.taskamanager.api.rest;

import com.smalaca.taskamanager.dto.SprintDto;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Sprint;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.repository.ProjectRepository;
import com.smalaca.taskamanager.repository.SprintRepository;
import com.smalaca.taskamanager.repository.StoryRepository;
import com.smalaca.taskamanager.repository.TaskRepository;
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
@RequestMapping("/sprint")
public class SprintController {
    private final SprintRepository sprintRepository;
    private final TaskRepository taskRepository;
    private final StoryRepository storyRepository;
    private final ProjectRepository projectRepository;

    public SprintController(
            SprintRepository sprintRepository, TaskRepository taskRepository, StoryRepository storyRepository, ProjectRepository projectRepository) {
        this.sprintRepository = sprintRepository;
        this.taskRepository = taskRepository;
        this.storyRepository = storyRepository;
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

    @PutMapping("{sprintId}/tasks/{taskId}")
    public ResponseEntity<Void> addTask(@PathVariable long sprintId, @PathVariable long taskId) {
        Optional<Sprint> foundSprint = sprintRepository.findById(sprintId);

        if (foundSprint.isPresent()) {
            Optional<Task> foundTask = taskRepository.findById(taskId);

            if (foundTask.isPresent()) {
                Sprint sprint = foundSprint.get();
                Task task = foundTask.get();
                sprint.addTask(task);
                task.setCurrentSprint(sprint);

                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("{sprintId}/tasks/{storyId}")
    public ResponseEntity<Void> addStory(@PathVariable long sprintId, @PathVariable long storyId) {
        Optional<Sprint> foundSprint = sprintRepository.findById(sprintId);

        if (foundSprint.isPresent()) {
            Optional<Story> foundStory = storyRepository.findById(storyId);

            if (foundStory.isPresent()) {
                Sprint sprint = foundSprint.get();
                Story story = foundStory.get();
                sprint.addStory(story);
                story.addSprint(sprint);
                story.setCurrentSprint(sprint);

                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
