package io.datadoc.authservice.model.http;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Builder for creating a MultiValueMap for HTTP form data. This form is used for making requests to
 * Keycloak and passing in the required parameters.
 */
public class HttpFormBuilder {

  private final MultiValueMap<String, String> httpForm = new LinkedMultiValueMap<>();

  /**
   * Add a grant type to the form.
   *
   * @param grantType Requested grant type.
   * @return this
   */
  public HttpFormBuilder withGrantType(String grantType) {
    this.httpForm.add("grant_type", grantType);
    return this;
  }

  /**
   * Add a token to the form.
   *
   * @param token JWT token.
   * @return this
   */
  public HttpFormBuilder withToken(String token) {
    this.httpForm.add("token", token);
    return this;
  }

  /**
   * Add an ID token to the form.
   *
   * @param idToken ID token from Keycloak.
   * @return this
   */
  public HttpFormBuilder withIdToken(String idToken) {
    this.httpForm.add("id_token", idToken);
    return this;
  }

  /**
   * Add a client ID to the form
   *
   * @param clientId ID of an application registered with Keycloak.
   * @return this
   */
  public HttpFormBuilder withClientId(String clientId) {
    this.httpForm.add("client_id", clientId);
    return this;
  }

  /**
   * Add a client secret to the form.
   *
   * @param clientSecret Secret of an application registered with Keycloak.
   * @return this
   */
  public HttpFormBuilder withClientSecret(String clientSecret) {
    this.httpForm.add("client_secret", clientSecret);
    return this;
  }

  /**
   * Add a username to the form.
   *
   * @param username Username of the user.
   * @return this
   */
  public HttpFormBuilder withUsername(String username) {
    this.httpForm.add("username", username);
    return this;
  }

  /**
   * Add user password to the form.
   *
   * @param password Password of the user.
   * @return this
   */
  public HttpFormBuilder withPassword(String password) {
    this.httpForm.add("password", password);
    return this;
  }

  /**
   * Add a scope to the form.
   *
   * @param scope - Keycloak will issue different tokens based on the scope.
   * @return this
   */
  public HttpFormBuilder withScope(String scope) {
    this.httpForm.add("scope", scope);
    return this;
  }

  /**
   * Build the http form.
   *
   * @return the built form of type MultiValueMap
   */
  public MultiValueMap<String, String> build() {
    return this.httpForm;
  }
}
