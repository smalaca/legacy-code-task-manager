package com.smalaca.taskamanager.api.rest;

import com.smalaca.taskamanager.domain.EmailAddress;
import com.smalaca.taskamanager.domain.PhoneNumber;
import com.smalaca.taskamanager.domain.TeamRole;
import com.smalaca.taskamanager.domain.User;
import com.smalaca.taskamanager.domain.UserRepository;
import org.apache.commons.lang3.RandomUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.smalaca.taskamanager.domain.TeamRole.BUSINESS_ANALYSIS;
import static com.smalaca.taskamanager.domain.TeamRole.DEVELOPER;
import static com.smalaca.taskamanager.domain.TeamRole.TESTER;
import static java.util.List.copyOf;

class InMemoryUserRepository implements UserRepository {
    private static final String DUMMY_PASSWORD = "somethingExtremelyConfidential";
    private final Map<Long, User> users = new HashMap<>();

    public InMemoryUserRepository() {
        users.put(1L, aUser(1L, "Bruce", "Banner", "bbanner", DEVELOPER, "hulk@fake.domain.com", "123456789"));
        users.put(2L, aUser(2L, "Peter", "Parker", "Spider Man", BUSINESS_ANALYSIS, "spiderman@fake.domain.com", "987654321"));
        users.put(3L, aUser(3L, "Clark", "Kent", "Super Man", DEVELOPER, "krypton@fake.domain.com", "111111111"));
        users.put(4L, aUser(4L, "Bruce", "Wayne", "Batman", TESTER, "gotham@fake.domain.com", "121212129"));
        users.put(5L, aUser(5L, "Anthony", "Stark", "Iron Man", DEVELOPER, "money@fake.domain.com", "123123123"));
    }

    private User aUser(long id, String firstName, String lastName, String login, TeamRole teamRole, String emailAddress, String phoneNumber) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setLogin(login);
        user.setPassword(DUMMY_PASSWORD);
        user.setTeamRole(teamRole);
        EmailAddress email = new EmailAddress();
        email.setEmailAddress(emailAddress);
        user.setEmailAddress(email);
        PhoneNumber number = new PhoneNumber();
        number.setPrefix("XYZ");
        number.setNumber(phoneNumber);
        user.setPhoneNumber(number);
        setId(id, user);

        return user;
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            setId(RandomUtils.nextLong(), user);
        }

        users.put(user.getId(), user);

        return user;
    }

    private void setId(long id, User user) {
        try {
            Field fieldId = User.class.getDeclaredField("id");
            fieldId.setAccessible(true);
            fieldId.set(user, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> users) {
        return null;
    }

    @Override
    public Optional<User> findByFirstNameAndLastName(String firstName, String lastName) {
        for (User user : findAll()) {
            if (user.getFirstName().equals(firstName) && user.getLastName().equals(lastName)) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findById(Long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        }

        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<User> findAll() {
        return copyOf(users.values());
    }

    @Override
    public Iterable<User> findAllById(Iterable<Long> ids) {
        List<User> found = new ArrayList<>();

        ids.forEach(id -> {
            if (users.containsKey(id)) {
                found.add(users.get(id));
            }
        });

        return found;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }

    @Override
    public void deleteAll(Iterable<? extends User> iterable) {

    }

    @Override
    public void deleteAll() {

    }
}
