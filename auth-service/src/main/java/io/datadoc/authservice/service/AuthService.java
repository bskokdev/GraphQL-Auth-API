package io.datadoc.authservice.service;

import io.datadoc.authservice.model.auth.JwtPayload;
import io.datadoc.authservice.model.auth.LoginCredentials;
import io.datadoc.authservice.model.auth.LoginError;
import io.datadoc.authservice.model.auth.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * AuthService provides methods for authenticating users & JWT token management. It uses
 * KeycloakService to interact with Keycloak. This service is the high-level abstraction for
 * authentication = catches all the exceptions.
 */
@Service
public class AuthService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
  private final KeycloakService keycloakService;

  public AuthService(KeycloakService keycloakService) {
    this.keycloakService = keycloakService;
  }

  /**
   * Issue a JWT tokens to the user based on their credentials.
   * TODO(bskokdev) - Integration test this.
   *
   * @param credentials The user's credentials - email & password.
   * @return LoginResponse containing the JWT tokens or an error object.
   */
  public LoginResponse issueJwtTokensToUser(LoginCredentials credentials) {
    try {
      JwtPayload jwtPayload = keycloakService.fetchTokensForUser(credentials).getBody();
      LOGGER.info("Issued a new JWT token");
      return new LoginResponse(jwtPayload, null);
    } catch (HttpClientErrorException e) {
      // Here we catch the client errors - 400, 401, 403 etc.
      LOGGER.error("Error issuing JWT token: {}", e.getStatusCode());
      return new LoginResponse(
          null,
          new LoginError(
              String.format("Error issuing the JWT token - %s", e.getStatusCode()),
              e.getStatusCode().value()
          )
      );
    } catch (Exception e) {
      // Here we catch the server errors & other unexpected exceptions - 500 etc.
      LOGGER.error("Error issuing JWT token: {}", e.getMessage());
      return new LoginResponse(
          null,
          new LoginError(
              String.format("Error issuing the JWT token - %s", e.getMessage()),
              HttpStatus.INTERNAL_SERVER_ERROR.value()
          )
      );
    }
  }

  /**
   * Logs out the user with the given ID token.
   *
   * @param idToken ID token previously issued to the user by Keycloak.
   * @return True if the logout was successful, false otherwise.
   */
  public boolean logoutBasedOnIdToken(String idToken) {
    try {
      keycloakService.logoutKeycloakUser(idToken);
      LOGGER.info("Successfully logged out user");
      return true;
    } catch (Exception e) {
      LOGGER.error("Error logging out a user: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Revokes the given JWT token.
   * TODO(bskokdev) - Integrate test this.
   *
   * @param token The JWT token to revoke.
   */
  public boolean revokeJwtToken(String token) {
    try {
      keycloakService.revokeKeycloakToken(token);
      LOGGER.info("Revoked the JWT token");
      return true;
    } catch (HttpClientErrorException e) {
      LOGGER.error("Error revoking the JWT token: {}", e.getStatusCode());
      return false;
    } catch (Exception e) {
      LOGGER.error("Error revoking the JWT token: {}", e.getMessage());
      return false;
    }
  }
}
