package com.smalaca.taskamanager.api.rest;

import com.smalaca.taskamanager.domain.TeamRole;
import com.smalaca.taskamanager.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import static com.smalaca.taskamanager.domain.TeamRole.BUSINESS_ANALYSIS;
import static com.smalaca.taskamanager.domain.TeamRole.DEVELOPER;
import static com.smalaca.taskamanager.domain.TeamRole.TESTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

class UserControllerTest {
    private static final Long EXISTING_USER_ID = 1L;
    private static final Long NOT_EXISTING_USER_ID = 101L;
    private static final UserDto NO_USER_DATA = null;
    private static final String FIRST_NAME = "Bruce";
    private static final String LAST_NAME = "Banner";
    private static final String LOGIN = "bbanner";
    private static final String PASSWORD = "somethingExtremelyConfidential";

    private UserController controller = new UserController(new InMemoryUserRepository());

    @Test
    void shouldReturnAllUsers() {
        ResponseEntity<List<UserDto>> response = controller.getAllUsers();

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).hasSize(5)
                .anySatisfy(userDto -> assertUser(userDto, 1L, "Bruce", "Banner", "bbanner", DEVELOPER, "hulk@fake.domain.com", "123456789"))
                .anySatisfy(userDto -> assertUser(userDto, 2L, "Peter", "Parker", "Spider Man", BUSINESS_ANALYSIS, "spiderman@fake.domain.com", "987654321"))
                .anySatisfy(userDto -> assertUser(userDto, 3L, "Clark", "Kent", "Super Man", DEVELOPER, "krypton@fake.domain.com", "111111111"))
                .anySatisfy(userDto -> assertUser(userDto, 4L, "Bruce", "Wayne", "Batman", TESTER, "gotham@fake.domain.com", "121212129"))
                .anySatisfy(userDto -> assertUser(userDto, 5L, "Anthony", "Stark", "Iron Man", DEVELOPER, "money@fake.domain.com", "123123123"));
    }

    private void assertUser(UserDto updated, long id, String firstName, String lastName, String login, TeamRole teamRole, String emailAddress, String phoneNumber) {
        assertThat(updated.getId()).isEqualTo(id);
        assertThat(updated.getFirstName()).isEqualTo(firstName);
        assertThat(updated.getLastName()).isEqualTo(lastName);
        assertThat(updated.getLogin()).isEqualTo(login);
        assertThat(updated.getPassword()).isEqualTo("somethingExtremelyConfidential");
        assertThat(updated.getEmailAddress()).isEqualTo(emailAddress);
        assertThat(updated.getPhonePrefix()).isEqualTo("XYZ");
        assertThat(updated.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(updated.getTeamRole()).isEqualTo(teamRole.name());
    }

    @Test
    void shouldReturnNotFoundIfRetrievedUserDoesNotExist() {
        ResponseEntity<UserDto> response = controller.getUser(NOT_EXISTING_USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldReturnExistingUser() {
        ResponseEntity<UserDto> response = controller.getUser(EXISTING_USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertUser(response.getBody());
    }

    @Test
    void shouldInformAboutConflictWhenCreatedUserAlreadyExists() {
        UriComponentsBuilder uriComponentsBuilder = null;
        UserDto existingUser = givenUserWithFirstAndLastName(FIRST_NAME, LAST_NAME);

        ResponseEntity<Void> response = controller.createUser(existingUser, uriComponentsBuilder);

        assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
    }

    @Test
    void shouldCreateUser() {
        UserDto user = givenUserWithFirstAndLastName("Natasha", "Romanow");
        UriComponentsBuilder uriComponentsBuilder = fromUriString("/");

        ResponseEntity<Void> response = controller.createUser(user, uriComponentsBuilder);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getHeaders().getLocation().getPath()).matches("/user/[0-9a-z\\-]+");
        assertThatUserWasCreated("Natasha", "Romanow", response.getHeaders());
    }

    private UserDto givenUserWithFirstAndLastName(String firstName, String lastName) {
        UserDto userDto = new UserDto();
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        return userDto;
    }

    private void assertThatUserWasCreated(String firstName, String lastName, HttpHeaders headers) {
        String userId = headers.getLocation().getPath().replace("/user/", "");
        UserDto user = controller.getUser(Long.valueOf(userId)).getBody();

        assertThat(user.getFirstName()).isEqualTo(firstName);
        assertThat(user.getLastName()).isEqualTo(lastName);
    }

    @Test
    void shouldReturnNotFoundIfUpdatedUserDoesNotExist() {
        ResponseEntity<UserDto> response = controller.updateUser(NOT_EXISTING_USER_ID, NO_USER_DATA);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldNotUpdateAnythingWhenNoChangesSend() {
        ResponseEntity<UserDto> response = controller.updateUser(EXISTING_USER_ID, new UserDto());

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertUser(controller.getUser(EXISTING_USER_ID).getBody());
    }

    private void assertUser(UserDto user) {
        assertThat(user.getId()).isEqualTo(EXISTING_USER_ID);
        assertThat(user.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(user.getLastName()).isEqualTo(LAST_NAME);
        assertThat(user.getLogin()).isEqualTo(LOGIN);
        assertThat(user.getPassword()).isEqualTo(PASSWORD);
    }

    @Test
    void shouldUpdateAboutSuccessIfUpdatingExistingUser() {
        String newLogin = randomString();
        String newPassword = randomString();
        String phonePrefix = randomString();
        String phoneNumber = randomString();
        String emailAddress = randomString();
        String teamRole = "BUSINESS_ANALYSIS";
        UserDto userDto = new UserDto();
        userDto.setLogin(newLogin);
        userDto.setPassword(newPassword);
        userDto.setEmailAddress(emailAddress);
        userDto.setPhonePrefix(phonePrefix);
        userDto.setPhoneNumber(phoneNumber);
        userDto.setTeamRole(teamRole);

        ResponseEntity<UserDto> response = controller.updateUser(EXISTING_USER_ID, userDto);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        UserDto actualDto = response.getBody();
        assertThat(actualDto.getId()).isEqualTo(EXISTING_USER_ID);
        assertThat(actualDto.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actualDto.getLastName()).isEqualTo(LAST_NAME);
        assertThat(actualDto.getLogin()).isEqualTo(newLogin);
        assertThat(actualDto.getPassword()).isEqualTo(newPassword);
        assertThat(actualDto.getEmailAddress()).isEqualTo(emailAddress);
        assertThat(actualDto.getPhonePrefix()).isEqualTo(phonePrefix);
        assertThat(actualDto.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(actualDto.getTeamRole()).isEqualTo(teamRole);
        UserDto updated = controller.getUser(EXISTING_USER_ID).getBody();
        assertThat(updated.getLogin()).isEqualTo(newLogin);
        assertThat(updated.getPassword()).isEqualTo(newPassword);
        assertThat(updated.getEmailAddress()).isEqualTo(emailAddress);
        assertThat(updated.getPhonePrefix()).isEqualTo(phonePrefix);
        assertThat(updated.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(updated.getTeamRole()).isEqualTo(teamRole);
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    @Test
    void shouldReturnNotFoundIfDeletedUserDoesNotExist() {
        ResponseEntity<Void> response = controller.deleteUser(NOT_EXISTING_USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldDeleteExistingUser() {
        ResponseEntity<Void> response = controller.deleteUser(EXISTING_USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(controller.getUser(EXISTING_USER_ID).getStatusCode()).isEqualTo(NOT_FOUND);
    }
}