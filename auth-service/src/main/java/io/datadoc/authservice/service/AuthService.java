package io.datadoc.authservice.service;

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
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static io.datadoc.authservice.config.KeycloakConstants.*;

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
    LOGGER.info("Attempting to login user: {}", credentials.email());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> httpForm = new LinkedMultiValueMap<>();
    httpForm.add(GRANT_TYPE_KEY, GRANT_TYPE);
    httpForm.add(CLIENT_ID_KEY, this.keycloakConfig.getClient());
    httpForm.add(EMAIL_KEY, credentials.email());
    httpForm.add(PASSWORD_KEY, credentials.password());

    return fetchKeycloakTokens(headers, httpForm);
  }

  /**
   * Calls the token endpoint to get the JWT tokens.
   *
   * @param headers The headers to send with the request.
   * @param form    The form data to send with the request. (grant_type, client_id, email, password)
   * @return LoginResponse containing the JWT tokens or an error object.
   */
  private LoginResponse fetchKeycloakTokens(HttpHeaders headers, MultiValueMap<String, String> form) {
    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
    try {
      JwtPayload jwtPayload = restTemplate.postForEntity(
              this.keycloakConfig.getEndpoints().getToken(), entity, JwtPayload.class).getBody();

      LOGGER.info("Successfully logged in user: {}", form.get(EMAIL_KEY));
      return jwtPayload;
    } catch (Exception e) {
      LOGGER.error("Error logging in user {} : {}", form.get(EMAIL_KEY), e.getMessage());
      return new LoginError("Invalid credentials", HttpStatus.UNAUTHORIZED.value());
    }
  }
}
