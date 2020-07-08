package com.smalaca.taskamanager.dto;

import java.util.List;

public class TeamMembersDto {
    private List<Long> userIds;

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public List<Long> getUserIds() {
        return userIds;
    }
}
