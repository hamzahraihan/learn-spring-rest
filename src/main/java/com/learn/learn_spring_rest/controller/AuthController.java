package com.learn.learn_spring_rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.learn.learn_spring_rest.entity.User;
import com.learn.learn_spring_rest.model.LoginUserRequest;
import com.learn.learn_spring_rest.model.TokenResponse;
import com.learn.learn_spring_rest.model.WebResponse;
import com.learn.learn_spring_rest.service.AuthService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class AuthController {
  @Autowired
  private AuthService authService;

  @PostMapping(path = "/api/auth/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request) {
    TokenResponse tokenResponse = authService.login(request);

    return WebResponse.<TokenResponse>builder().data(tokenResponse).build();
  }

  @DeleteMapping(path = "/api/auth/logout", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> logout(User user) {
    authService.logout(user);
    return WebResponse.<String>builder().data("OK").build();
  }

}
