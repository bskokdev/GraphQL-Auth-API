package io.datadoc.authservice.config;

/**
 * Constants used for Keycloak authentication.
 */
public final class KeycloakConstants {

  public static final String GRANT_TYPE_PASSWORD = "password";
  public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
  public static final String SCOPE_OPENID = "openid";

  private KeycloakConstants() {
    throw new IllegalStateException("Utility class");
  }
}
