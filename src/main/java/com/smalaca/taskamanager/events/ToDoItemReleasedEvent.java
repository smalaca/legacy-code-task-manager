package com.smalaca.taskamanager.events;

public class ToDoItemReleasedEvent {
    private long toDoItemId;

    public long getToDoItemId() {
        return toDoItemId;
    }

    public void setToDoItemId(long toDoItemId) {
        this.toDoItemId = toDoItemId;
    }
}
