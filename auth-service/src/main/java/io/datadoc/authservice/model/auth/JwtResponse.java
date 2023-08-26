package io.datadoc.authservice.model.auth;

/**
 * Response from the login endpoint to the client. Contains the JWT tokens & an error object. Both
 * fields are nullable.
 *
 * @param payload The JWT tokens.
 * @param error   The error object.
 */
public record JwtResponse(
    JwtPayload payload,
    ResponseError error
) {

}
