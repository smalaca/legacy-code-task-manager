package com.smalaca.taskamanager.client;

import com.smalaca.taskamanager.model.embedded.PhoneNumber;

public interface SmsCommunicatorClient {
    void textTo(PhoneNumber phoneNumber, String link);
}
