package com.learn.learn_spring_rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learn.learn_spring_rest.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {

}