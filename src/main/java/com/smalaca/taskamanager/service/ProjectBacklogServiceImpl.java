package com.smalaca.taskamanager.service;

import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Story;
import org.springframework.stereotype.Service;

@Service
public class ProjectBacklogServiceImpl implements ProjectBacklogService {
    @Override
    public void moveToReadyForDevelopment(Story story, Project project) {

    }

    @Override
    public void putOnTop(Epic epic) {

    }

    @Override
    public String linkFor(long toDoItemId) {
        return "link to: " + toDoItemId;
    }
}
