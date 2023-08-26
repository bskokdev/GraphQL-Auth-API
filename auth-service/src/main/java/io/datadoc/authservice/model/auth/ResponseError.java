package io.datadoc.authservice.model.auth;

/**
 * LoginError represents an error response from the Keycloak token endpoint.
 *
 * @param message The error message.
 * @param code    The error code.
 */
public record ResponseError(String message, int code) {

}
