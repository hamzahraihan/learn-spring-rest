package com.learn.learn_spring_rest.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learn.learn_spring_rest.entity.Contact;
import com.learn.learn_spring_rest.entity.User;
import com.learn.learn_spring_rest.model.ContactResponse;
import com.learn.learn_spring_rest.model.CreateContactRequest;
import com.learn.learn_spring_rest.repository.ContactRepository;

import jakarta.transaction.Transactional;

@Service
public class ContactService {

  @Autowired
  private ContactRepository contactRepository;

  @Autowired
  private ValidationService validationService;

  @Transactional
  public ContactResponse create(User user, CreateContactRequest request) {
    validationService.validate(request);

    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setFirstName(request.getFirstName());
    contact.setLastName(request.getLastName());
    contact.setEmail(request.getEmail());
    contact.setPhone(request.getPhone());
    contact.setUser(user);

    contactRepository.save(contact);

    return ContactResponse.builder()
        .id(contact.getId())
        .firstName(contact.getFirstName())
        .lastName(contact.getLastName())
        .email(contact.getEmail())
        .phone(contact.getPhone())
        .build();
  }

}
