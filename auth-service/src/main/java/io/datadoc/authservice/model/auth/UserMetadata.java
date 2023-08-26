package io.datadoc.authservice.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UserMetadata contains the user's information stored in Keycloak.
 *
 * @param id            user's ID
 * @param username      user's username
 * @param firstName     user's first name
 * @param lastName      user's last name
 * @param email         user's email
 * @param emailVerified true if the user's email is verified, false otherwise.
 */
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
