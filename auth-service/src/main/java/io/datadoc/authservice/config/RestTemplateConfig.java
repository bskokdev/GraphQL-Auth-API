package io.datadoc.authservice.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * This is a configuration class for the RestTemplate.
 */
@Configuration
public class RestTemplateConfig {

  /**
   * Bean for the RestTemplate - used for making HTTP requests.
   *
   * @param builder The RestTemplateBuilder.
   * @return The RestTemplate.
   */
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }
}
