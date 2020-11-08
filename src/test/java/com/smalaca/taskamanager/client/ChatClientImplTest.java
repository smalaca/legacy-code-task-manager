package com.smalaca.taskamanager.client;

import com.smalaca.taskamanager.model.other.ChatRoom;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatClientImplTest {
    @Test
    void shouldReturnChatRoomForUserName() {
        String userName = "steve.rogers";

        ChatRoom chatRoom = new ChatClientImpl().connectWith(userName);

        assertThat(chatRoom.getUserName()).isEqualTo(userName);
    }
}