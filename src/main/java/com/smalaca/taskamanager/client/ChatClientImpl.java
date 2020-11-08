package com.smalaca.taskamanager.client;

import com.smalaca.taskamanager.model.other.ChatRoom;
import org.springframework.stereotype.Service;

@Service
public class ChatClientImpl implements ChatClient {
    @Override
    public ChatRoom connectWith(String userName) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setUserName(userName);
        return chatRoom;
    }
}
