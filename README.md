# GraphQL Authentication Service API in Java

## Introduction

Simple Spring Boot application with GraphQL API and JWT authentication.
The API provides several queries and mutations to manage users and their roles

(see [GraphQL schema](./auth-service/src/main/resources/graphql/schema.graphqls)).

## Getting Started

1. Clone the repository
2. Run `docker compose up -d` to start the containers
    - this will start the Keycloak instance and the authentication service itself
3. Access the Keycloak admin console at http://localhost:8080/admin
    - Admin login: `username: admin & passowrd: admin`
    - Keycloak imports the realm configuration from the `mock-realm.json` file
    - The realm contains 2 users:
        - `username: user1 & password: user1pass`
        - `username: user2 & password: user2pass`
4. Access the GraphQL API at http://localhost:8081/graphiql

## GraphQL Queries and Mutations

### Queries

- `me` - returns information about user with the provided JWT access token.
    - parameters:
        - `accessToken` - JWT access token

```graphql
query Me {
    me(accessToken: "JWT access token goes here") {
        id,
        username,
        firstName,
        lastName,
        email,
        emailVerified
    }
}
```

### Mutations

- `login` - returns JWT tokens for the provided username and password if credentials are valid.
    - parameters:
        - `credentials` - object containing username and password.
            - `email` - email of the user.
            - `password` - password of the user.

```graphql
mutation Login {
    login(credentials: {email: "user2@mockrealm.com", password: "user2pass"}) {
        tokenType,
        accessToken,
        refreshToken,
        idToken,
        expiresIn,
        refreshTokenExpiresIn,
    }
}
```

- `refresh` - returns new JWT tokens for the provided refresh token if it is valid.
    - parameters:
        - `refreshToken` - JWT refresh token.

```graphql
mutation RefreshTokens {
    refresh(refreshToken: "JWT refresh token goes here") {
        accessToken
        refreshToken
        idToken
        expiresIn
        refreshTokenExpiresIn
        tokenType
    }
}
```

- `logout` - logs out the user with the provided JWT ID token out of the session.
    - parameters:
        - `idToken` - JWT ID token.

```graphql 
mutation LogoutUser {
    logout(idToken: "JWT ID token goes here")
}
```

- `revoke` - invalidates the provided JWT token.
    - parameters:
        - `token` - JWT token.

```graphql
mutation RevokeToken {
    revoke(token: "JWT token goes here")
}
```