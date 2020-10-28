package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUserNameFirstNameAndUserNameLastName(String firstName, String lastName);
}
