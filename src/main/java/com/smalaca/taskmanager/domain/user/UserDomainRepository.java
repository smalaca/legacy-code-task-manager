package com.smalaca.taskmanager.domain.user;

import com.smalaca.taskamanager.model.entities.User;

public interface UserDomainRepository {
    User save(User user);
}
