package com.smalaca.taskamanager.dto;

import java.util.List;

@SuppressWarnings("MethodCount")
public class TaskDto {
    private Long id;
    private String description;
    private String status;
    private String title;
    private String ownerFirstName;
    private Long ownerId;
    private String ownerPhoneNumberPrefix;
    private String ownerLastName;
    private String ownerEmailAddress;
    private String ownerPhoneNumberNumber;
    private List<WatcherDto> watchers;
    private Long storyId;
    private AssigneeDto assignee;
    private List<StakeholderDto> stakeholders;

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
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

    public String getStatus() {
        return status;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getOwnerFirstName() {
        return ownerFirstName;
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

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public String getOwnerPhoneNumberPrefix() {
        return ownerPhoneNumberPrefix;
    }

    public String getOwnerPhoneNumberNumber() {
        return ownerPhoneNumberNumber;
    }

    public void setOwnerPhoneNumberNumber(String ownerPhoneNumberNumber) {
        this.ownerPhoneNumberNumber = ownerPhoneNumberNumber;
    }

    public void setOwnerPhoneNumberPrefix(String ownerPhoneNumberPrefix) {
        this.ownerPhoneNumberPrefix = ownerPhoneNumberPrefix;
    }

    public void setOwnerEmailAddress(String ownerEmailAddress) {
        this.ownerEmailAddress = ownerEmailAddress;
    }

    public String getOwnerEmailAddress() {
        return ownerEmailAddress;
    }

    public void setStoryId(Long storyId) {
        this.storyId = storyId;
    }

    public void setWatchers(List<WatcherDto> watchers) {
        this.watchers = watchers;
    }

    public Long getStoryId() {
        return storyId;
    }

    public List<WatcherDto> getWatchers() {
        return watchers;
    }

    public AssigneeDto getAssignee() {
        return assignee;
    }

    public List<StakeholderDto> getStakeholders() {
        return stakeholders;
    }

    public void setStakeholders(List<StakeholderDto> stakeholders) {
        this.stakeholders = stakeholders;
    }

    public void setAssignee(AssigneeDto assignee) {
        this.assignee = assignee;
    }
}
