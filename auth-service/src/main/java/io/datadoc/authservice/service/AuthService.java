package io.datadoc.authservice.service;

import static io.datadoc.authservice.config.KeycloakConstants.CLIENT_ID_KEY;
import static io.datadoc.authservice.config.KeycloakConstants.EMAIL_KEY;
import static io.datadoc.authservice.config.KeycloakConstants.GRANT_TYPE;
import static io.datadoc.authservice.config.KeycloakConstants.GRANT_TYPE_KEY;
import static io.datadoc.authservice.config.KeycloakConstants.PASSWORD_KEY;
import static io.datadoc.authservice.config.KeycloakConstants.TOKEN_KEY;

import io.datadoc.authservice.config.KeycloakConfig;
import io.datadoc.authservice.model.JwtPayload;
import io.datadoc.authservice.model.LoginCredentials;
import io.datadoc.authservice.model.LoginError;
import io.datadoc.authservice.model.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * AuthService provides methods for authenticating users & JWT token management. It uses Keycloak as
 * the identity provider - refer to the KeycloakConfig class & Keycloak documentation for more
 * details.
 */
@Service
public class AuthService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
  private final RestTemplate restTemplate;
  private final KeycloakConfig keycloakConfig;

  public AuthService(RestTemplate restTemplate, KeycloakConfig keycloakConfig) {
    this.restTemplate = restTemplate;
    this.keycloakConfig = keycloakConfig;
  }

  /**
   * Issue a JWT tokens to the user with the given credentials.
   *
   * @param credentials The user's credentials - email & password.
   * @return LoginResponse containing the JWT tokens or an error object.
   */
  public LoginResponse issueJwtTokensToUser(LoginCredentials credentials) {
    MultiValueMap<String, String> httpForm = new LinkedMultiValueMap<>();
    httpForm.add(GRANT_TYPE_KEY, GRANT_TYPE);
    httpForm.add(CLIENT_ID_KEY, this.keycloakConfig.getClient());
    httpForm.add(EMAIL_KEY, credentials.email());
    httpForm.add(PASSWORD_KEY, credentials.password());

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(httpForm, getFormHeaders());
    ResponseEntity<JwtPayload> res = postForKeycloakEntity(
        entity,
        this.keycloakConfig.getEndpoints().getToken(),
        JwtPayload.class
    );

    if (res.getStatusCode() != HttpStatus.OK) {
      LOGGER.error("Error issuing JWT token: {}", res.getStatusCode());
      return new LoginResponse(
          null,
          new LoginError("Error issuing JWT token", res.getStatusCode().value())
      );
    }
    LOGGER.info("Issued a new JWT token");
    // TODO(bskokdev) - res.getBody() is null for some reason, but credential check is ok
    return new LoginResponse(res.getBody(), null);
  }

  /**
   * Revoke the given JWT token. This will invalidate the token and the user will be logged out.
   *
   * @param accessToken The JWT token to revoke.
   */
  public boolean revokeJwtToken(String accessToken) {
    MultiValueMap<String, String> httpForm = new LinkedMultiValueMap<>();
    httpForm.add(TOKEN_KEY, accessToken);
    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(httpForm, getFormHeaders());

    ResponseEntity<Void> res = postForKeycloakEntity(
        entity,
        this.keycloakConfig.getEndpoints().getRevoke(),
        Void.class
    );

    if (res.getStatusCode() != HttpStatus.OK) {
      LOGGER.error("Error revoking JWT token: {}", res.getStatusCode());
      return false;
    }
    LOGGER.info("Revoked JWT token");
    return true;
  }

  /**
   * Get the headers for a Keycloak request. This is used for requests that require a form body.
   *
   * @return The headers for a Keycloak request.
   */
  private HttpHeaders getFormHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    return headers;
  }

  /**
   * Handler for POST requests to Keycloak. This method will return the response entity of the given
   * type or a response entity with the error status code.
   *
   * @param entity       The request entity.
   * @param url          The Keycloak endpoint.
   * @param responseType The response type class.
   * @param <T>          The generic type.
   * @return The response entity of the given type.
   */
  private <T> ResponseEntity<T> postForKeycloakEntity(
      HttpEntity<MultiValueMap<String, String>> entity, String url, Class<T> responseType) {
    try {
      return restTemplate.postForEntity(url, entity, responseType);
    } catch (HttpClientErrorException e) {
      return new ResponseEntity<>(e.getStatusCode());
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
