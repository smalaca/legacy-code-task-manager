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
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class CommunicationServiceImplTest {
    public static final ToDoItem TO_DO_ITEM = mock(ToDoItem.class);
    private final CommunicationServiceImpl service = new CommunicationServiceImpl();

    @Test
    void shouldSetType() {
        service.setType(CommunicatorType.NULL_TYPE);
    }

    @Test
    void shouldDoNothingNotifyingProductOwner() {
        ProductOwner productOwner = mock(ProductOwner.class);

        service.notify(TO_DO_ITEM, productOwner);

        verifyNoInteractions(TO_DO_ITEM, productOwner);
    }

    @Test
    void shouldDoNothingNotifyingOwner() {
        Owner owner = mock(Owner.class);

        service.notify(TO_DO_ITEM, owner);

        verifyNoInteractions(TO_DO_ITEM, owner);
    }

    @Test
    void shouldDoNothingNotifyingWatcher() {
        Watcher watcher = mock(Watcher.class);

        service.notify(TO_DO_ITEM, watcher);

        verifyNoInteractions(TO_DO_ITEM, watcher);
    }

    @Test
    void shouldDoNothingNotifyingUser() {
        User user = mock(User.class);

        service.notify(TO_DO_ITEM, user);

        verifyNoInteractions(TO_DO_ITEM, user);
    }

    @Test
    void shouldDoNothingNotifyingStakeholder() {
        Stakeholder stakeholder = mock(Stakeholder.class);

        service.notify(TO_DO_ITEM, stakeholder);

        verifyNoInteractions(TO_DO_ITEM, stakeholder);
    }

    @Test
    void shouldDoNothingNotifyingTeam() {
        Team team = mock(Team.class);

        service.notify(TO_DO_ITEM, team);

        verifyNoInteractions(TO_DO_ITEM, team);
    }

    @Test
    void shouldDoNothingNotifyingTeamsAboutProject() {
        Project project = mock(Project.class);

        service.notifyTeamsAbout(TO_DO_ITEM, project);

        verifyNoInteractions(TO_DO_ITEM, project);
    }
}