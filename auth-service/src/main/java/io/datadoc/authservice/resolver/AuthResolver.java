package io.datadoc.authservice.resolver;

import io.datadoc.authservice.model.auth.JwtResponse;
import io.datadoc.authservice.model.auth.LoginCredentials;
import io.datadoc.authservice.model.auth.UserMetadataResponse;
import io.datadoc.authservice.service.AuthService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
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
   * User will be issued with JWT tokens (access, refresh, id) based on their credentials.
   *
   * @param credentials User's email & password object.
   * @return JwtResponse containing the JWT tokens & possibly non-null error object.
   * @see JwtResponse
   */
  @MutationMapping
  public JwtResponse login(@Argument LoginCredentials credentials) {
    return this.authService.issueJwtTokensToUser(credentials);
  }

  /**
   * Returns the user's information based on their access token.
   *
   * @param accessToken The user's access token.
   * @return UserInfo object containing the user's information.
   */
  @QueryMapping
  public UserMetadataResponse me(@Argument String accessToken) {
    return this.authService.getUserMetadata(accessToken);
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
   * Refreshes a user's JWT tokens based on a refresh token - issues new JwtPayload to the user.
   *
   * @param refreshToken The user's refresh token.
   * @return JwtResponse containing the JWT tokens & possibly non-null error object.
   * @see JwtResponse
   */
  @MutationMapping
  public JwtResponse refresh(@Argument String refreshToken) {
    return this.authService.refreshUserTokens(refreshToken);
  }

  /**
   * Revokes a JWT token - invalidates the token.
   *
   * @param token The JWT token to revoke.
   * @return true if the token was revoked successfully, false otherwise.
   */
  @MutationMapping
  public boolean revoke(@Argument String token) {
    return this.authService.revokeJwtToken(token);
  }
}
