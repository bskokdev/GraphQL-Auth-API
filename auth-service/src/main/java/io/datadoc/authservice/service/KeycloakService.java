package io.datadoc.authservice.service;

import static io.datadoc.authservice.config.KeycloakConstants.GRANT_TYPE_CLIENT_CREDENTIALS;
import static io.datadoc.authservice.config.KeycloakConstants.GRANT_TYPE_PASSWORD;
import static io.datadoc.authservice.config.KeycloakConstants.SCOPE_OPENID;

import io.datadoc.authservice.config.KeycloakConfig;
import io.datadoc.authservice.model.auth.JwtPayload;
import io.datadoc.authservice.model.auth.LoginCredentials;
import io.datadoc.authservice.model.http.HttpFormBuilder;
import java.util.Objects;
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
        withGrantType(GRANT_TYPE_PASSWORD).
        withClientId(this.keycloakConfig.getClient().getId()).
        withClientSecret(this.keycloakConfig.getClient().getSecret()).
        withUsername(credentials.email()).
        withPassword(credentials.password()).
        withScope(SCOPE_OPENID).
        build();

    LOGGER.info("Keycloak requesting JWT token for user...");
    return restTemplate.postForEntity(
        this.keycloakConfig.getEndpoints().getToken(),
        new HttpEntity<>(httpForm, this.httpService.getHttpFormHeaders()),
        JwtPayload.class
    );
  }

  /**
   * Request JWT tokens from Keycloak instance for its client. Client is a registered application
   * within Keycloak. Refer to the KeycloakConfig class & Keycloak documentation for more details.
   *
   * @return LoginResponse containing the JWT tokens.
   * @throws HttpStatusCodeException If the request to Keycloak fails - Unauthorized, Bad Request
   */
  public ResponseEntity<JwtPayload> fetchTokensForClient() throws HttpStatusCodeException {
    MultiValueMap<String, String> httpForm = new HttpFormBuilder().
        withGrantType(GRANT_TYPE_CLIENT_CREDENTIALS).
        withClientId(this.keycloakConfig.getClient().getId()).
        withClientSecret(this.keycloakConfig.getClient().getSecret()).
        build();

    LOGGER.info("Keycloak requesting JWT token for a registered client...");
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
  public void revokeKeycloakToken(String token) throws HttpStatusCodeException {
    // Get a client access token - revocation endpoint requires a client access token.
    String clientAccessToken = Objects.requireNonNull(
        fetchTokensForClient().getBody()
    ).accessToken();

    MultiValueMap<String, String> httpForm = new HttpFormBuilder().
        withGrantType(GRANT_TYPE_CLIENT_CREDENTIALS).
        withClientId(this.keycloakConfig.getClient().getId()).
        withClientSecret(this.keycloakConfig.getClient().getSecret()).
        withToken(token).
        build();

    LOGGER.info("Keycloak attempting to revoke a JWT token...");
    restTemplate.postForEntity(
        this.keycloakConfig.getEndpoints().getRevoke(),
        new HttpEntity<>(httpForm, this.httpService.getHttpFormHeaders(clientAccessToken)),
        Void.class
    );
  }

  public void logoutKeycloakUser(String idToken) throws HttpStatusCodeException {
    MultiValueMap<String, String> httpForm = new HttpFormBuilder().
        withIdToken(idToken).
        withClientId(this.keycloakConfig.getClient().getId()).
        withClientSecret(this.keycloakConfig.getClient().getSecret()).
        build();

    LOGGER.info("Keycloak attempting to logout a user...");
    restTemplate.postForEntity(
        this.keycloakConfig.getEndpoints().getLogout(),
        new HttpEntity<>(httpForm, this.httpService.getHttpFormHeaders()),
        Void.class
    );
  }
}
