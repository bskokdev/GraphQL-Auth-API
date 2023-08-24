package io.datadoc.authservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JwtPayload(
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("refresh_token") String refreshToken,
    @JsonProperty("expires_in") int expiresIn,
    @JsonProperty("refresh_expires_in") int refreshTokenExpiresIn)
    implements LoginResponse {}
