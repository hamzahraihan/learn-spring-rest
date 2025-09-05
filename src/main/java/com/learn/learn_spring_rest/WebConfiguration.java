package com.learn.learn_spring_rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.learn.learn_spring_rest.resolver.UserArgumentResolver;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  @Autowired
  private UserArgumentResolver userArgumentResolver;

  @Override
  public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
    WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    resolvers.add(userArgumentResolver);
  }
}
