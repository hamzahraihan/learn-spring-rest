package com.learn.learn_spring_rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.learn_spring_rest.entity.Contact;
import com.learn.learn_spring_rest.entity.User;

public interface ContactRepository extends JpaRepository<Contact, String> {
  Optional<Contact> findFirstByUserAndId(User user, String id);

}
