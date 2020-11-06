package com.smalaca.taskamanager.service;

import com.smalaca.taskamanager.model.entities.Sprint;
import com.smalaca.taskamanager.model.entities.Task;

public interface SprintBacklogService {
    void moveToReadyForDevelopment(Task task, Sprint sprint);
}
