package com.smalaca.taskamanager.service;

import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;

public interface StoryService {
    void updateProgressOf(Story story, Task task);

    void attachPartialApprovalFor(long storyId, long taskId);
}
