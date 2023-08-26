package io.datadoc.authservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This is a configuration class for the Keycloak. It provides basic information about the Keycloak
 * instance.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakConfig {

  private String realm;
  private Client client;
  private String baseUrl;
  private String protocol;
  private Endpoints endpoints;

  @Getter
  @Setter
  public static class Client {

    private String id;
    private String secret;
  }

  @Getter
  @Setter
  public static class Endpoints {

    private String token;
    private String revoke;
    private String logout;
    private String userInfo;
  }
}

