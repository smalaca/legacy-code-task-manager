package com.smalaca.taskamanager.client;

import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import org.junit.jupiter.api.Test;

class SmsCommunicatorClientImplTest {
    @Test
    void shouldDoNothing() {
        new SmsCommunicatorClientImpl().textTo(new PhoneNumber(), "www.refactoring.com");
    }
}