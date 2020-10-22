package com.smalaca.taskamanager.domain;

import com.smalaca.taskamanager.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.function.Consumer;

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
    void shouldFindUsersByIds() {
        Long id1 = repository.save(user("Peter", "Parker")).getId();
        repository.save(user("Tony", "Stark"));
        Long id3 = repository.save(user("Steve", "Rogers")).getId();

        Iterable<User> actual = repository.findAllById(asList(id1, id3, 13L));

        assertThat(actual).hasSize(2)
                .anySatisfy(isUser("Peter", "Parker"))
                .anySatisfy(isUser("Steve", "Rogers"));
    }

    private Consumer<User> isUser(String firstName, String lastName) {
        return actual -> {
            assertThat(actual.getFirstName()).isEqualTo(firstName);
            assertThat(actual.getLastName()).isEqualTo(lastName);
        };
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
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress("dummy@gmail.com");
        user.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix("+48");
        phoneNumber.setNumber("123456789");
        user.setPhoneNumber(phoneNumber);

        return user;
    }
}