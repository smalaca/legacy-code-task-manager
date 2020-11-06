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
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.TO_BE_DEFINED;

@Entity
@SuppressWarnings("MethodCount")
public class Epic implements ToDoItem {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String description;

    private ToDoItemStatus status = TO_BE_DEFINED;

    @Embedded
    private Owner owner;

    @ManyToOne
    private Project project;

    @ElementCollection
    private List<Watcher> watchers = new ArrayList<>();

    @ElementCollection
    private List<Stakeholder> stakeholders = new ArrayList<>();

    @Embedded
    private Assignee assignee;

    @OneToMany
    private List<Story> stories = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public ToDoItemStatus getStatus() {
        return status;
    }

    public void setStatus(ToDoItemStatus status) {
        this.status = status;
    }

    @Override
    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    @Override
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public List<Watcher> getWatchers() {
        return watchers;
    }

    public void addWatcher(Watcher watcher) {
        watchers.add(watcher);
    }

    public void removeWatcher(Watcher watcher) {
        if (!watchers.contains(watcher)) {
            throw new RuntimeException();
        }
        watchers.remove(watcher);
    }

    @Override
    public List<Stakeholder> getStakeholders() {
        return stakeholders;
    }

    public void addStakeholder(Stakeholder stakeholder) {
        stakeholders.add(stakeholder);
    }

    public void removeStakeholder(Stakeholder stakeholder) {
        if (!stakeholders.contains(stakeholder)) {
            throw new RuntimeException();
        }
        stakeholders.remove(stakeholder);
    }

    @Override
    public Assignee getAssignee() {
        return assignee;
    }

    public void setAssignee(Assignee assignee) {
        this.assignee = assignee;
    }

    @Override
    public boolean isAssigned() {
        return assignee != null;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void addStory(Story story) {
        stories.add(story);
    }
}
