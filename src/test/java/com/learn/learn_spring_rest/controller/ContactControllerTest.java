package com.learn.learn_spring_rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
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
import com.learn.learn_spring_rest.model.ContactResponse;
import com.learn.learn_spring_rest.model.CreateContactRequest;
import com.learn.learn_spring_rest.model.UpdateContactRequest;
import com.learn.learn_spring_rest.model.WebResponse;
import com.learn.learn_spring_rest.repository.ContactRepository;
import com.learn.learn_spring_rest.repository.UserRepository;
import com.learn.learn_spring_rest.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
public class ContactControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ContactRepository contactRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    contactRepository.deleteAll();
    userRepository.deleteAll();

    User user = new User();
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    user.setName("Test");
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
    userRepository.save(user);
  }

  @Test
  void createContactBadRequest() throws Exception {
    CreateContactRequest request = new CreateContactRequest();
    request.setFirstName("");
    request.setEmail("wrongemail");

    mockMvc.perform(
        post("/api/contacts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isBadRequest())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {

              });

          assertNotNull(response.getErrors());

        });
  }

  @Test
  void createContactSuccess() throws Exception {
    CreateContactRequest request = new CreateContactRequest();
    request.setFirstName("John");
    request.setLastName("Doe");
    request.setEmail("john@gmail.com");
    request.setPhone("0213123");

    mockMvc.perform(
        post("/api/contacts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });

          assertNull(response.getErrors());
          assertEquals("John", response.getData().getFirstName());
          assertEquals("Doe", response.getData().getLastName());
          assertEquals("john@gmail.com", response.getData().getEmail());
          assertEquals("0213123", response.getData().getPhone());

          assertTrue(contactRepository.existsById(response.getData().getId()));
        });
  }

  @Test
  void getContactNotFound() throws Exception {

    mockMvc.perform(
        get("/api/contacts/123123")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isNotFound())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {

              });

          assertNotNull(response.getErrors());

        });
  }

  @Test
  void getContactSuccess() throws Exception {
    User user = userRepository.findById("test").orElseThrow();

    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("john");
    contact.setLastName("doe");
    contact.setEmail("johndoe@example.com");
    contact.setPhone("0123123");

    contactRepository.save(contact);

    mockMvc.perform(
        get("/api/contacts/" + contact.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });

          assertNull(response.getErrors());
          assertEquals(contact.getId(), response.getData().getId());
          assertEquals(contact.getFirstName(), response.getData().getFirstName());
          assertEquals(contact.getLastName(), response.getData().getLastName());
          assertEquals(contact.getEmail(), response.getData().getEmail());
          assertEquals(contact.getPhone(), response.getData().getPhone());

        });
  }

  @Test
  void updateContactBadRequest() throws Exception {
    UpdateContactRequest request = new UpdateContactRequest();
    request.setFirstName("");
    request.setEmail("wrongemail");

    mockMvc.perform(
        put("/api/contacts/1234")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isBadRequest())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {

              });

          assertNotNull(response.getErrors());

        });
  }

  @Test
  void updateContactSuccess() throws Exception {
    User user = userRepository.findById("test").orElseThrow();

    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("john");
    contact.setLastName("doe");
    contact.setEmail("johndoe@example.com");
    contact.setPhone("0123123");

    contactRepository.save(contact);

    UpdateContactRequest request = new UpdateContactRequest();
    request.setFirstName("Jane");
    request.setLastName("William");
    request.setEmail("jane@gmail.com");
    request.setPhone("123123");

    mockMvc.perform(
        put("/api/contacts/" + contact.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });

          assertNull(response.getErrors());
          assertEquals(request.getFirstName(), response.getData().getFirstName());
          assertEquals(request.getLastName(), response.getData().getLastName());
          assertEquals(request.getEmail(), response.getData().getEmail());
          assertEquals(request.getPhone(), response.getData().getPhone());

          assertTrue(contactRepository.existsById(response.getData().getId()));
        });
  }

  @Test
  void deleteContactNotFound() throws Exception {

    mockMvc.perform(
        delete("/api/contacts/123123")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isNotFound())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {

              });

          assertNotNull(response.getErrors());

        });
  }

  @Test
  void deleteContactSuccess() throws Exception {
    User user = userRepository.findById("test").orElseThrow();

    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("john");
    contact.setLastName("doe");
    contact.setEmail("johndoe@example.com");
    contact.setPhone("0123123");

    contactRepository.save(contact);

    mockMvc.perform(
        delete("/api/contacts/" + contact.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });

          assertNull(response.getErrors());
          assertEquals("OK", response.getData());
        });
  }

  @Test
  void searchContactNotFound() throws Exception {

    mockMvc.perform(
        get("/api/contacts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<List<ContactResponse>> response = objectMapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
          assertNull(response.getErrors());
          assertEquals(0, response.getData().size());
          assertEquals(0, response.getPaging().getTotalPage());
          assertEquals(0, response.getPaging().getCurrentPage());
          assertEquals(10, response.getPaging().getSize());
        }

        );

  }

  @Test
  void searchContactSuccess() throws Exception {

    User user = userRepository.findById("test").orElseThrow();

    for (int i = 0; i < 100; i++) {
      Contact contact = new Contact();
      contact.setId(UUID.randomUUID().toString());
      contact.setUser(user);
      contact.setFirstName("john " + i);
      contact.setLastName("doe");
      contact.setEmail("johndoe@example.com");
      contact.setPhone("0123123");

      contactRepository.save(contact);
    }

    // performing test for contact search by first name
    mockMvc.perform(
        get("/api/contacts")
            .queryParam("name", "john")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<List<ContactResponse>> response = objectMapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
          assertNull(response.getErrors());
          assertEquals(10, response.getData().size());
          assertEquals(10, response.getPaging().getTotalPage());
          assertEquals(0, response.getPaging().getCurrentPage());
          assertEquals(10, response.getPaging().getSize());
        });

    // performing test for contact search by last name
    mockMvc.perform(
        get("/api/contacts")
            .queryParam("name", "doe")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<List<ContactResponse>> response = objectMapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
          assertNull(response.getErrors());
          assertEquals(10, response.getData().size());
          assertEquals(10, response.getPaging().getTotalPage());
          assertEquals(0, response.getPaging().getCurrentPage());
          assertEquals(10, response.getPaging().getSize());
        });

    // performing test for contact search by email
    mockMvc.perform(
        get("/api/contacts")
            .queryParam("email", "example.com")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<List<ContactResponse>> response = objectMapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
          assertNull(response.getErrors());
          assertEquals(10, response.getData().size());
          assertEquals(10, response.getPaging().getTotalPage());
          assertEquals(0, response.getPaging().getCurrentPage());
          assertEquals(10, response.getPaging().getSize());
        });

    // performing test for contact search by phone
    mockMvc.perform(
        get("/api/contacts")
            .queryParam("phone", "123")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<List<ContactResponse>> response = objectMapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
          assertNull(response.getErrors());
          assertEquals(10, response.getData().size());
          assertEquals(10, response.getPaging().getTotalPage());
          assertEquals(0, response.getPaging().getCurrentPage());
          assertEquals(10, response.getPaging().getSize());
        });

    // performing test for contact search if page is above limit
    mockMvc.perform(
        get("/api/contacts")
            .queryParam("phone", "123")
            .queryParam("page", "1000")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<List<ContactResponse>> response = objectMapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
          assertNull(response.getErrors());
          assertEquals(0, response.getData().size());
          assertEquals(10, response.getPaging().getTotalPage());
          assertEquals(1000, response.getPaging().getCurrentPage());
          assertEquals(10, response.getPaging().getSize());
        });

  }
}
