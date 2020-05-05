package com.smalaca.taskamanager.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUser() {
        User user = new User();
        user.setLogin("antman");
        user.setPassword("4ntZ__Ru13Z");
        user.setFirstName("Scott");
        user.setLastName("Lang");
        user.setTeamRole(TeamRole.DEVELOPER);
        user.setPhoneNumber(new PhoneNumber("4NT", "421399999"));
        user.setEmailAddress(new EmailAddress("antman@avengers.com"));

        assertThat(user.getId()).isNull();
        assertThat(user.getLogin()).isEqualTo("antman");
        assertThat(user.getPassword()).isEqualTo("4ntZ__Ru13Z");
        assertThat(user.getFirstName()).isEqualTo("Scott");
        assertThat(user.getLastName()).isEqualTo("Lang");
        assertThat(user.getTeamRole()).isEqualTo(TeamRole.DEVELOPER);
        assertThat(user.getPhoneNumber().getPrefix()).isEqualTo("4NT");
        assertThat(user.getPhoneNumber().getNumber()).isEqualTo("421399999");
        assertThat(user.getEmailAddress().getEmailAddress()).isEqualTo("antman@avengers.com");
    }
}