package com.learn.learn_spring_rest.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.learn.learn_spring_rest.entity.User;
import com.learn.learn_spring_rest.model.LoginUserRequest;
import com.learn.learn_spring_rest.model.TokenResponse;
import com.learn.learn_spring_rest.repository.UserRepository;
import com.learn.learn_spring_rest.security.BCrypt;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ValidationService validationService;

  @Transactional
  public TokenResponse login(LoginUserRequest request) {
    validationService.validate(request);

    User user = userRepository.findById(request.getUsername())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password wrong!"));

    if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
      user.setToken(UUID.randomUUID().toString());
      user.setTokenExpiredAt(next30Days());
      userRepository.save(user);
      return TokenResponse.builder().token(user.getToken()).expiredAt(user.getTokenExpiredAt()).build();
    } else {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password wrong!");
    }
  }

  private Long next30Days() {
    return System.currentTimeMillis() + (1000 * 16 * 24 * 30);
  }

}
