package io.github.belgif.rest.problem.spring.boot.client;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.spring.client.ProblemExchangeFilterFunction;
import io.github.belgif.rest.problem.spring.client.ProblemResponseErrorHandler;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ProblemExchangeFilterFunctionTest {

    @InjectMocks
    private ProblemExchangeFilterFunction filter;

    @Mock
    private ProblemResponseErrorHandler problemResponseErrorHandler;

    @Mock
    private ExchangeFunction exchangeFunction;

    @Mock
    private ClientRequest request;

    @Test
    void problemMediaType() throws Exception {
        ClientResponse response = ClientResponse.create(HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/problem+json")
                .body("{}")
                .build();
        when(exchangeFunction.exchange(request)).thenReturn(Mono.just(response));
        Problem problem = new BadRequestProblem();
        when(problemResponseErrorHandler.handleProblem(eq(400), any(InputStream.class))).thenReturn(problem);

        Mono<ClientResponse> result = filter.filter(request, exchangeFunction);

        assertThatException().isThrownBy(result::block).isEqualTo(problem);
    }

    @Test
    void jsonMediaTypeErrorStatus() throws Exception {
        ClientResponse response = ClientResponse.create(HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/json")
                .body("{}")
                .build();
        when(exchangeFunction.exchange(request)).thenReturn(Mono.just(response));
        Problem problem = new BadRequestProblem();
        when(problemResponseErrorHandler.handleProblem(eq(400), any(InputStream.class))).thenReturn(problem);

        Mono<ClientResponse> result = filter.filter(request, exchangeFunction);

        assertThatException().isThrownBy(result::block).isEqualTo(problem);
    }

    @Test
    void jsonMediaTypeNoErrorStatus() {
        ClientResponse response = ClientResponse.create(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body("{}")
                .build();
        when(exchangeFunction.exchange(request)).thenReturn(Mono.just(response));
        Mono<ClientResponse> result = filter.filter(request, exchangeFunction);

        assertThat(result.block()).isEqualTo(response);
    }

    @Test
    void differentMediaType() {
        ClientResponse response = ClientResponse.create(HttpStatus.OK)
                .header("Content-Type", "application/xml")
                .body("{}")
                .build();
        when(exchangeFunction.exchange(request)).thenReturn(Mono.just(response));
        Mono<ClientResponse> result = filter.filter(request, exchangeFunction);

        assertThat(result.block()).isEqualTo(response);
    }

    @Test
    void healthDown() throws Exception {
        ClientResponse response = ClientResponse.create(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Content-Type", "application/json")
                .header("Test", "foo")
                .cookie("Cookie", "value")
                .body("{ \"status\" : \"DOWN\" }")
                .build();
        when(exchangeFunction.exchange(request)).thenReturn(Mono.just(response));
        when(problemResponseErrorHandler.handleProblem(eq(503), any(InputStream.class))).thenReturn(null);

        ClientResponse result = filter.filter(request, exchangeFunction).block();
        assertThat(result.statusCode().value()).isEqualTo(503);
        assertThat(result.headers().asHttpHeaders()).isEqualTo(response.headers().asHttpHeaders());
        assertThat(result.cookies()).isEqualTo(response.cookies());
    }

}
