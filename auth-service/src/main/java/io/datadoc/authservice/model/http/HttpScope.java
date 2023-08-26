package io.datadoc.authservice.model.http;

import lombok.Getter;

@Getter
public enum HttpScope {
  OPENID("openid");

  private final String scope;

  HttpScope(String scope) {
    this.scope = scope;
  }
}
