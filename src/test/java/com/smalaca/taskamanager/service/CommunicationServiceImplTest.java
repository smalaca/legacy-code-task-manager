package com.smalaca.taskamanager.service;

import com.smalaca.taskamanager.client.ChatClient;
import com.smalaca.taskamanager.client.MailClient;
import com.smalaca.taskamanager.client.SmsCommunicatorClient;
import com.smalaca.taskamanager.devnull.DevNullDirectory;
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
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static com.smalaca.taskamanager.infrastructure.enums.CommunicatorType.DIRECT;
import static com.smalaca.taskamanager.infrastructure.enums.CommunicatorType.MAIL;
import static com.smalaca.taskamanager.infrastructure.enums.CommunicatorType.NULL_TYPE;
import static com.smalaca.taskamanager.infrastructure.enums.CommunicatorType.SMS;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class CommunicationServiceImplTest {
    private final ProjectBacklogService projectBacklogService = mock(ProjectBacklogService.class);
    private final DevNullDirectory devNullDirectory = mock(DevNullDirectory.class);
    private final ChatClient chat = mock(ChatClient.class);
    private final SmsCommunicatorClient smsCommunicator = mock(SmsCommunicatorClient.class);
    private final MailClient mailClient = mock(MailClient.class);
    private final CommunicationServiceImpl service = new CommunicationServiceImpl(projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);

    @Test
    void shouldNotifyProductOwnerViaMail() {
        ToDoItem toDoItem = mock(ToDoItem.class);
        service.setType(MAIL);
        ProductOwner productOwner = mock(ProductOwner.class);
        EmailAddress emailAddress = mock(EmailAddress.class);
        given(productOwner.getEmailAddress()).willReturn(emailAddress);
        User user = mock(User.class);
        EmailAddress userEmailAddress = mock(EmailAddress.class);
        given(user.getEmailAddress()).willReturn(userEmailAddress);
        SessionHolder.instance().logIn(user);
        given(toDoItem.getId()).willReturn(13L);

        service.notify(toDoItem, productOwner);

        then(toDoItem).should(times(2)).getId();
        then(productOwner).should().getEmailAddress();
        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);
        then(mailClient).should().send(captor.capture());
        Mail mail = captor.getValue();
        assertThat(mail.getFrom()).isEqualTo(userEmailAddress);
        assertThat(mail.getTo()).isEqualTo(emailAddress);
        assertThat(mail.getTopic()).isEqualTo("NOTIFICATION ABOUT: 13");
        assertThat(mail.getContent()).isEqualTo("13");
        verifyNoMoreInteractions(toDoItem, productOwner, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyProductOwnerViaSms() {
        long toDoItemId = 123;
        ToDoItem toDoItem = mock(ToDoItem.class);
        given(toDoItem.getId()).willReturn(toDoItemId);
        String link = "www.letstalkaboutjava.com";
        given(projectBacklogService.linkFor(toDoItemId)).willReturn(link);
        service.setType(SMS);
        PhoneNumber phoneNumber = mock(PhoneNumber.class);
        ProductOwner productOwner = mock(ProductOwner.class);
        given(productOwner.getPhoneNumber()).willReturn(phoneNumber);

        service.notify(toDoItem, productOwner);

        then(productOwner).should().getPhoneNumber();
        then(toDoItem).should().getId();
        then(projectBacklogService).should().linkFor(toDoItemId);
        then(smsCommunicator).should().textTo(phoneNumber, link);
        verifyNoMoreInteractions(toDoItem, productOwner, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyProductOwnerViaChat() {
        long toDoItemId = 42;
        ToDoItem toDoItem = mock(ToDoItem.class);
        given(toDoItem.getId()).willReturn(toDoItemId);
        service.setType(DIRECT);
        ProductOwner productOwner = mock(ProductOwner.class);
        String firstName = "Steve";
        String lastName = "Rogers";
        given(productOwner.getFirstName()).willReturn(firstName);
        given(productOwner.getLastName()).willReturn(lastName);
        ChatRoom chatRoom = mock(ChatRoom.class);
        given(chat.connectWith("Steve.Rogers")).willReturn(chatRoom);
        String link = "www.refactoring.com";
        given(projectBacklogService.linkFor(anyLong())).willReturn(link);

        service.notify(toDoItem, productOwner);

        then(productOwner).should().getFirstName();
        then(productOwner).should().getLastName();
        then(chat).should().connectWith("Steve.Rogers");
        then(toDoItem).should().getId();
        then(projectBacklogService).should().linkFor(toDoItemId);
        then(chatRoom).should().send(link);
        verifyNoMoreInteractions(toDoItem, productOwner, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyProductOwnerViaDevNull() {
        ToDoItem toDoItem = mock(ToDoItem.class);
        service.setType(NULL_TYPE);
        ProductOwner productOwner = mock(ProductOwner.class);

        service.notify(toDoItem, productOwner);

        then(devNullDirectory).should().forget();
        verifyNoMoreInteractions(toDoItem, productOwner, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyOwnerViaMail() {
        ToDoItem toDoItem = mock(ToDoItem.class);
        service.setType(MAIL);
        Owner owner = mock(Owner.class);
        EmailAddress emailAddress = mock(EmailAddress.class);
        given(owner.getEmailAddress()).willReturn(emailAddress);
        User user = mock(User.class);
        EmailAddress userEmailAddress = mock(EmailAddress.class);
        given(user.getEmailAddress()).willReturn(userEmailAddress);
        SessionHolder.instance().logIn(user);
        given(toDoItem.getId()).willReturn(13L);

        service.notify(toDoItem, owner);

        then(toDoItem).should(times(2)).getId();
        then(owner).should().getEmailAddress();
        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);
        then(mailClient).should().send(captor.capture());
        Mail mail = captor.getValue();
        assertThat(mail.getFrom()).isEqualTo(userEmailAddress);
        assertThat(mail.getTo()).isEqualTo(emailAddress);
        assertThat(mail.getTopic()).isEqualTo("NOTIFICATION ABOUT: 13");
        assertThat(mail.getContent()).isEqualTo("13");
        verifyNoMoreInteractions(toDoItem, owner, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyOwnerViaSms() {
        long toDoItemId = 123;
        ToDoItem toDoItem = mock(ToDoItem.class);
        given(toDoItem.getId()).willReturn(toDoItemId);
        String link = "www.letstalkaboutjava.com";
        given(projectBacklogService.linkFor(toDoItemId)).willReturn(link);
        service.setType(SMS);
        PhoneNumber phoneNumber = mock(PhoneNumber.class);
        Owner owner = mock(Owner.class);
        given(owner.getPhoneNumber()).willReturn(phoneNumber);

        service.notify(toDoItem, owner);

        then(owner).should().getPhoneNumber();
        then(toDoItem).should().getId();
        then(projectBacklogService).should().linkFor(toDoItemId);
        then(smsCommunicator).should().textTo(phoneNumber, link);
        verifyNoMoreInteractions(toDoItem, owner, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyOwnerViaChat() {
        long toDoItemId = 42;
        ToDoItem toDoItem = mock(ToDoItem.class);
        given(toDoItem.getId()).willReturn(toDoItemId);
        service.setType(DIRECT);
        Owner owner = mock(Owner.class);
        String firstName = "Steve";
        String lastName = "Rogers";
        given(owner.getFirstName()).willReturn(firstName);
        given(owner.getLastName()).willReturn(lastName);
        ChatRoom chatRoom = mock(ChatRoom.class);
        given(chat.connectWith("Steve.Rogers")).willReturn(chatRoom);
        String link = "www.refactoring.com";
        given(projectBacklogService.linkFor(anyLong())).willReturn(link);

        service.notify(toDoItem, owner);

        then(owner).should().getFirstName();
        then(owner).should().getLastName();
        then(chat).should().connectWith("Steve.Rogers");
        then(toDoItem).should().getId();
        then(projectBacklogService).should().linkFor(toDoItemId);
        then(chatRoom).should().send(link);
        verifyNoMoreInteractions(toDoItem, owner, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyOwnerViaDevNull() {
        ToDoItem toDoItem = mock(ToDoItem.class);
        service.setType(NULL_TYPE);
        Owner owner = mock(Owner.class);

        service.notify(toDoItem, owner);

        then(devNullDirectory).should().forget();
        verifyNoMoreInteractions(toDoItem, owner, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyWatcherViaMail() {
        ToDoItem toDoItem = mock(ToDoItem.class);
        service.setType(MAIL);
        Watcher watcher = mock(Watcher.class);
        EmailAddress emailAddress = mock(EmailAddress.class);
        given(watcher.getEmailAddress()).willReturn(emailAddress);
        User user = mock(User.class);
        EmailAddress userEmailAddress = mock(EmailAddress.class);
        given(user.getEmailAddress()).willReturn(userEmailAddress);
        SessionHolder.instance().logIn(user);
        given(toDoItem.getId()).willReturn(13L);

        service.notify(toDoItem, watcher);

        then(toDoItem).should(times(2)).getId();
        then(watcher).should().getEmailAddress();
        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);
        then(mailClient).should().send(captor.capture());
        Mail mail = captor.getValue();
        assertThat(mail.getFrom()).isEqualTo(userEmailAddress);
        assertThat(mail.getTo()).isEqualTo(emailAddress);
        assertThat(mail.getTopic()).isEqualTo("NOTIFICATION ABOUT: 13");
        assertThat(mail.getContent()).isEqualTo("13");
        verifyNoMoreInteractions(toDoItem, watcher, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyWatcherViaSms() {
        long toDoItemId = 123;
        ToDoItem toDoItem = mock(ToDoItem.class);
        given(toDoItem.getId()).willReturn(toDoItemId);
        String link = "www.letstalkaboutjava.com";
        given(projectBacklogService.linkFor(toDoItemId)).willReturn(link);
        service.setType(SMS);
        PhoneNumber phoneNumber = mock(PhoneNumber.class);
        Watcher watcher = mock(Watcher.class);
        given(watcher.getPhoneNumber()).willReturn(phoneNumber);

        service.notify(toDoItem, watcher);

        then(watcher).should().getPhoneNumber();
        then(toDoItem).should().getId();
        then(projectBacklogService).should().linkFor(toDoItemId);
        then(smsCommunicator).should().textTo(phoneNumber, link);
        verifyNoMoreInteractions(toDoItem, watcher, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyWatcherViaChat() {
        long toDoItemId = 42;
        ToDoItem toDoItem = mock(ToDoItem.class);
        given(toDoItem.getId()).willReturn(toDoItemId);
        service.setType(DIRECT);
        Watcher watcher = mock(Watcher.class);
        String firstName = "Steve";
        String lastName = "Rogers";
        given(watcher.getFirstName()).willReturn(firstName);
        given(watcher.getLastName()).willReturn(lastName);
        ChatRoom chatRoom = mock(ChatRoom.class);
        given(chat.connectWith("Steve.Rogers")).willReturn(chatRoom);
        String link = "www.refactoring.com";
        given(projectBacklogService.linkFor(anyLong())).willReturn(link);

        service.notify(toDoItem, watcher);

        then(watcher).should().getFirstName();
        then(watcher).should().getLastName();
        then(chat).should().connectWith("Steve.Rogers");
        then(toDoItem).should().getId();
        then(projectBacklogService).should().linkFor(toDoItemId);
        then(chatRoom).should().send(link);
        verifyNoMoreInteractions(toDoItem, watcher, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyWatcherViaDevNull() {
        ToDoItem toDoItem = mock(ToDoItem.class);
        service.setType(NULL_TYPE);
        Watcher watcher = mock(Watcher.class);

        service.notify(toDoItem, watcher);

        then(devNullDirectory).should().forget();
        verifyNoMoreInteractions(toDoItem, watcher, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyStakeholderViaMail() {
        ToDoItem toDoItem = mock(ToDoItem.class);
        service.setType(MAIL);
        Stakeholder stakeholder = mock(Stakeholder.class);
        EmailAddress emailAddress = mock(EmailAddress.class);
        given(stakeholder.getEmailAddress()).willReturn(emailAddress);
        User user = mock(User.class);
        EmailAddress userEmailAddress = mock(EmailAddress.class);
        given(user.getEmailAddress()).willReturn(userEmailAddress);
        SessionHolder.instance().logIn(user);
        given(toDoItem.getId()).willReturn(13L);

        service.notify(toDoItem, stakeholder);

        then(toDoItem).should(times(2)).getId();
        then(stakeholder).should().getEmailAddress();
        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);
        then(mailClient).should().send(captor.capture());
        Mail mail = captor.getValue();
        assertThat(mail.getFrom()).isEqualTo(userEmailAddress);
        assertThat(mail.getTo()).isEqualTo(emailAddress);
        assertThat(mail.getTopic()).isEqualTo("NOTIFICATION ABOUT: 13");
        assertThat(mail.getContent()).isEqualTo("13");
        verifyNoMoreInteractions(toDoItem, stakeholder, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyStakeholderViaSms() {
        long toDoItemId = 123;
        ToDoItem toDoItem = mock(ToDoItem.class);
        given(toDoItem.getId()).willReturn(toDoItemId);
        String link = "www.letstalkaboutjava.com";
        given(projectBacklogService.linkFor(toDoItemId)).willReturn(link);
        service.setType(SMS);
        PhoneNumber phoneNumber = mock(PhoneNumber.class);
        Stakeholder stakeholder = mock(Stakeholder.class);
        given(stakeholder.getPhoneNumber()).willReturn(phoneNumber);

        service.notify(toDoItem, stakeholder);

        then(stakeholder).should().getPhoneNumber();
        then(toDoItem).should().getId();
        then(projectBacklogService).should().linkFor(toDoItemId);
        then(smsCommunicator).should().textTo(phoneNumber, link);
        verifyNoMoreInteractions(toDoItem, stakeholder, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyStakeholderViaChat() {
        long toDoItemId = 42;
        ToDoItem toDoItem = mock(ToDoItem.class);
        given(toDoItem.getId()).willReturn(toDoItemId);
        service.setType(DIRECT);
        Stakeholder stakeholder = mock(Stakeholder.class);
        String firstName = "Steve";
        String lastName = "Rogers";
        given(stakeholder.getFirstName()).willReturn(firstName);
        given(stakeholder.getLastName()).willReturn(lastName);
        ChatRoom chatRoom = mock(ChatRoom.class);
        given(chat.connectWith("Steve.Rogers")).willReturn(chatRoom);
        String link = "www.refactoring.com";
        given(projectBacklogService.linkFor(anyLong())).willReturn(link);

        service.notify(toDoItem, stakeholder);

        then(stakeholder).should().getFirstName();
        then(stakeholder).should().getLastName();
        then(chat).should().connectWith("Steve.Rogers");
        then(toDoItem).should().getId();
        then(projectBacklogService).should().linkFor(toDoItemId);
        then(chatRoom).should().send(link);
        verifyNoMoreInteractions(toDoItem, stakeholder, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyStakeholderViaDevNull() {
        ToDoItem toDoItem = mock(ToDoItem.class);
        service.setType(NULL_TYPE);
        Stakeholder stakeholder = mock(Stakeholder.class);

        service.notify(toDoItem, stakeholder);

        then(devNullDirectory).should().forget();
        verifyNoMoreInteractions(toDoItem, stakeholder, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyUserViaMail() {
        ToDoItem toDoItem = mock(ToDoItem.class);
        service.setType(MAIL);
        User user = mock(User.class);
        EmailAddress emailAddress = mock(EmailAddress.class);
        given(user.getEmailAddress()).willReturn(emailAddress);
        User logged = mock(User.class);
        EmailAddress userEmailAddress = mock(EmailAddress.class);
        given(logged.getEmailAddress()).willReturn(userEmailAddress);
        SessionHolder.instance().logIn(logged);
        given(toDoItem.getId()).willReturn(13L);

        service.notify(toDoItem, user);

        then(toDoItem).should(times(2)).getId();
        then(user).should().getEmailAddress();
        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);
        then(mailClient).should().send(captor.capture());
        Mail mail = captor.getValue();
        assertThat(mail.getFrom()).isEqualTo(userEmailAddress);
        assertThat(mail.getTo()).isEqualTo(emailAddress);
        assertThat(mail.getTopic()).isEqualTo("NOTIFICATION ABOUT: 13");
        assertThat(mail.getContent()).isEqualTo("13");
        verifyNoMoreInteractions(toDoItem, user, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyUserViaSms() {
        long toDoItemId = 123;
        ToDoItem toDoItem = mock(ToDoItem.class);
        given(toDoItem.getId()).willReturn(toDoItemId);
        String link = "www.letstalkaboutjava.com";
        given(projectBacklogService.linkFor(toDoItemId)).willReturn(link);
        service.setType(SMS);
        PhoneNumber phoneNumber = mock(PhoneNumber.class);
        User user = mock(User.class);
        given(user.getPhoneNumber()).willReturn(phoneNumber);

        service.notify(toDoItem, user);

        then(user).should().getPhoneNumber();
        then(toDoItem).should().getId();
        then(projectBacklogService).should().linkFor(toDoItemId);
        then(smsCommunicator).should().textTo(phoneNumber, link);
        verifyNoMoreInteractions(toDoItem, user, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyUserViaChat() {
        long toDoItemId = 42;
        ToDoItem toDoItem = mock(ToDoItem.class);
        given(toDoItem.getId()).willReturn(toDoItemId);
        service.setType(DIRECT);
        User user = mock(User.class);
        String login = "steve.rogers";
        given(user.getLogin()).willReturn(login);
        ChatRoom chatRoom = mock(ChatRoom.class);
        given(chat.connectWith(login)).willReturn(chatRoom);
        String link = "www.refactoring.com";
        given(projectBacklogService.linkFor(anyLong())).willReturn(link);

        service.notify(toDoItem, user);

        then(user).should().getLogin();
        then(chat).should().connectWith(login);
        then(toDoItem).should().getId();
        then(projectBacklogService).should().linkFor(toDoItemId);
        then(chatRoom).should().send(link);
        verifyNoMoreInteractions(toDoItem, user, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldNotifyUserViaDevNull() {
        ToDoItem toDoItem = mock(ToDoItem.class);
        service.setType(NULL_TYPE);
        User user = mock(User.class);

        service.notify(toDoItem, user);

        then(devNullDirectory).should().forget();
        verifyNoMoreInteractions(toDoItem, user, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }
    
    @Test
    void shouldDoNothingNotifyingTeam() {
        ToDoItem toDoItem = mock(ToDoItem.class);
        service.setType(NULL_TYPE);
        Team team = mock(Team.class);
        given(team.getMembers()).willReturn(asList(mock(User.class), mock(User.class)));

        service.notify(toDoItem, team);

        then(team).should().getMembers();
        then(devNullDirectory).should(times(2)).forget();
        verifyNoMoreInteractions(toDoItem, team, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }

    @Test
    void shouldDoNothingNotifyingTeamsAboutProject() {
        ToDoItem toDoItem = mock(ToDoItem.class);
        service.setType(NULL_TYPE);
        Team team1 = mock(Team.class);
        given(team1.getMembers()).willReturn(asList(mock(User.class), mock(User.class)));
        Team team2 = mock(Team.class);
        given(team2.getMembers()).willReturn(asList(mock(User.class)));
        Project project = mock(Project.class);
        given(project.getTeams()).willReturn(asList(team1, team2));

        service.notifyTeamsAbout(toDoItem, project);

        then(project).should().getTeams();
        then(devNullDirectory).should(times(3)).forget();
        verifyNoMoreInteractions(toDoItem, project, projectBacklogService, devNullDirectory, chat, smsCommunicator, mailClient);
    }
}