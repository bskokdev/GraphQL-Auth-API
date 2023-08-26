package io.datadoc.authservice.service;

import io.datadoc.authservice.model.auth.JwtPayload;
import io.datadoc.authservice.model.auth.JwtResponse;
import io.datadoc.authservice.model.auth.LoginCredentials;
import io.datadoc.authservice.model.auth.ResponseError;
import io.datadoc.authservice.model.auth.UserMetadata;
import io.datadoc.authservice.model.auth.UserMetadataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * AuthService provides methods JWT token management. It uses KeycloakService to interact with the
 * Keycloak instance. This service is the high-level abstraction for authentication.
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
   * Issue JWT tokens to the user based on their credentials.
   *
   * @param credentials user's email & password object.
   * @return LoginResponse containing the JWT tokens & possibly non-null error object.
   */
  public JwtResponse issueJwtTokensToUser(LoginCredentials credentials) {
    try {
      ResponseEntity<JwtPayload> response = keycloakService.fetchTokensForUser(credentials);
      LOGGER.info("Successfully issued a new JWT token - {}", response.getStatusCode());
      return new JwtResponse(response.getBody(), null);
    } catch (HttpClientErrorException e) {
      // Catch the client errors - unauthorized, bad request etc.
      LOGGER.error("ERROR issuing JWT token - {}", e.getStatusCode());
      return new JwtResponse(
          null,
          new ResponseError(
              String.format("ERROR issuing the JWT token - %s", e.getStatusCode()),
              e.getStatusCode().value()
          )
      );
    } catch (Exception e) {
      // Catch all other exceptions - server, network etc.
      // We don't want to expose the exception message to the client.
      LOGGER.error("ERROR issuing JWT token - {}", e.getMessage());
      return new JwtResponse(
          null,
          new ResponseError(
              "Internal server error issuing the JWT token",
              HttpStatus.INTERNAL_SERVER_ERROR.value()
          )
      );
    }
  }

  /**
   * Returns the user's information based on their access token.
   *
   * @param accessToken The user's access token.
   * @return UserInfo object containing the user's information.
   */
  public UserMetadataResponse getUserMetadata(String accessToken) {
    try {
      ResponseEntity<UserMetadata> response = keycloakService.fetchUser(accessToken);
      LOGGER.info("Successfully fetched user info - {}", response.getStatusCode());
      return new UserMetadataResponse(response.getBody(), null);
    } catch (HttpClientErrorException e) {
      // Catch the client errors - unauthorized, bad request etc.
      LOGGER.error("ERROR fetching user info - {}", e.getStatusCode());
      return new UserMetadataResponse(
          null,
          new ResponseError(
              String.format("ERROR fetching user info - %s", e.getStatusCode()),
              e.getStatusCode().value()
          )
      );
    } catch (Exception e) {
      // Catch all other exceptions - server, network etc.
      // We don't want to expose the exception message to the client.
      LOGGER.error("ERROR fetching user info - {}", e.getMessage());
      return new UserMetadataResponse(
          null,
          new ResponseError(
              "Internal server error fetching user info",
              HttpStatus.INTERNAL_SERVER_ERROR.value()
          )
      );
    }
  }

  /**
   * Refreshes the user's JWT tokens based on their refresh token.
   *
   * @param refreshToken JWT refresh token.
   * @return LoginResponse containing the JWT tokens & possibly non-null error object.
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
          new ResponseError(
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
          new ResponseError(
              "Internal server error issuing the JWT token",
              HttpStatus.INTERNAL_SERVER_ERROR.value()
          )
      );
    }
  }

  /**
   * Logs out the user out of the session based on their ID token.
   *
   * @param idToken The user's ID token - this is issued upon login.
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
   * @return True if the token was revoked successfully, false otherwise.
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
