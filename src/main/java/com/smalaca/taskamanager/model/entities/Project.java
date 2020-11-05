package com.smalaca.taskamanager.model.entities;

import com.smalaca.taskamanager.model.enums.ProjectStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static com.smalaca.taskamanager.model.enums.ProjectStatus.IDEA;

@Entity
public class Project {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private ProjectStatus projectStatus = IDEA;

    @ManyToOne
    private ProductOwner productOwner;

    @OneToMany
    private List<Team> teams = new ArrayList<>();
    
    @OneToMany
    private List<Epic> epics = new ArrayList<>();

    @OneToMany
    private List<Sprint> sprints = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public ProductOwner getProductOwner() {
        return productOwner;
    }

    public void setProductOwner(ProductOwner productOwner) {
        this.productOwner = productOwner;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void addTeam(Team team) {
        teams.add(team);
    }

    public List<Epic> getEpics() {
        return epics;
    }

    public void addEpic(Epic epic) {
        epics.add(epic);
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void addSprint(Sprint sprint) {
        sprints.add(sprint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Project project = (Project) o;

        return new EqualsBuilder()
                .append(id, project.id)
                .append(name, project.name)
                .isEquals();
    }

    @Override
    @SuppressWarnings("MagicNumber")
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .toHashCode();
    }
}
