package io.datadoc.authservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JwtPayload represents the response from the Keycloak token endpoint.
 *
 * @param tokenType             The type of token returned.
 * @param accessToken           The access token.
 * @param refreshToken          The refresh token.
 * @param expiresIn             The time in seconds until the access token expires.
 * @param refreshTokenExpiresIn The time in seconds until the refresh token expires.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JwtPayload(
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("refresh_token") String refreshToken,
    @JsonProperty("expires_in") int expiresIn,
    @JsonProperty("refresh_expires_in") int refreshTokenExpiresIn) {

}
