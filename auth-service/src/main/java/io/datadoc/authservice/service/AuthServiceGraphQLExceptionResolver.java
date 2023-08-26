package io.datadoc.authservice.service;


import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

/**
 * AuthServiceGraphQLExceptionResolver is responsible for mapping exceptions thrown by the
 * AuthResolver to GraphQL errors.
 */
@Component
public class AuthServiceGraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {

  /**
   * Maps the exception to a GraphQLError.
   *
   * @param ex  The exception
   * @param env The DataFetchingEnvironment
   * @return The GraphQLError
   */
  @Override
  protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
    Throwable t = NestedExceptionUtils.getMostSpecificCause(ex);

    // Exception is a HttpClientErrorException.
    if (t instanceof HttpClientErrorException httpException) {
      ErrorClassification errorClassification = mapHttpStatusToErrorClassification(
          (HttpStatus) httpException.getStatusCode()
      );
      return GraphqlErrorBuilder.newError(env)
          .errorType(errorClassification)
          .message(t.getMessage())
          .build();
    }

    // Handle all other exceptions.
    if (t instanceof Exception) {
      return GraphqlErrorBuilder.newError(env)
          .errorType(ErrorType.ExecutionAborted)
          .message("An internal server error occurred.")
          .build();
    }
    return null;
  }

  /**
   * Maps the HttpStatus to the corresponding ErrorClassification.
   *
   * @param httpStatus The HttpStatus to map.
   * @return The corresponding ErrorClassification.
   */
  private ErrorClassification mapHttpStatusToErrorClassification(HttpStatus httpStatus) {
    return switch (httpStatus) {
      case UNAUTHORIZED, FORBIDDEN -> ErrorType.ValidationError;
      default -> ErrorType.DataFetchingException;
    };
  }
}



