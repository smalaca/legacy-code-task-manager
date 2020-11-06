package com.smalaca.taskamanager.events;

public class EpicReadyToPrioritize {
    private long epicId;

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
    }
}
