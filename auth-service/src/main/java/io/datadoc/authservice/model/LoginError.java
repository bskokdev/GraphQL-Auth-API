package io.datadoc.authservice.model;

public record LoginError(String message, int code) implements LoginResponse {}
