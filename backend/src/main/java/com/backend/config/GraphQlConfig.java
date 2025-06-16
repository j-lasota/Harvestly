package com.backend.config;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class GraphQlConfig {

    @Bean
    public DataFetcherExceptionResolver exceptionResolver() {
        return (exception, environment) -> {
            // Sprawdzamy "łańcuch przyczyn" wyjątku. Czasem błąd bezpieczeństwa
            // jest opakowany w inny, generyczny wyjątek.
            Throwable e = findCause(exception, AccessDeniedException.class, AuthenticationException.class);
            if (e == null) {
                // Jeśli to nie jest błąd bezpieczeństwa, nie robimy nic
                return Mono.empty();
            }

            // Teraz, gdy mamy już "czysty" błąd bezpieczeństwa, mapujemy go
            if (e instanceof AccessDeniedException) {
                return Mono.just(List.of(buildGraphQLError(e, environment, ErrorType.FORBIDDEN)));
            }
            if (e instanceof AuthenticationException) {
                return Mono.just(List.of(buildGraphQLError(e, environment, ErrorType.UNAUTHORIZED)));
            }

            // Domyślnie nic nie robimy
            return Mono.empty();
        };
    }

    // Metoda pomocnicza do "rozpakowywania" wyjątków
    private Throwable findCause(Throwable throwable, Class<?>... causeTypes) {
        Throwable e = throwable;
        while (e != null) {
            for (Class<?> causeType : causeTypes) {
                if (causeType.isInstance(e)) {
                    return e;
                }
            }
            e = e.getCause();
        }
        return null;
    }

    private GraphQLError buildGraphQLError(Throwable exception, DataFetchingEnvironment environment, ErrorType errorType) {
        return GraphqlErrorBuilder.newError()
                .errorType(errorType)
                .message(errorType.name()) // Bezpieczna, generyczna wiadomość
                .path(environment.getExecutionStepInfo().getPath())
                .location(environment.getField().getSourceLocation())
                .build();
    }
}