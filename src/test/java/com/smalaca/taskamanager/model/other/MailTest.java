package com.smalaca.taskamanager.model.other;

import com.smalaca.taskamanager.model.embedded.EmailAddress;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MailTest {
    @Test
    void shouldCreateMail() {
        EmailAddress to = new EmailAddress();
        EmailAddress from = new EmailAddress();
        String topic = "Good talk";
        String content = "A lot to talk about";
        Mail mail = new Mail();
        mail.setTo(to);
        mail.setFrom(from);
        mail.setTopic(topic);
        mail.setContent(content);

        assertThat(mail.getTo()).isEqualTo(to);
        assertThat(mail.getFrom()).isEqualTo(from);
        assertThat(mail.getTopic()).isEqualTo(topic);
        assertThat(mail.getContent()).isEqualTo(content);
    }
}