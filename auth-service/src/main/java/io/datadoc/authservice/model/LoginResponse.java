package io.datadoc.authservice.model;

/**
 * Response from the login endpoint to the client. Contains the JWT tokens & an error object.
 * Both fields are nullable.
 *
 * @param jwtPayload The JWT tokens.
 * @param loginError The error object.
 */
public record LoginResponse(
        JwtPayload jwtPayload,
        LoginError loginError
) {
}
