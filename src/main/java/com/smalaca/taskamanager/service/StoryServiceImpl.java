package com.smalaca.taskamanager.service;

import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import org.springframework.stereotype.Service;

@Service
public class StoryServiceImpl implements StoryService {
    @Override
    public void updateProgressOf(Story story, Task task) {

    }

    @Override
    public void attachPartialApprovalFor(long storyId, long taskId) {

    }
}
