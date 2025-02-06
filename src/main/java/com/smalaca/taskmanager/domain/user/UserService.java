package com.smalaca.taskmanager.domain.user;

import com.smalaca.taskamanager.dto.UserDto;
import com.smalaca.taskamanager.model.embedded.UserName;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.model.enums.TeamRole;
import com.smalaca.taskamanager.repository.UserRepository;

public class UserService {
    private final UserDomainRepository userRepository;

    public UserService(UserDomainRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(UserDto userDto) {
        User user = new User();
        user.setTeamRole(TeamRole.valueOf(userDto.getTeamRole()));
        UserName userName = new UserName();
        userName.setFirstName(userDto.getFirstName());
        userName.setLastName(userDto.getLastName());
        user.setUserName(userName);
        user.setLogin(userDto.getLogin());
        user.setPassword(userDto.getPassword());

        return userRepository.save(user);
    }
}
