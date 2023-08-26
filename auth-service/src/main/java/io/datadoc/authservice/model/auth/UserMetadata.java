package io.datadoc.authservice.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserMetadata(
    @JsonProperty("sub") String id,
    @JsonProperty("preferred_username") String username,
    @JsonProperty("given_name") String firstName,
    @JsonProperty("family_name") String lastName,
    @JsonProperty("email") String email,
    @JsonProperty("email_verified") boolean emailVerified
) {

}
