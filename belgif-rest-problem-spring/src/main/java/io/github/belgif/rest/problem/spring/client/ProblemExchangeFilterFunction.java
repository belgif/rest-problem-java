package io.github.belgif.rest.problem.spring.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.spring.ProblemMediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * ExchangeFilterFunction that converts problem responses to Problem exceptions for reactive WebClient.
 */
@Component
public class ProblemExchangeFilterFunction implements ExchangeFilterFunction {

    private final ProblemResponseErrorHandler errorHandler;

    public ProblemExchangeFilterFunction(ProblemResponseErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return next.exchange(request).flatMap(this::handleResponse);
    }

    private Mono<ClientResponse> handleResponse(ClientResponse response) {
        MediaType mediaType = response.headers().contentType().orElse(null);
        if (ProblemMediaType.INSTANCE.isCompatibleWith(mediaType)
                || (response.statusCode().isError() && MediaType.APPLICATION_JSON.isCompatibleWith(mediaType))) {
            return DataBufferUtils.join(response.body(BodyExtractors.toDataBuffers()))
                    .defaultIfEmpty(new DefaultDataBufferFactory().wrap(new byte[0]))
                    .flatMap(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        Problem problem;
                        try {
                            problem = errorHandler.handleProblem(
                                    response.statusCode().value(), new ByteArrayInputStream(bytes));
                        } catch (IOException e) {
                            return Mono.error(new UncheckedIOException(e));
                        }
                        if (problem != null) {
                            return Mono.error(problem);
                        }
                        ClientResponse rebuilt = ClientResponse
                                .create(response.statusCode())
                                // not using putAll() for headers because it results in IncompatibleClassChangeError:
                                // Class org.springframework.http.ReadOnlyHttpHeaders does not implement
                                // the requested interface java.util.Map
                                .headers(headers -> response.headers().asHttpHeaders()
                                        .forEach((name, values) -> values.forEach(value -> headers.add(name, value))))
                                .cookies(cookies -> cookies.putAll(response.cookies()))
                                .body(Flux.just(new DefaultDataBufferFactory().wrap(bytes)))
                                .build();
                        return Mono.just(rebuilt);
                    });
        }
        return Mono.just(response);
    }

}
