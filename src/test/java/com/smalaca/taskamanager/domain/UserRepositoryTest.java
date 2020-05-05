package com.smalaca.taskamanager.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRepositoryTest {
    @Autowired private UserRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void shouldFindNoUserWhenUserNotExist() {
        repository.saveAll(asList(user("Peter", "Parker"), user("Tony", "Stark"), user("Steve", "Rogers")));

        Optional<User> actual = repository.findByFirstNameAndLastName("Natasha", "Romanow");

        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    void shouldFindUserByFirstAndLastName() {
        repository.saveAll(asList(user("Peter", "Parker"), user("Tony", "Stark"), user("Steve", "Rogers")));

        User actual = repository.findByFirstNameAndLastName("Peter", "Parker").get();

        assertThat(actual.getFirstName()).isEqualTo("Peter");
        assertThat(actual.getLastName()).isEqualTo("Parker");
    }

    private User user(String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setLogin("login");
        user.setPassword("password");
        user.setEmailAddress(new EmailAddress("dummy@gmail.com"));
        user.setPhoneNumber(new PhoneNumber("+48", "123456789"));

        return user;
    }
}