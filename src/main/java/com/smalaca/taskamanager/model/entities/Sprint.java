package com.smalaca.taskamanager.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
