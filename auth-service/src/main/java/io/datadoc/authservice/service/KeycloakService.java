package io.datadoc.authservice.service;

import io.datadoc.authservice.config.KeycloakConfig;
import io.datadoc.authservice.model.auth.JwtPayload;
import io.datadoc.authservice.model.auth.LoginCredentials;
import io.datadoc.authservice.model.http.HttpFormBuilder;
import io.datadoc.authservice.model.http.HttpGrantType;
import io.datadoc.authservice.model.http.HttpScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

/**
 * This class provides methods for interacting with Keycloak. Its main purpose is to interact with
 * Keycloak endpoints.
 * TODO(bskokdev) - Add integration tests!!!.
 */
@Service
public class KeycloakService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
  private final RestTemplate restTemplate;
  private final KeycloakConfig keycloakConfig;
  private final HttpService httpService;

  public KeycloakService(
      RestTemplate restTemplate,
      KeycloakConfig keycloakConfig,
      HttpService httpService
  ) {
    this.restTemplate = restTemplate;
    this.keycloakConfig = keycloakConfig;
    this.httpService = httpService;
  }

  /**
   * Request JWT tokens from Keycloak instance for the user with the given credentials.
   *
   * @param credentials The user's credentials - email & password.
   * @return LoginResponse containing the JWT tokens.
   * @throws HttpStatusCodeException If the request to Keycloak fails.
   */
  public ResponseEntity<JwtPayload> fetchTokensForUser(LoginCredentials credentials)
      throws HttpStatusCodeException {
    MultiValueMap<String, String> httpForm = new HttpFormBuilder().
        withGrantType(HttpGrantType.PASSWORD.getGrantType()).
        withClientId(this.keycloakConfig.getClient().getId()).
        withClientSecret(this.keycloakConfig.getClient().getSecret()).
        withUsername(credentials.email()).
        withPassword(credentials.password()).
        withScope(HttpScope.OPENID.getScope()).
        build();

    LOGGER.info("Keycloak requesting JWT token for user...");
    return restTemplate.postForEntity(
        this.keycloakConfig.getEndpoints().getToken(),
        new HttpEntity<>(httpForm, this.httpService.getHttpFormHeaders()),
        JwtPayload.class
    );
  }

  /**
   * Refreshes the user's JWT tokens based on the refresh token. Makes a request to Keycloak token
   * endpoint.
   *
   * @param refreshToken The refresh token issued to the user by Keycloak.
   * @return JwtPayload containing the JWT tokens.
   * @throws HttpStatusCodeException If the request to Keycloak fails - Unauthorized, Bad Request
   */
  public ResponseEntity<JwtPayload> refreshTokens(String refreshToken)
      throws HttpStatusCodeException {
    MultiValueMap<String, String> httpForm = new HttpFormBuilder().
        withGrantType(HttpGrantType.REFRESH_TOKEN.getGrantType()).
        withClientId(this.keycloakConfig.getClient().getId()).
        withClientSecret(this.keycloakConfig.getClient().getSecret()).
        withRefreshToken(refreshToken).
        build();

    LOGGER.info("Keycloak attempting to refresh a JWT token...");
    return restTemplate.postForEntity(
        this.keycloakConfig.getEndpoints().getToken(),
        new HttpEntity<>(httpForm, this.httpService.getHttpFormHeaders()),
        JwtPayload.class
    );
  }

  /**
   * Request Keycloak to revoke the given JWT token. This requires a client access token - refer to
   * the KeycloakConfig class & Keycloak docs.
   *
   * @param token JWT token to be revoked.
   * @throws HttpStatusCodeException If the request to Keycloak fails - Unauthorized, Bad Request
   */
  public ResponseEntity<String> revokeKeycloakToken(String token) throws HttpStatusCodeException {
    MultiValueMap<String, String> httpForm = new HttpFormBuilder().
        withGrantType(HttpGrantType.CLIENT_CREDENTIALS.getGrantType()).
        withClientId(this.keycloakConfig.getClient().getId()).
        withClientSecret(this.keycloakConfig.getClient().getSecret()).
        withGenericToken(token).
        build();

    LOGGER.info("Keycloak attempting to revoke a JWT token...");
    return restTemplate.postForEntity(
        this.keycloakConfig.getEndpoints().getRevoke(),
        new HttpEntity<>(httpForm, this.httpService.getHttpFormHeaders()),
        String.class
    );
  }

  /**
   * Request Keycloak to log out a user based on their ID token.
   *
   * @param idToken ID token previously issued to the user by Keycloak.
   * @throws HttpStatusCodeException If the request to Keycloak fails - Unauthorized, Bad Request
   */
  public ResponseEntity<String> logoutKeycloakUser(String idToken) throws HttpStatusCodeException {
    MultiValueMap<String, String> httpForm = new HttpFormBuilder().
        withIdToken(idToken).
        withClientId(this.keycloakConfig.getClient().getId()).
        withClientSecret(this.keycloakConfig.getClient().getSecret()).
        build();

    LOGGER.info("Keycloak attempting to logout a user...");
    return restTemplate.postForEntity(
        this.keycloakConfig.getEndpoints().getLogout(),
        new HttpEntity<>(httpForm, this.httpService.getHttpFormHeaders()),
        String.class
    );
  }
}
