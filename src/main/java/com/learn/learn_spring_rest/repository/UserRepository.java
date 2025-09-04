package com.learn.learn_spring_rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learn.learn_spring_rest.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findFirstByToken(String token);
}
