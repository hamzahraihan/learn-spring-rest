package com.learn.learn_spring_rest.controller;

import org.springframework.web.bind.annotation.RestController;

import com.learn.learn_spring_rest.entity.User;
import com.learn.learn_spring_rest.model.AddressResponse;
import com.learn.learn_spring_rest.model.CreateAddressRequest;
import com.learn.learn_spring_rest.model.WebResponse;
import com.learn.learn_spring_rest.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class AddressController {

  @Autowired
  private AddressService addressService;

  @PostMapping(path = "/api/contacts/{contactId}/addresses", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<AddressResponse> create(User user, @RequestBody CreateAddressRequest request,
      @PathVariable("contactId") String contactId) {

    request.setContactId(contactId);
    AddressResponse addressResponse = addressService.create(user, request);

    return WebResponse.<AddressResponse>builder().data(addressResponse).build();
  }

  @GetMapping(path = "/api/contacts/{contactId}/addresses/{addressId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<AddressResponse> get(User user, @PathVariable("contactId") String contactId,
      @PathVariable("addressId") String addressId) {
    AddressResponse response = addressService.get(user, contactId, addressId);

    return WebResponse.<AddressResponse>builder().data(response).build();
  }

}
