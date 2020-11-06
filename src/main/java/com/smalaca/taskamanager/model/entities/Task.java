package com.smalaca.taskamanager.model.entities;

import com.smalaca.taskamanager.model.embedded.Assignee;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskamanager.model.interfaces.ToDoItem;

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
@SuppressWarnings("MethodCount")
public class Task implements ToDoItem {
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

    @ManyToOne
    private Sprint currentSprint;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
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

    @Override
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

    @Override
    public List<Watcher> getWatchers() {
        return watchers;
    }

    public Story getStory() {
        return story;
    }

    public void removeStakeholder(Stakeholder stakeholder) {
        if (!stakeholders.contains(stakeholder)) {
            throw new RuntimeException();
        }
        stakeholders.remove(stakeholder);
    }

    public void setAssignee(Assignee assignee) {
        this.assignee = assignee;
    }

    @Override
    public List<Stakeholder> getStakeholders() {
        return stakeholders;
    }

    public void removeWatcher(Watcher watcher) {
        if (!watchers.contains(watcher)) {
            throw new RuntimeException();
        }
        watchers.remove(watcher);
    }

    @Override
    public Assignee getAssignee() {
        return assignee;
    }

    @Override
    public Project getProject() {
        return getStory().getEpic().getProject();
    }

    @Override
    public boolean isAssigned() {
        return assignee != null;
    }

    public void setCurrentSprint(Sprint currentSprint) {
        this.currentSprint = currentSprint;
    }

    public Sprint getCurrentSprint() {
        return currentSprint;
    }

    public boolean isSubtask() {
        return story != null;
    }
}
