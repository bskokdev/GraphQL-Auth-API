package io.datadoc.authservice.resolver;

import io.datadoc.authservice.model.auth.LoginCredentials;
import io.datadoc.authservice.model.auth.LoginResponse;
import io.datadoc.authservice.service.AuthService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

/**
 * AuthResolver provides GraphQL query & mutations methods for authenticating users. Refer to the
 * GraphQL schema for more information.
 */
@Controller
public class AuthResolver {

  private final AuthService authService;

  public AuthResolver(AuthService authService) {
    this.authService = authService;
  }

  /**
   * User will be issued JWT tokens (access, refresh, id) based on their credentials.
   *
   * @param credentials The user's credentials - email & password.
   * @return LoginResponse containing the JWT tokens & error object if any.
   */
  @MutationMapping
  public LoginResponse login(@Argument LoginCredentials credentials) {
    return this.authService.issueJwtTokensToUser(credentials);
  }

  /**
   * Logs out a user based on their ID token.
   *
   * @param idToken The user's ID token previously issued by Keycloak.
   * @return true if the user was logged out successfully, false otherwise.
   */
  @MutationMapping
  public boolean logout(@Argument String idToken) {
    return this.authService.logoutBasedOnIdToken(idToken);
  }

  /**
   * Revokes a JWT token.
   *
   * @param token The JWT token to revoke.
   * @return true if the token was revoked successfully, false otherwise.
   */
  @MutationMapping
  public boolean revoke(@Argument String token) {
    return this.authService.revokeJwtToken(token);
  }
}
