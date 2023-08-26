package io.datadoc.authservice.resolver;

import io.datadoc.authservice.model.auth.JwtPayload;
import io.datadoc.authservice.model.auth.LoginCredentials;
import io.datadoc.authservice.model.auth.UserMetadata;
import io.datadoc.authservice.service.KeycloakService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * AuthResolver provides GraphQL query & mutations methods for authenticating users. Refer to the
 * GraphQL schema for more information.
 */
@Controller
public class AuthResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthResolver.class);
  private final KeycloakService keycloakService;

  public AuthResolver(KeycloakService keycloakService) {
    this.keycloakService = keycloakService;
  }

  /**
   * User will be issued with JWT tokens (access, refresh, id) based on their credentials.
   *
   * @param credentials User's email & password object.
   * @return JwtPayload containing the JWT tokens.
   * @see JwtPayload
   */
  @MutationMapping
  public JwtPayload login(@Argument LoginCredentials credentials) throws HttpStatusCodeException {
    ResponseEntity<JwtPayload> response = keycloakService.fetchTokensForUser(credentials);
    LOGGER.info("Successfully issued JWT tokens - {}", response.getStatusCode());
    return response.getBody();
  }

  /**
   * Returns the user's information based on their access token.
   *
   * @param accessToken The user's access token.
   * @return UserMetadata object containing the user's information.
   */
  @QueryMapping
  public UserMetadata me(@Argument String accessToken) throws HttpStatusCodeException {
    ResponseEntity<UserMetadata> response = keycloakService.fetchUser(accessToken);
    LOGGER.info("Successfully fetched user info - {}", response.getStatusCode());
    return response.getBody();
  }

  /**
   * Logs out a user based on their ID token.
   *
   * @param idToken The user's ID token previously issued by Keycloak.
   * @return true if the user was logged out successfully, false otherwise.
   */
  @MutationMapping
  public boolean logout(@Argument String idToken) throws HttpStatusCodeException {
    ResponseEntity<String> response = keycloakService.logoutKeycloakUser(idToken);
    LOGGER.info("Successfully logged out user - {}", response.getStatusCode());
    return true;
  }

  /**
   * Refreshes a user's JWT tokens based on a refresh token - issues new JwtPayload to the user.
   *
   * @param refreshToken The user's refresh token.
   * @return JwtPayload containing the JWT tokens
   * @see JwtPayload
   */
  @MutationMapping
  public JwtPayload refresh(@Argument String refreshToken) throws HttpStatusCodeException {
    ResponseEntity<JwtPayload> response = keycloakService.refreshTokens(refreshToken);
    LOGGER.info("Successfully refreshed the JWT token - {}", response.getStatusCode());
    return response.getBody();
  }

  /**
   * Revokes a JWT token - invalidates the token.
   *
   * @param token The JWT token to revoke.
   * @return true if the token was revoked successfully, false otherwise.
   */
  @MutationMapping
  public boolean revoke(@Argument String token) throws HttpStatusCodeException {
    ResponseEntity<String> res = keycloakService.revokeKeycloakToken(token);
    LOGGER.info("Successfully revoked the JWT token - {}", res.getStatusCode());
    return true;
  }
}
