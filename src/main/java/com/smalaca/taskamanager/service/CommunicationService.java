package com.smalaca.taskamanager.service;

import com.smalaca.taskamanager.infrastructure.enums.CommunicatorType;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.ProductOwner;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Team;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.model.interfaces.ToDoItem;

public interface CommunicationService {
    void setType(CommunicatorType type);

    void notify(ToDoItem toDoItem, ProductOwner productOwner);

    void notify(ToDoItem toDoItem, Owner owner);

    void notify(ToDoItem toDoItem, Watcher watcher);

    void notify(ToDoItem toDoItem, User user);

    void notify(ToDoItem toDoItem, Stakeholder stakeholder);

    void notify(ToDoItem toDoItem, Team team);

    void notifyTeamsAbout(ToDoItem toDoItem, Project project);
}
