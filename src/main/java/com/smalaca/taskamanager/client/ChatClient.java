package com.smalaca.taskamanager.client;

import com.smalaca.taskamanager.model.other.ChatRoom;

public interface ChatClient {
    ChatRoom connectWith(String userName);
}
