package com.smalaca.taskamanager.model.interfaces;

import com.smalaca.taskamanager.model.embedded.Assignee;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;

import java.util.List;

public interface ToDoItem {
    ToDoItemStatus getStatus();

    Project getProject();

    List<Watcher> getWatchers();

    Owner getOwner();

    boolean isAssigned();

    Assignee getAssignee();

    List<Stakeholder> getStakeholders();

    Long getId();
}
