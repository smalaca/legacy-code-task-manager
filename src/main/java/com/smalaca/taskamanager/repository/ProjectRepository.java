package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.entities.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {
}
