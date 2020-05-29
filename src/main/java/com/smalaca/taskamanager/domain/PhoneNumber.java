package com.smalaca.taskamanager.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PhoneNumber {
    @Column(name = "phone_prefix")
    private String prefix;

    @Column(name = "phone_number")
    private String number;

    private PhoneNumber() {}

    public PhoneNumber(String prefix, String number) {
        this.prefix = prefix;
        this.number = number;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNumber() {
        return number;
    }
}
