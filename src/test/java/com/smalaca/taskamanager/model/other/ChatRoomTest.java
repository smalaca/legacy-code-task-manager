package com.smalaca.taskamanager.model.other;

import org.junit.jupiter.api.Test;

class ChatRoomTest {
    @Test
    void shouldDoNothingWhenSend() {
        new ChatRoom().send("www.legacy.code.com");
    }
}