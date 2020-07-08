package com.smalaca.taskamanager.dto;

import java.util.List;

public class TeamMembersDto {
    private long teamId;
    private List<Long> userIds;

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public List<Long> getUserIds() {
        return userIds;
    }
}
