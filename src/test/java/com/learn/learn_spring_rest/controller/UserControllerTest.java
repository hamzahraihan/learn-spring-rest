package com.learn.learn_spring_rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.learn_spring_rest.entity.User;
import com.learn.learn_spring_rest.model.RegisterUserRequest;
import com.learn.learn_spring_rest.model.UpdateUserRequest;
import com.learn.learn_spring_rest.model.UserResponse;
import com.learn.learn_spring_rest.model.WebResponse;
import com.learn.learn_spring_rest.repository.UserRepository;
import com.learn.learn_spring_rest.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void testRegisterSuccess() throws Exception {
    RegisterUserRequest request = new RegisterUserRequest();
    request.setUsername("test");
    request.setPassword("rahasia");
    request.setName("test");

    mockMvc.perform(
        post("/api/users")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpectAll(status().isOk())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {
              });
          assertEquals("OK", response.getData());
        });
  }

  @Test
  void testRegisterBadRequest() throws Exception {
    RegisterUserRequest request = new RegisterUserRequest();
    request.setUsername("");
    request.setPassword("");
    request.setName("");

    mockMvc.perform(
        post("/api/users")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpectAll(status().isBadRequest())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {
              });
          assertNotNull(response.getErrors());
        });
  }

  @Test
  void testRegisterDuplicate() throws Exception {
    // initialize a new user
    User user = new User();
    user.setName("test");
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
    userRepository.save(user);

    // duplicate a user
    RegisterUserRequest request = new RegisterUserRequest();
    request.setUsername("test");
    request.setPassword("rahasia");
    request.setName("test");

    mockMvc.perform(
        post("/api/users")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpectAll(status().isBadRequest())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {
              });
          assertNotNull(response.getErrors());
        });
  }

  @Test
  void getUserUnauthorized() throws Exception {
    mockMvc.perform(
        get("/api/users/current")
            .accept(MediaType.APPLICATION_JSON).header("X-API-TOKEN", "notfound"))
        .andExpectAll(
            status().isUnauthorized())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {
              });
          assertNotNull(response.getErrors());
        });
  }

  @Test
  void getUserSuccess() throws Exception {
    User user = new User();
    user.setName("test");
    user.setUsername("test");
    user.setToken("test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);

    userRepository.save(user);

    mockMvc.perform(
        get("/api/users/current")
            .accept(MediaType.APPLICATION_JSON).header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<UserResponse>>() {
              });
          assertNull(response.getErrors());
          assertEquals("test", response.getData().getUsername());
          assertEquals("test", response.getData().getName());
        });
  }

  @Test
  void getUserTokenExpired() throws Exception {
    User user = new User();
    user.setName("test");
    user.setUsername("test");
    user.setToken("test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    user.setTokenExpiredAt(System.currentTimeMillis() + -1000000000L);

    userRepository.save(user);

    mockMvc.perform(
        get("/api/users/current")
            .accept(MediaType.APPLICATION_JSON).header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isUnauthorized())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {
              });
          assertNotNull(response.getErrors());
        });
  }

  @Test
  void updateUserUnauthorized() throws Exception {
    UpdateUserRequest request = new UpdateUserRequest();

    mockMvc.perform(
        patch("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpectAll(
            status().isUnauthorized())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {
              });
          assertNotNull(response.getErrors());
        });
  }

  @Test
  void updateUserSuccess() throws Exception {
    User user = new User();
    user.setName("test");
    user.setUsername("test");
    user.setToken("test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);

    userRepository.save(user);

    UpdateUserRequest request = new UpdateUserRequest();
    request.setName("John");
    request.setPassword("test123");

    mockMvc.perform(
        patch("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test"))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<UserResponse>>() {
              });
          assertNull(response.getErrors());
          assertEquals("John", response.getData().getName());
          assertEquals("test", response.getData().getUsername());

          // check if user data has been updated inside database
          User userDB = userRepository.findById("test").orElse(null);
          assertNotNull(userDB);
          assertTrue(BCrypt.checkpw("test123", userDB.getPassword()));
        });
  }

}
