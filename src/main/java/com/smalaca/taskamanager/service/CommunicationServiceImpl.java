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
import org.springframework.stereotype.Service;

@Service
public class CommunicationServiceImpl implements CommunicationService {
    @Override
    public void setType(CommunicatorType type) {

    }

    @Override
    public void notify(ToDoItem toDoItem, ProductOwner productOwner) {

    }

    @Override
    public void notify(ToDoItem toDoItem, Owner owner) {

    }

    @Override
    public void notify(ToDoItem toDoItem, Watcher watcher) {

    }

    @Override
    public void notify(ToDoItem toDoItem, User user) {

    }

    @Override
    public void notify(ToDoItem toDoItem, Stakeholder stakeholder) {

    }

    @Override
    public void notify(ToDoItem toDoItem, Team team) {

    }

    @Override
    public void notifyTeamsAbout(ToDoItem toDoItem, Project project) {

    }
}
