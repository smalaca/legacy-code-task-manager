package com.smalaca.taskamanager.session;

import com.smalaca.taskamanager.model.entities.User;

public final class SessionHolder {
    private static final SessionHolder INSTANCE = new SessionHolder();

    private User user;

    private SessionHolder() {}

    public static SessionHolder instance() {
        return INSTANCE;
    }

    public void logIn(User user) {
        this.user = user;
    }

    public User logged() {
        return user;
    }
}
