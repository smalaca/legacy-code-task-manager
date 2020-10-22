package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.domain.ProductOwner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOwnerRepository extends CrudRepository<ProductOwner, Long> {
}
