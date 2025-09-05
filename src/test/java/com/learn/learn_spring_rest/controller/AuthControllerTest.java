package com.learn.learn_spring_rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.learn_spring_rest.entity.User;
import com.learn.learn_spring_rest.model.LoginUserRequest;
import com.learn.learn_spring_rest.model.TokenResponse;
import com.learn.learn_spring_rest.model.WebResponse;
import com.learn.learn_spring_rest.repository.UserRepository;
import com.learn.learn_spring_rest.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
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
  void loginFailedUserNotFound() throws Exception {
    LoginUserRequest request = new LoginUserRequest();
    request.setUsername("test");
    request.setPassword("test");

    mockMvc.perform(
        post("/api/auth/login")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpectAll(
            status().isUnauthorized())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
          assertNotNull(response.getErrors());
        });

  }

  @Test
  void loginFailedWrongPassword() throws Exception {
    User user = new User();
    user.setName("Test");
    user.setUsername("Test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    userRepository.save(user);

    LoginUserRequest request = new LoginUserRequest();
    request.setUsername("test");
    request.setPassword("wrongpass");

    mockMvc.perform(
        post("/api/auth/login")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpectAll(
            status().isUnauthorized())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
          assertNotNull(response.getErrors());
        });
  }

  @Test
  void loginSuccess() throws Exception {
    User user = new User();
    user.setName("Test");
    user.setUsername("Test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    userRepository.save(user);

    LoginUserRequest request = new LoginUserRequest();
    request.setUsername("test");
    request.setPassword("test");

    mockMvc.perform(
        post("/api/auth/login")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
          assertNull(response.getErrors());

          assertNotNull(response.getData().getToken());
          assertNotNull(response.getData().getExpiredAt());

          User userDb = userRepository.findById("test").orElse(null);
          assertNotNull(userDb);
          assertEquals(userDb.getToken(), response.getData().getToken());
          assertEquals(userDb.getTokenExpiredAt(), response.getData().getExpiredAt());
        });
  }

  @Test
  void logoutFailed() throws Exception {
    mockMvc.perform(
        delete("/api/auth/logout").accept(MediaType.APPLICATION_JSON)).andExpectAll(
            status().isUnauthorized())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
          assertNotNull(response.getErrors());
        });
  }

  @Test
  void logoutSuccess() throws Exception {
    User user = new User();
    user.setName("test");
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    user.setToken("token");
    user.setTokenExpiredAt(System.currentTimeMillis() + 1000000L);
    userRepository.save(user);

    mockMvc.perform(
        delete("/api/auth/logout")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "token"))
        .andExpectAll(
            status().isOk())
        .andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
          assertNull(response.getErrors());
          assertEquals("OK", response.getData());

          User userDB = userRepository.findById("test").orElse(null);
          assertNotNull(userDB);
          assertNull(userDB.getToken());
          assertNull(userDB.getTokenExpiredAt());
        });
  }
}
