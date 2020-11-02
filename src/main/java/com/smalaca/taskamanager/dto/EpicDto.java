package com.smalaca.taskamanager.dto;

public class EpicDto {
    private Long id;
    private String title;
    private String description;
    private String status;
    private Long ownerId;
    private String ownerFirstName;
    private String ownerLastName;
    private String ownerPhoneNumberPrefix;
    private String ownerPhoneNumberNumber;
    private String ownerEmailAddress;
    private Long projectId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public void setOwnerFirstName(String ownerFirstName) {
        this.ownerFirstName = ownerFirstName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    public String getOwnerPhoneNumberPrefix() {
        return ownerPhoneNumberPrefix;
    }

    public void setOwnerPhoneNumberPrefix(String ownerPhoneNumberPrefix) {
        this.ownerPhoneNumberPrefix = ownerPhoneNumberPrefix;
    }

    public String getOwnerPhoneNumberNumber() {
        return ownerPhoneNumberNumber;
    }

    public void setOwnerPhoneNumberNumber(String ownerPhoneNumberNumber) {
        this.ownerPhoneNumberNumber = ownerPhoneNumberNumber;
    }

    public String getOwnerEmailAddress() {
        return ownerEmailAddress;
    }

    public void setOwnerEmailAddress(String ownerEmailAddress) {
        this.ownerEmailAddress = ownerEmailAddress;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
