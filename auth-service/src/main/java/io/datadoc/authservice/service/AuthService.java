package io.datadoc.authservice.service;

import io.datadoc.authservice.model.auth.JwtPayload;
import io.datadoc.authservice.model.auth.JwtResponse;
import io.datadoc.authservice.model.auth.LoginCredentials;
import io.datadoc.authservice.model.auth.LoginError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * AuthService provides methods for authenticating users & JWT token management. It uses
 * KeycloakService to interact with Keycloak. This service is the high-level abstraction for
 * authentication = catches all the exceptions.
 * TODO(bskokdev) - add integration tests for this service.
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
   *
   * @param credentials The user's credentials - email & password.
   * @return LoginResponse containing the JWT tokens or an error object.
   */
  public JwtResponse issueJwtTokensToUser(LoginCredentials credentials) {
    try {
      ResponseEntity<JwtPayload> response = keycloakService.fetchTokensForUser(credentials);
      LOGGER.info("Successfully issued a new JWT token - {}", response.getStatusCode());
      return new JwtResponse(response.getBody(), null);
    } catch (HttpClientErrorException e) {
      // Catch the client errors - unauthorized, bad request etc.
      LOGGER.error("ERROR issuing JWT token: {}", e.getStatusCode());
      return new JwtResponse(
          null,
          new LoginError(
              String.format("ERROR issuing the JWT token - %s", e.getStatusCode()),
              e.getStatusCode().value()
          )
      );
    } catch (Exception e) {
      // Catch all other exceptions - server, network etc.
      // We don't want to expose the exception message to the client.
      LOGGER.error("ERROR issuing JWT token: {}", e.getMessage());
      return new JwtResponse(
          null,
          new LoginError(
              "Internal server error issuing the JWT token",
              HttpStatus.INTERNAL_SERVER_ERROR.value()
          )
      );
    }
  }

  /**
   * Refreshes the user's JWT tokens based on the refresh token.
   *
   * @param refreshToken The refresh token issued to the user by Keycloak.
   * @return LoginResponse containing the JWT tokens & possibly an error object.
   */
  public JwtResponse refreshUserTokens(String refreshToken) {
    try {
      ResponseEntity<JwtPayload> response = keycloakService.refreshTokens(refreshToken);
      LOGGER.info("Successfully refreshed the JWT token - {}", response.getStatusCode());
      return new JwtResponse(response.getBody(), null);
    } catch (HttpClientErrorException e) {
      // Catch the client errors - unauthorized, bad request etc.
      LOGGER.error("ERROR refreshing the JWT token - {}", e.getStatusCode());
      return new JwtResponse(
          null,
          new LoginError(
              String.format("ERROR refreshing the JWT token - %s", e.getStatusCode()),
              e.getStatusCode().value()
          )
      );
    } catch (Exception e) {
      // Catch all other exceptions - server, network etc.
      // We don't want to expose the exception message to the client.
      LOGGER.error("ERROR issuing JWT token - {}", e.getMessage());
      return new JwtResponse(
          null,
          new LoginError(
              "Internal server error issuing the JWT token",
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
      ResponseEntity<String> response = keycloakService.logoutKeycloakUser(idToken);
      LOGGER.info("Successfully logged out user - {}", response.getStatusCode());
      return true;
    } catch (Exception e) {
      LOGGER.error("ERROR logging out a user");
      return false;
    }
  }

  /**
   * Revokes the given JWT token.
   *
   * @param token The JWT token to revoke.
   */
  public boolean revokeJwtToken(String token) {
    try {
      ResponseEntity<String> res = keycloakService.revokeKeycloakToken(token);
      LOGGER.info("Successfully revoked the JWT token - {}", res.getStatusCode());
      return true;
    } catch (Exception e) {
      LOGGER.error("ERROR revoking the JWT token");
      return false;
    }
  }
}
