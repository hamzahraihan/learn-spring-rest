package com.learn.learn_spring_rest.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import com.learn.learn_spring_rest.entity.User;
import com.learn.learn_spring_rest.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

  @Autowired
  private UserRepository userRepository;

  @Override
  public boolean supportsParameter(@NonNull MethodParameter parameter) {
    return User.class.equals(parameter.getParameterType());
  }

  @Override
  @Nullable
  public Object resolveArgument(@NonNull MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
      @NonNull NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
    HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest();
    String token = servletRequest.getHeader("X-API-TOKEN");

    if (token == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }

    User user = userRepository.findFirstByToken(token).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"));

    if (user.getTokenExpiredAt() < System.currentTimeMillis()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Expired");
    }

    return user;
  }

}
