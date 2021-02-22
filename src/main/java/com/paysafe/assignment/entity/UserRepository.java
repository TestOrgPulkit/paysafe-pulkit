package com.paysafe.assignment.entity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// this class is used to perform CRUD operations on our db table
@Repository
public interface UserRepository extends CrudRepository<UserEntity, String> {
    UserEntity findByEmail(String email);

    UserEntity findByMerchantCustomerId(String merchantCustomerId);

}
