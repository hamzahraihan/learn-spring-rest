package com.learn.learn_spring_rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.learn.learn_spring_rest.entity.Contact;
import com.learn.learn_spring_rest.entity.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String>, JpaSpecificationExecutor<Contact> {
  Optional<Contact> findFirstByUserAndId(User user, String id);

}
