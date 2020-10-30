package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.entities.Epic;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpicRepository extends CrudRepository<Epic, Long> {
}
