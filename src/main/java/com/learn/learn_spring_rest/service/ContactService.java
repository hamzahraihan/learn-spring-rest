package com.learn.learn_spring_rest.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.learn.learn_spring_rest.entity.Contact;
import com.learn.learn_spring_rest.entity.User;
import com.learn.learn_spring_rest.model.ContactResponse;
import com.learn.learn_spring_rest.model.CreateContactRequest;
import com.learn.learn_spring_rest.repository.ContactRepository;

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

    return toContactResponse(contact);
  }

  @Transactional(readOnly = true)
  public ContactResponse get(User user, String id) {
    Contact contact = contactRepository.findFirstByUserAndId(user, id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found!"));

    return toContactResponse(contact);
  }

  private ContactResponse toContactResponse(Contact contact) {
    return ContactResponse.builder()
        .id(contact.getId())
        .firstName(contact.getFirstName())
        .lastName(contact.getLastName())
        .email(contact.getEmail())
        .phone(contact.getPhone())
        .build();
  }
}
