package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.entities.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
}
