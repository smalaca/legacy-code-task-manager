package com.smalaca.acl.user;

import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.repository.UserRepository;
import com.smalaca.taskmanager.domain.user.UserDomainRepository;

public class AclUserDomainRepository implements UserDomainRepository {
    private final UserRepository userRepository;

    public AclUserDomainRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
