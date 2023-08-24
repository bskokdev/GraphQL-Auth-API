package io.datadoc.authservice.model;

/**
 * LoginError represents an error response from the Keycloak token endpoint.
 *
 * @param message The error message.
 * @param code    The error code.
 */
public record LoginError(String message, int code) {
}
