package com.learn.learn_spring_rest.controller;

import org.springframework.web.bind.annotation.RestController;

import com.learn.learn_spring_rest.entity.User;
import com.learn.learn_spring_rest.model.AddressResponse;
import com.learn.learn_spring_rest.model.CreateAddressRequest;
import com.learn.learn_spring_rest.model.WebResponse;
import com.learn.learn_spring_rest.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class AddressController {

  @Autowired
  private AddressService addressService;

  @PostMapping(path = "/api/contacts/{contactId}/addresses")
  public WebResponse<AddressResponse> create(User user, @RequestBody CreateAddressRequest request,
      @PathVariable("contactId") String contactId) {

    request.setContactId(contactId);
    AddressResponse addressResponse = addressService.create(user, request);

    return WebResponse.<AddressResponse>builder().data(addressResponse).build();
  }

}
