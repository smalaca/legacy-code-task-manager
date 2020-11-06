package com.smalaca.taskamanager.service;

import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.processor.ToDoItemProcessor;
import com.smalaca.taskamanager.repository.EpicRepository;
import com.smalaca.taskamanager.repository.StoryRepository;
import com.smalaca.taskamanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@SuppressWarnings({"IllegalCatch", "MagicNumber"})
public class ToDoItemService {
    private final ToDoItemProcessor processor;
    private final EpicRepository epicRepository;
    private final StoryRepository storyRepository;
    private final TaskRepository taskRepository;

    public ToDoItemService(
            ToDoItemProcessor processor, EpicRepository epicRepository, StoryRepository storyRepository, TaskRepository taskRepository) {
        this.processor = processor;
        this.epicRepository = epicRepository;
        this.storyRepository = storyRepository;
        this.taskRepository = taskRepository;
    }

    public int processTask(Long taskId) {
        Optional<Task> found = taskRepository.findById(taskId);

        if (found.isEmpty()) {
            return -1;
        }

        Task task = found.get();

        try {
            processor.processFor(task);
            return 1;
        } catch (Exception exception) {
            return -2;
        }
    }

    public int processStory(Long storyId) {
        Optional<Story> found = storyRepository.findById(storyId);

        if (found.isEmpty()) {
            return -1;
        }

        Story story = found.get();

        try {
            processor.processFor(story);
            return 1;
        } catch (Exception exception) {
            return -2;
        }
    }

    public int processEpic(Long epicId) {
        Optional<Epic> found = epicRepository.findById(epicId);

        if (found.isPresent()) {
            Epic epic = found.get();

            try {
                processor.processFor(epic);
                return 1;
            } catch (Exception exception) {
                return -2;
            }
        } else {
            return -1;
        }
    }
}
