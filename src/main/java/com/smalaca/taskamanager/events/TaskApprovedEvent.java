package com.smalaca.taskamanager.events;

public class TaskApprovedEvent {
    private long taskId;

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getTaskId() {
        return taskId;
    }
}
