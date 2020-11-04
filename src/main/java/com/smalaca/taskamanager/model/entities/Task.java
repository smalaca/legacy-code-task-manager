package com.smalaca.taskamanager.model.entities;

import com.smalaca.taskamanager.model.embedded.Assignee;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.TO_BE_DEFINED;

@Entity
public class Task {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String description;

    private ToDoItemStatus status = TO_BE_DEFINED;

    @Embedded
    private Owner owner;

    @ManyToOne
    private Story story;

    @ElementCollection
    private List<Watcher> watchers = new ArrayList<>();

    @ElementCollection
    private List<Stakeholder> stakeholders = new ArrayList<>();

    @Embedded
    private Assignee assignee;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public ToDoItemStatus getStatus() {
        return status;
    }

    public void setStatus(ToDoItemStatus status) {
        this.status = status;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public void addStakeholder(Stakeholder stakeholder) {
        stakeholders.add(stakeholder);
    }

    public void addWatcher(Watcher watcher) {
        watchers.add(watcher);
    }

    public List<Watcher> getWatchers() {
        return watchers;
    }

    public Story getStory() {
        return story;
    }

    public void setAssignee(Assignee assignee) {
        this.assignee = assignee;
    }

    public void removeStakeholder(Stakeholder stakeholder) {
        if (!stakeholders.contains(stakeholder)) {
            throw new RuntimeException();
        }
        stakeholders.remove(stakeholder);
    }

    public List<Stakeholder> getStakeholders() {
        return stakeholders;
    }

    public void removeWatcher(Watcher watcher) {
        if (!watchers.contains(watcher)) {
            throw new RuntimeException();
        }
        watchers.remove(watcher);
    }

    public Assignee getAssignee() {
        return assignee;
    }
}
