package io.datadoc.authservice.resolver;

import io.datadoc.authservice.model.LoginCredentials;
import io.datadoc.authservice.model.LoginResponse;
import io.datadoc.authservice.service.AuthService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * AuthResolver provides GraphQL query & mutations methods for authenticating users.
 */
@Controller
public class AuthResolver {

  private final AuthService authService;

  public AuthResolver(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Issue client with a JWT token.
   *
   * @param credentials The user's credentials - email & password.
   * @return LoginResponse containing the JWT tokens or an error object.
   */
  @QueryMapping
  public LoginResponse login(@Argument LoginCredentials credentials) {
    return this.authService.issueJwtTokensToUser(credentials);
  }
}
