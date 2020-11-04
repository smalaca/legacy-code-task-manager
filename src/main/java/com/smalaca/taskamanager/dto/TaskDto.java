package com.smalaca.taskamanager.dto;

import java.util.List;

@SuppressWarnings("MethodCount")
public class TaskDto {
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
    private Long storyId;
    private List<WatcherDto> watchers;
    private List<StakeholderDto> stakeholders;
    private AssigneeDto assignee;

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

    public Long getStoryId() {
        return storyId;
    }

    public void setStoryId(Long storyId) {
        this.storyId = storyId;
    }

    public List<WatcherDto> getWatchers() {
        return watchers;
    }

    public void setWatchers(List<WatcherDto> watchers) {
        this.watchers = watchers;
    }

    public List<StakeholderDto> getStakeholders() {
        return stakeholders;
    }

    public void setStakeholders(List<StakeholderDto> stakeholders) {
        this.stakeholders = stakeholders;
    }

    public AssigneeDto getAssignee() {
        return assignee;
    }

    public void setAssignee(AssigneeDto assignee) {
        this.assignee = assignee;
    }
}