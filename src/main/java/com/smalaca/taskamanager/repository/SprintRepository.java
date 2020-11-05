package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.entities.Sprint;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SprintRepository extends CrudRepository<Sprint, Long> {
    Optional<Sprint> findByNameAndProjectId(String name, Long projectId);
}
