package com.smalaca.taskamanager.client;

import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class SmsCommunicatorClientImpl implements SmsCommunicatorClient {
    @Override
    public void textTo(PhoneNumber phoneNumber, String link) {

    }
}
