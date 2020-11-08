package com.smalaca.taskamanager.session;

import com.smalaca.taskamanager.model.entities.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionHolderTest {
    @Test
    void shouldReturnAlwaysTheSameInstance() {
        SessionHolder sessionHolder = SessionHolder.instance();

        assertThat(SessionHolder.instance()).isEqualTo(sessionHolder);
        assertThat(SessionHolder.instance()).isEqualTo(sessionHolder);
    }

    @Test
    void shouldReturnLoggedUser() {
        User user = new User();

        SessionHolder.instance().logIn(user);

        assertThat(SessionHolder.instance().logged()).isEqualTo(user);
    }
}