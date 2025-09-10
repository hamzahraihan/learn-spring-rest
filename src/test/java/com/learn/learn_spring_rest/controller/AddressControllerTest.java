package com.learn.learn_spring_rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.learn_spring_rest.entity.Address;
import com.learn.learn_spring_rest.entity.Contact;
import com.learn.learn_spring_rest.entity.User;
import com.learn.learn_spring_rest.model.AddressResponse;
import com.learn.learn_spring_rest.model.CreateAddressRequest;
import com.learn.learn_spring_rest.model.UpdateAddressRequest;
import com.learn.learn_spring_rest.model.WebResponse;
import com.learn.learn_spring_rest.repository.AddressRepository;
import com.learn.learn_spring_rest.repository.ContactRepository;
import com.learn.learn_spring_rest.repository.UserRepository;
import com.learn.learn_spring_rest.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private ContactRepository contactRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    addressRepository.deleteAll();
    contactRepository.deleteAll();
    userRepository.deleteAll();

    User user = new User();
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    user.setName("Test");
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
    userRepository.save(user);

    Contact contact = new Contact();
    contact.setId("test");
    contact.setUser(user);
    contact.setFirstName("john");
    contact.setLastName("doe");
    contact.setEmail("johndoe@example.com");
    contact.setPhone("0123123");
    contactRepository.save(contact);
  }

  @Test
  void createAddressBadRequest() throws Exception {
    CreateAddressRequest request = new CreateAddressRequest();
    request.setCountry("");

    mockMvc.perform(
        post("/api/contacts/test/addresses")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isBadRequest())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
          assertNotNull(response.getErrors());
        });
  }

  @Test
  void createAddressSuccess() throws Exception {
    CreateAddressRequest request = new CreateAddressRequest();
    request.setStreet("Jalan");
    request.setCity("Jakarta");
    request.setProvince("DKI");
    request.setCountry("Indonesia");
    request.setPostalCode("123123");

    mockMvc.perform(
        post("/api/contacts/test/addresses")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

          assertNull(response.getErrors());
          assertEquals(request.getStreet(), response.getData().getStreet());
          assertEquals(request.getCity(), response.getData().getCity());
          assertEquals(request.getCountry(), response.getData().getCountry());
          assertEquals(request.getProvince(), response.getData().getProvince());
          assertEquals(request.getPostalCode(), response.getData().getPostalCode());

          assertTrue(addressRepository.existsById(response.getData().getId()));
        });
  }

  @Test
  void getAddressNotFound() throws Exception {
    mockMvc.perform(
        get("/api/contacts/test/addresses/test")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isNotFound())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
          assertNotNull(response.getErrors());
        });
  }

  @Test
  void getAddressSuccess() throws Exception {
    Contact contact = contactRepository.findById("test").orElseThrow();

    Address address = new Address();
    address.setId("test");
    address.setContact(contact);
    address.setCity("Jakarta");
    address.setProvince("DKI");
    address.setCountry("Indonesia");
    address.setPostalCode("123123");
    addressRepository.save(address);

    mockMvc.perform(
        get("/api/contacts/test/addresses/test")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isOk())
        .andDo(result -> {
          WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
          assertNull(response.getErrors());
          assertEquals(address.getId(), response.getData().getId());
          assertEquals(address.getCity(), response.getData().getCity());
          assertEquals(address.getStreet(), response.getData().getStreet());
          assertEquals(address.getProvince(), response.getData().getProvince());
          assertEquals(address.getCountry(), response.getData().getCountry());
          assertEquals(address.getPostalCode(), response.getData().getPostalCode());

          assertTrue(addressRepository.existsById(address.getId()));
        });
  }

  @Test
  void updateAddressBadRequest() throws Exception {
    UpdateAddressRequest request = new UpdateAddressRequest();
    request.setCountry("");

    mockMvc.perform(
        put("/api/contacts/test/addresses/test")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isBadRequest())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
          assertNotNull(response.getErrors());
        });
  }

  @Test
  void updateAddressSuccess() throws Exception {
    Contact contact = contactRepository.findById("test").orElseThrow();

    Address address = new Address();
    address.setId("test");
    address.setContact(contact);
    address.setCity("Old City");
    address.setProvince("Old Province");
    address.setCountry("Old Country");
    address.setPostalCode("123");
    addressRepository.save(address);

    UpdateAddressRequest request = new UpdateAddressRequest();
    request.setStreet("Jalan");
    request.setCity("Jakarta");
    request.setProvince("DKI");
    request.setCountry("Indonesia");
    request.setPostalCode("123123");

    mockMvc.perform(
        put("/api/contacts/test/addresses/test")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

          assertNull(response.getErrors());
          assertEquals(request.getStreet(), response.getData().getStreet());
          assertEquals(request.getCity(), response.getData().getCity());
          assertEquals(request.getCountry(), response.getData().getCountry());
          assertEquals(request.getProvince(), response.getData().getProvince());
          assertEquals(request.getPostalCode(), response.getData().getPostalCode());

          assertTrue(addressRepository.existsById(response.getData().getId()));
        });
  }

  @Test
  void deleteAddressNotFound() throws Exception {
    mockMvc.perform(
        delete("/api/contacts/test/addresses/test")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isNotFound())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
          assertNotNull(response.getErrors());
        });
  }

  @Test
  void deleteAddressSuccess() throws Exception {
    Contact contact = contactRepository.findById("test").orElseThrow();

    Address address = new Address();
    address.setId("test");
    address.setContact(contact);
    address.setCity("Jakarta");
    address.setProvince("DKI");
    address.setCountry("Indonesia");
    address.setPostalCode("123123");
    addressRepository.save(address);

    mockMvc.perform(
        delete("/api/contacts/test/addresses/test")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isOk())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
          assertNull(response.getErrors());
          assertEquals("OK", response.getData());

          assertFalse(addressRepository.existsById("test"));
        });
  }

  @Test
  void listAddressNotFound() throws Exception {
    mockMvc.perform(
        get("/api/contacts/notexist/addresses")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isNotFound())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
          assertNotNull(response.getErrors());
        });
  }

  @Test
  void listAddressSuccess() throws Exception {
    Contact contact = contactRepository.findById("test").orElseThrow();

    for (int i = 0; i < 100; i++) {
      Address address = new Address();
      address.setId("test - " + i);
      address.setContact(contact);
      address.setCity("Jakarta");
      address.setProvince("DKI");
      address.setCountry("Indonesia");
      address.setPostalCode("123123");
      addressRepository.save(address);
    }

    mockMvc.perform(
        get("/api/contacts/test/addresses")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isOk())
        .andDo(result -> {
          WebResponse<List<AddressResponse>> response = objectMapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
          assertNull(response.getErrors());
          assertEquals(100, response.getData().size());
        });
  }
}
