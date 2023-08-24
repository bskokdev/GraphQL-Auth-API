package io.datadoc.authservice.model;

public record LoginResponse(
        JwtPayload jwtPayload,
        LoginError loginError
) {
}
