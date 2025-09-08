package com.learn.learn_spring_rest.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.learn_spring_rest.entity.Contact;
import com.learn.learn_spring_rest.entity.User;
import com.learn.learn_spring_rest.model.CreateAddressRequest;
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
    contact.setId(UUID.randomUUID().toString());
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

}
