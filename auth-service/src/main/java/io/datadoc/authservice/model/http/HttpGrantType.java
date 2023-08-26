package io.datadoc.authservice.model.http;

import lombok.Getter;

@Getter
public enum HttpGrantType {
  CLIENT_CREDENTIALS("client_credentials"),
  PASSWORD("password"),
  REFRESH_TOKEN("refresh_token");

  private final String grantType;

  HttpGrantType(String grantType) {
    this.grantType = grantType;
  }
}
