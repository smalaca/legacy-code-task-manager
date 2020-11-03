package com.smalaca.taskamanager.model.embedded;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Assignee {
    @Column(name = "assignee_first_name")
    private String firstName;
    @Column(name = "assignee_last_name")
    private String lastName;
    @Column(name = "assignee_team_id")
    private Long teamId;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
}
