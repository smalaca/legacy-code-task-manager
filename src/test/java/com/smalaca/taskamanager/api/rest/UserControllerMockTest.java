package com.smalaca.taskamanager.api.rest;

import com.google.common.collect.ImmutableList;
import com.smalaca.taskamanager.dto.UserDto;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.UserName;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.model.enums.TeamRole;
import com.smalaca.taskamanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.smalaca.taskamanager.model.enums.TeamRole.BUSINESS_ANALYSIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class UserControllerMockTest {
    private static final User DUMMY_USER_1 = dummyUser();
    private static final User DUMMY_USER_2 = dummyUser();
    private static final List<User> DUMMY_USERS = ImmutableList.of(DUMMY_USER_1, DUMMY_USER_2);
    private static final Long EXISTING_USER_ID = 13L;
    private static final Long NOT_EXISTING_USER_ID = 69L;
    private static final Long NEW_USER_ID = 42L;
    private static final String FIRST_NAME = "Steve";
    private static final String LAST_NAME = "Rogers";
    private static final String LOGIN = "captain america";
    private static final String PASSWORD = "avengers";
    private static final String EMAIL_ADDRESS = "dummy@gmail.com";
    private static final String PHONE_PREFIX = "+48";
    private static final String PHONE_NUMBER = "123456789";
    private static final TeamRole TEAM_ROLE = BUSINESS_ANALYSIS;
    private static final String TEAM_ROLE_AS_STRING = TEAM_ROLE.name();
    private static final User MOCKED_USER = aMockedUser();
    private static final UserDto MOCKED_USER_DTO = aMockedUserDto();
    private static final URI DUMMY_URI = URI.create("dummy/uri");

    @Mock private UriComponentsBuilder uriComponentsBuilder;
    @Mock private UriComponents uriComponents;

    @Mock private UserRepository repository;
    @InjectMocks private UserController controller;

    @Test
    void shouldReturnAllUsers() {
        given(repository.findAll()).willReturn(DUMMY_USERS);

        ResponseEntity<List<UserDto>> response = controller.getAllUsers();

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void shouldReturnNotFoundIfRetrievedUserDoesNotExist() {
        given(repository.findById(NOT_EXISTING_USER_ID)).willReturn(Optional.empty());

        ResponseEntity<UserDto> response = controller.getUser(NOT_EXISTING_USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldReturnExistingUser() {
        given(repository.findById(EXISTING_USER_ID)).willReturn(Optional.of(MOCKED_USER));

        ResponseEntity<UserDto> response = controller.getUser(EXISTING_USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertUserDto(response.getBody());
    }

    private void assertUserDto(UserDto user) {
        assertThat(user.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(user.getLastName()).isEqualTo(LAST_NAME);
        assertThat(user.getId()).isEqualTo(EXISTING_USER_ID);
        assertThat(user.getLogin()).isEqualTo(LOGIN);
        assertThat(user.getPassword()).isEqualTo(PASSWORD);
    }

    @Test
    void shouldInformAboutConflictWhenCreatedUserAlreadyExists() {
        given(repository.findByUserNameFirstNameAndUserNameLastName(FIRST_NAME, LAST_NAME)).willReturn(Optional.of(MOCKED_USER));

        ResponseEntity<Void> response = controller.createUser(MOCKED_USER_DTO, uriComponentsBuilder);

        assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
    }

    @Test
    void shouldCreateUser() {
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        given(repository.findByUserNameFirstNameAndUserNameLastName(FIRST_NAME, LAST_NAME)).willReturn(Optional.empty());
        given(repository.save(any())).willAnswer(invocationOnMock -> {
            User argument = invocationOnMock.getArgument(0);
            Field id = User.class.getDeclaredField("id");
            id.setAccessible(true);
            id.set(argument, NEW_USER_ID);
            return argument;
        });
        given(uriComponentsBuilder.path(anyString())).willReturn(uriComponentsBuilder);
        given(uriComponentsBuilder.buildAndExpand(NEW_USER_ID)).willReturn(uriComponents);
        given(uriComponents.toUri()).willReturn(DUMMY_URI);

        ResponseEntity<Void> response = controller.createUser(MOCKED_USER_DTO, uriComponentsBuilder);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getHeaders().getLocation().normalize()).isEqualTo(DUMMY_URI);
        then(repository).should().save(argumentCaptor.capture());
        User user = argumentCaptor.getValue();
        assertThat(user.getUserName().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(user.getTeamRole()).isEqualTo(TEAM_ROLE);
        assertThat(user.getUserName().getLastName()).isEqualTo(LAST_NAME);
        assertThat(user.getLogin()).isEqualTo(LOGIN);
        assertThat(user.getPassword()).isEqualTo(PASSWORD);
    }

    @Test
    void shouldReturnNotFoundIfUpdatedUserDoesNotExist() {
        given(repository.findById(NOT_EXISTING_USER_ID)).willReturn(Optional.empty());

        ResponseEntity<UserDto> response = controller.updateUser(NOT_EXISTING_USER_ID, MOCKED_USER_DTO);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldNotUpdateAnythingWhenNoChangesSend() {
        UserDto userDto = mock(UserDto.class);
        given(userDto.getLogin()).willReturn(null);
        given(userDto.getPassword()).willReturn(null);
        User user = aMockedUser();
        given(repository.findById(EXISTING_USER_ID)).willReturn(Optional.of(user));
        given(repository.save(any())).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ResponseEntity<UserDto> response = controller.updateUser(EXISTING_USER_ID, userDto);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        then(user).should(never()).setLogin(any());
        then(user).should(never()).setPassword(any());
    }

    @Test
    void shouldUpdateAboutSuccessIfUpdatingExistingUser() {
        User user = aMockedUser();
        given(repository.findById(EXISTING_USER_ID)).willReturn(Optional.of(user));
        given(repository.save(any())).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ResponseEntity<UserDto> response = controller.updateUser(EXISTING_USER_ID, MOCKED_USER_DTO);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        then(user).should().setLogin(LOGIN);
        then(user).should().setPassword(PASSWORD);
        then(user).should().setTeamRole(BUSINESS_ANALYSIS);
        ArgumentCaptor<PhoneNumber> phoneNumberCaptor = ArgumentCaptor.forClass(PhoneNumber.class);
        then(user).should().setPhoneNumber(phoneNumberCaptor.capture());
        assertThat(phoneNumberCaptor.getValue().getNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(phoneNumberCaptor.getValue().getPrefix()).isEqualTo(PHONE_PREFIX);
        ArgumentCaptor<EmailAddress> emailAddressCaptor = ArgumentCaptor.forClass(EmailAddress.class);
        then(user).should().setEmailAddress(emailAddressCaptor.capture());
        assertThat(emailAddressCaptor.getValue().getEmailAddress()).isEqualTo(EMAIL_ADDRESS);
        then(repository).should().save(user);
    }

    @Test
    void shouldReturnNotFoundIfDeletedUserDoesNotExist() {
        given(repository.findById(NOT_EXISTING_USER_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> response = controller.deleteUser(NOT_EXISTING_USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldDeleteExistingUser() {
        given(repository.findById(EXISTING_USER_ID)).willReturn(Optional.of(MOCKED_USER));

        ResponseEntity<Void> response = controller.deleteUser(EXISTING_USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        then(repository).should().delete(MOCKED_USER);
    }

    private static User aMockedUser() {
        User user = mock(User.class);
        given(user.getId()).willReturn(EXISTING_USER_ID);
        UserName userName = aMockedUserName();
        given(user.getUserName()).willReturn(userName);
        given(user.getLogin()).willReturn(LOGIN);
        given(user.getPassword()).willReturn(PASSWORD);
        return user;
    }

    private static User dummyUser() {
        UserName userName = mock(UserName.class);
        User user = mock(User.class);
        given(user.getUserName()).willReturn(userName);
        return user;
    }

    private static UserName aMockedUserName() {
        UserName userName = mock(UserName.class);
        given(userName.getFirstName()).willReturn(FIRST_NAME);
        given(userName.getLastName()).willReturn(LAST_NAME);
        return userName;
    }

    private static UserDto aMockedUserDto() {
        UserDto user = mock(UserDto.class);
        given(user.getId()).willReturn(EXISTING_USER_ID);
        given(user.getFirstName()).willReturn(FIRST_NAME);
        given(user.getLastName()).willReturn(LAST_NAME);
        given(user.getLogin()).willReturn(LOGIN);
        given(user.getPassword()).willReturn(PASSWORD);
        given(user.getEmailAddress()).willReturn(EMAIL_ADDRESS);
        given(user.getPhonePrefix()).willReturn(PHONE_PREFIX);
        given(user.getPhoneNumber()).willReturn(PHONE_NUMBER);
        given(user.getTeamRole()).willReturn(TEAM_ROLE_AS_STRING);
        return user;
    }
}