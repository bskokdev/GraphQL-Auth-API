package io.datadoc.authservice.model.auth;

/**
 * Response from the user information endpoint to the client. Contains the user information & and an
 * error object. Both are nullable.
 *
 * @param userMetadata
 * @param error
 */
public record UserMetadataResponse(
    UserMetadata userMetadata,
    ResponseError error
) {

}
