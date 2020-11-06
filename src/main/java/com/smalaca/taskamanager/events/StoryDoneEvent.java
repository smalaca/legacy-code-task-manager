package com.smalaca.taskamanager.events;

public class StoryDoneEvent {
    private long storyId;

    public long getStoryId() {
        return storyId;
    }

    public void setStoryId(long storyId) {
        this.storyId = storyId;
    }
}
