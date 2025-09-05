package com.learn.learn_spring_rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.learn_spring_rest.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, String> {

}
