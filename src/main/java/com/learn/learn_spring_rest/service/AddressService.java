package com.learn.learn_spring_rest.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.learn.learn_spring_rest.entity.Address;
import com.learn.learn_spring_rest.entity.Contact;
import com.learn.learn_spring_rest.entity.User;
import com.learn.learn_spring_rest.model.AddressResponse;
import com.learn.learn_spring_rest.model.CreateAddressRequest;
import com.learn.learn_spring_rest.repository.AddressRepository;
import com.learn.learn_spring_rest.repository.ContactRepository;

@Service
public class AddressService {

  @Autowired
  private ContactRepository contactRepository;

  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private ValidationService validationService;

  @Transactional
  public AddressResponse create(User user, CreateAddressRequest request) {
    validationService.validate(request);

    Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found!"));

    Address address = new Address();
    address.setId(UUID.randomUUID().toString());
    address.setContact(contact);
    address.setStreet(request.getStreet());
    address.setCity(request.getCity());
    address.setProvince(request.getProvince());
    address.setCountry(request.getCountry());
    address.setPostalCode(request.getPostalCode());

    addressRepository.save(address);

    return toAddressResponse(address);
  }

  private AddressResponse toAddressResponse(Address address) {
    return AddressResponse.builder()
        .id(address.getId())
        .street(address.getStreet())
        .city(address.getCity())
        .province(address.getProvince())
        .country(address.getCountry())
        .postalCode(address.getPostalCode())
        .build();
  }

}
