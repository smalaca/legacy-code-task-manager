package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.entities.ProductOwner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductOwnerRepository extends CrudRepository<ProductOwner, Long> {
    Optional<ProductOwner> findByFirstNameAndLastName(String firstName, String lastName);
}
