package com.smalaca.taskamanager.service;

import com.smalaca.taskamanager.model.entities.Sprint;
import com.smalaca.taskamanager.model.entities.Task;
import org.springframework.stereotype.Service;

@Service
public class SprintBacklogServiceImpl implements SprintBacklogService {
    @Override
    public void moveToReadyForDevelopment(Task task, Sprint sprint) {

    }
}
