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
public class Story {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String description;

    private ToDoItemStatus status = TO_BE_DEFINED;

    @Embedded
    private Owner owner;

    @ManyToOne
    private Epic epic;

    @ElementCollection
    private List<Watcher> watchers = new ArrayList<>();

    @ElementCollection
    private List<Stakeholder> stakeholders = new ArrayList<>();

    @Embedded
    private Assignee assignee;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(ToDoItemStatus status) {
        this.status = status;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public void removeWatcher(Watcher watcher) {
        if (!watchers.contains(watcher)) {
            throw new RuntimeException();
        }
        watchers.remove(watcher);
    }

    public void removeStakeholder(Stakeholder stakeholder) {
        if (!stakeholders.contains(stakeholder)) {
            throw new RuntimeException();
        }
        stakeholders.remove(stakeholder);
    }

    public Assignee getAssignee() {
        return assignee;
    }

    public void setAssignee(Assignee assignee) {
        this.assignee = assignee;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ToDoItemStatus getStatus() {
        return status;
    }

    public Owner getOwner() {
        return owner;
    }

    public Epic getEpic() {
        return epic;
    }

    public List<Watcher> getWatchers() {
        return watchers;
    }

    public void addWatcher(Watcher watcher) {
        watchers.add(watcher);
    }

    public List<Stakeholder> getStakeholders() {
        return stakeholders;
    }

    public void addStakeholder(Stakeholder stakeholder) {
        stakeholders.add(stakeholder);
    }
}
