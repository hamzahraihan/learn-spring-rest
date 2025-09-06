package com.learn.learn_spring_rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.learn.learn_spring_rest.entity.User;
import com.learn.learn_spring_rest.model.ContactResponse;
import com.learn.learn_spring_rest.model.CreateContactRequest;
import com.learn.learn_spring_rest.model.WebResponse;
import com.learn.learn_spring_rest.service.ContactService;

@RestController
public class ContactController {

  @Autowired
  private ContactService contactService;

  @PostMapping(path = "/api/contacts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest request) {
    ContactResponse contactResponse = contactService.create(user, request);
    return WebResponse.<ContactResponse>builder().data(contactResponse).build();

  }

  @GetMapping(path = "/api/contacts/{contactId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<ContactResponse> get(User user, @PathVariable("contactId") String contactId) {
    ContactResponse contactResponse = contactService.get(user, contactId);
    return WebResponse.<ContactResponse>builder().data(contactResponse).build();
  }
}
