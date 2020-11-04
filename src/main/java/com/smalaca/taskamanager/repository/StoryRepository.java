package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.entities.Story;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository extends CrudRepository<Story, Long> {
}
