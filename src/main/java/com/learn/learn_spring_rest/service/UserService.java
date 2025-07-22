package com.learn.learn_spring_rest.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.learn.learn_spring_rest.entity.User;
import com.learn.learn_spring_rest.model.RegisterUserRequest;
import com.learn.learn_spring_rest.repository.UserRepository;
import com.learn.learn_spring_rest.security.BCrypt;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Validator validator;

  @Transactional
  public void register(RegisterUserRequest request) {
    Set<ConstraintViolation<RegisterUserRequest>> constraintViolations = validator.validate(request);
    // check if constraint violation is not equal to 0
    if (constraintViolations.size() != 0) {
      throw new ConstraintViolationException(constraintViolations);
    }

    // check if username is exist by reading the username in database
    if (userRepository.existsById(request.getUsername())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
    }

    // create a new user if there is no any existing username
    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
    user.setName(request.getName());

    userRepository.save(user);
  }
}
