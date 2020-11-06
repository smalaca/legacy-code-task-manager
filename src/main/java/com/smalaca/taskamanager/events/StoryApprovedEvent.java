package com.smalaca.taskamanager.events;

public class StoryApprovedEvent {
    private long storyId;

    public void setStoryId(long storyId) {
        this.storyId = storyId;
    }

    public long getStoryId() {
        return storyId;
    }
}
