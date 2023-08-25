package io.datadoc.authservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakConfig {

  private String realm;
  private String client;
  private String baseUrl;
  private String protocol;
  private Endpoints endpoints;

  @Getter
  @Setter
  public static class Endpoints {

    private String token;
    private String revoke;
  }
}

