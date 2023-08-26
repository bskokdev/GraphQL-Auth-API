package io.datadoc.authservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * This is a helper service for making HTTP requests. Provides methods for getting the headers for a
 * request that requires a form body.
 */
@Service
public class HttpService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
  private final RestTemplate restTemplate;

  public HttpService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Get the headers for a request that requires a form body.
   *
   * @return The headers for a request that requires a form body.
   */
  public HttpHeaders getHttpFormHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    return headers;
  }

  /**
   * Get the headers for a request that requires a form body and an authorization token.
   *
   * @param token The authorization token.
   * @return The headers for a request that requires a form body and an authorization token.
   */
  public HttpHeaders getHttpFormHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.set("Authorization", "Bearer " + token);
    return headers;
  }
}
