package com.smalaca.taskamanager.model.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Sprint {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @ManyToOne
    private Project project;

    @OneToMany
    private List<Task> tasks = new ArrayList<>();

    @ManyToMany
    private List<Story> stories = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void addStory(Story story) {
        stories.add(story);
    }
}
