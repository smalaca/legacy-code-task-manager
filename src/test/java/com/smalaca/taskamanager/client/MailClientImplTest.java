package com.smalaca.taskamanager.client;

import com.smalaca.taskamanager.model.other.Mail;
import org.junit.jupiter.api.Test;

class MailClientImplTest {
    @Test
    void shouldSendMail() {
        new MailClientImpl().send(new Mail());
    }
}