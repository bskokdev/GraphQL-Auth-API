package io.datadoc.authservice.model.auth;

/**
 * LoginCredentials represents the user's credentials.
 *
 * @param email    The user's email address.
 * @param password The user's password.
 */
public record LoginCredentials(String email, String password) {

}
