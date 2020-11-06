package com.smalaca.taskamanager.service;

import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Story;

public interface ProjectBacklogService {
    void moveToReadyForDevelopment(Story story, Project project);

    void putOnTop(Epic epic);

    String linkFor(long toDoItemId);
}
