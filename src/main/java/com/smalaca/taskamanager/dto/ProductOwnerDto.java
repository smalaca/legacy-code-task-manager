package com.smalaca.taskamanager.dto;

import java.util.ArrayList;
import java.util.List;

public class ProductOwnerDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String phonePrefix;
    private String emailAddress;
    private List<Long> projectIds = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhonePrefix(String phonePrefix) {
        this.phonePrefix = phonePrefix;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setProjectIds(List<Long> projectIds) {
        this.projectIds = projectIds;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhonePrefix() {
        return phonePrefix;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public List<Long> getProjectIds() {
        return projectIds;
    }
}
