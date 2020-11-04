package com.smalaca.taskamanager.dto;

import java.util.List;

@SuppressWarnings("MethodCount")
public class StoryDto {
    private Long id;
    private Long ownerId;
    private Long epicId;
    private String title;
    private String description;
    private String status;
    private String ownerLastName;
    private String ownerFirstName;
    private String ownerPhoneNumberNumber;
    private String ownerPhoneNumberPrefix;
    private String ownerEmailAddress;
    private AssigneeDto assignee;
    private List<WatcherDto> watchers;
    private List<StakeholderDto> stakeholders;

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerFirstName(String ownerFirstName) {
        this.ownerFirstName = ownerFirstName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    public void setOwnerPhoneNumberPrefix(String ownerPhoneNumberPrefix) {
        this.ownerPhoneNumberPrefix = ownerPhoneNumberPrefix;
    }

    public void setOwnerPhoneNumberNumber(String ownerPhoneNumberNumber) {
        this.ownerPhoneNumberNumber = ownerPhoneNumberNumber;
    }

    public void setOwnerEmailAddress(String ownerEmailAddress) {
        this.ownerEmailAddress = ownerEmailAddress;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public void setWatchers(List<WatcherDto> watchers) {
        this.watchers = watchers;
    }

    public void setStakeholders(List<StakeholderDto> stakeholders) {
        this.stakeholders = stakeholders;
    }

    public void setAssignee(AssigneeDto assignee) {
        this.assignee = assignee;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public String getOwnerPhoneNumberPrefix() {
        return ownerPhoneNumberPrefix;
    }

    public String getOwnerPhoneNumberNumber() {
        return ownerPhoneNumberNumber;
    }

    public String getOwnerEmailAddress() {
        return ownerEmailAddress;
    }

    public Long getEpicId() {
        return epicId;
    }

    public List<WatcherDto> getWatchers() {
        return watchers;
    }

    public List<StakeholderDto> getStakeholders() {
        return stakeholders;
    }

    public AssigneeDto getAssignee() {
        return assignee;
    }
}
