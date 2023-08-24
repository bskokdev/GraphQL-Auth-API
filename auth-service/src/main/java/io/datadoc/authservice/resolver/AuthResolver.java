package io.datadoc.authservice.resolver;

import io.datadoc.authservice.model.LoginCredentials;
import io.datadoc.authservice.model.LoginResponse;
import io.datadoc.authservice.service.AuthService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AuthResolver {

  private final AuthService authService;

  public AuthResolver(AuthService authService) {
    this.authService = authService;
  }

  @QueryMapping
  public LoginResponse login(@Argument LoginCredentials credentials) {
    return this.authService.login(credentials);
  }
}
