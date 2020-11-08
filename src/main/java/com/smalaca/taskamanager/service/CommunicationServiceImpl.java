package com.smalaca.taskamanager.service;

import com.smalaca.taskamanager.client.ChatClient;
import com.smalaca.taskamanager.client.MailClient;
import com.smalaca.taskamanager.client.SmsCommunicatorClient;
import com.smalaca.taskamanager.devnull.DevNullDirectory;
import com.smalaca.taskamanager.infrastructure.enums.CommunicatorType;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.ProductOwner;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Team;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.model.interfaces.ToDoItem;
import com.smalaca.taskamanager.model.other.ChatRoom;
import com.smalaca.taskamanager.model.other.Mail;
import com.smalaca.taskamanager.session.SessionHolder;
import org.springframework.stereotype.Service;

@Service
public class CommunicationServiceImpl implements CommunicationService {
    private CommunicatorType type;
    private final ProjectBacklogService projectBacklogService;
    private final DevNullDirectory devNullDirectory;
    private final ChatClient chat;
    private final SmsCommunicatorClient smsCommunicator;
    private final MailClient mailClient;

    public CommunicationServiceImpl(
            ProjectBacklogService projectBacklogService, DevNullDirectory devNullDirectory, ChatClient chat,
            SmsCommunicatorClient smsCommunicator, MailClient mailClient) {
        this.projectBacklogService = projectBacklogService;
        this.devNullDirectory = devNullDirectory;
        this.chat = chat;
        this.smsCommunicator = smsCommunicator;
        this.mailClient = mailClient;
    }


    public void setType(CommunicatorType type) {
        this.type = type;
    }

    public void notify(ToDoItem toDoItem, ProductOwner productOwner) {
        switch (type) {
            case MAIL:
                notifyAbout(toDoItem, productOwner.getEmailAddress());
                break;
            case SMS:
                notifyAbout(toDoItem, productOwner.getPhoneNumber());
                break;
            case DIRECT:
                notifyAbout(toDoItem, productOwner.getFirstName() + "." + productOwner.getLastName());
                break;
            case NULL_TYPE:
                notifyAbout();
                break;
        }
    }

    public void notify(ToDoItem toDoItem, Owner owner) {
        switch (type) {
            case SMS:
                notifyAbout(toDoItem, owner.getPhoneNumber());
                break;
            case MAIL:
                notifyAbout(toDoItem, owner.getEmailAddress());
                break;
            case DIRECT:
                notifyAbout(toDoItem, owner.getFirstName() + "." + owner.getLastName());
                break;
            case NULL_TYPE:
                notifyAbout();
                break;
        }
    }

    public void notify(ToDoItem toDoItem, Watcher watcher) {
        switch (type) {
            case SMS:
                notifyAbout(toDoItem, watcher.getPhoneNumber());
                break;
            case DIRECT:
                notifyAbout(toDoItem, watcher.getFirstName() + "." + watcher.getLastName());
                break;
            case MAIL:
                notifyAbout(toDoItem, watcher.getEmailAddress());
                break;
            case NULL_TYPE:
                notifyAbout();
                break;
        }
    }

    public void notify(ToDoItem toDoItem, User user) {
        switch (type) {
            case SMS:
                notifyAbout(toDoItem, user.getPhoneNumber());
                break;
            case DIRECT:
                notifyAbout(toDoItem, user.getLogin());
                break;
            case MAIL:
                notifyAbout(toDoItem, user.getEmailAddress());
                break;
            case NULL_TYPE:
                notifyAbout();
                break;
        }
    }

    public void notify(ToDoItem toDoItem, Stakeholder stakeholder) {
        switch (type) {
            case DIRECT:
                notifyAbout(toDoItem, stakeholder.getFirstName() + "." + stakeholder.getLastName());
                break;
            case SMS:
                notifyAbout(toDoItem, stakeholder.getPhoneNumber());
                break;
            case MAIL:
                notifyAbout(toDoItem, stakeholder.getEmailAddress());
                break;
            case NULL_TYPE:
                notifyAbout();
                break;
        }
    }

    public void notifyTeamsAbout(ToDoItem toDoItem, Project project) {
        for (Team team : project.getTeams()) {
            notify(toDoItem, team);
        }
    }

    public void notify(ToDoItem toDoItem, Team team) {
        for (User user : team.getMembers()) {
            notify(toDoItem, user);
        }
    }

    private void notifyAbout() {
        devNullDirectory.forget();
    }

    private void notifyAbout(ToDoItem toDoItem, String userName) {
        ChatRoom chatRoom = chat.connectWith(userName);
        chatRoom.send(projectBacklogService.linkFor(toDoItem.getId()));
    }

    private void notifyAbout(ToDoItem toDoItem, PhoneNumber phoneNumber) {
        smsCommunicator.textTo(phoneNumber, projectBacklogService.linkFor(toDoItem.getId()));
    }

    private void notifyAbout(ToDoItem toDoItem, EmailAddress emailAddress) {
        User loggedUser = SessionHolder.instance().logged();
        Mail mail = new Mail();
        mail.setFrom(loggedUser.getEmailAddress());
        mail.setTo(emailAddress);
        mail.setTopic("NOTIFICATION ABOUT: " + toDoItem.getId());
        mail.setContent(String.valueOf(toDoItem.getId()));

        mailClient.send(mail);
    }
}
