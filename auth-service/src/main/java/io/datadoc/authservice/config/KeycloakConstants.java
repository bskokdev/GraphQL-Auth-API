package io.datadoc.authservice.config;

/** Constants used for Keycloak authentication http form. */
public final class KeycloakConstants {
  public static final String GRANT_TYPE_KEY = "grant_type";
  public static final String GRANT_TYPE = "password";
  public static final String CLIENT_ID_KEY = "client_id";
  public static final String EMAIL_KEY = "username";
  public static final String PASSWORD_KEY = "password";

  private KeycloakConstants() {
    throw new IllegalStateException("Utility class");
  }
}
