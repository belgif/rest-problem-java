package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.belgif.rest.problem.api.Problem;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ProblemWebClientCustomizerTest {

    private final ProblemWebClientCustomizer customizer = new ProblemWebClientCustomizer();

    @Mock
    private WebClient.Builder builder;

    @Mock
    private ExchangeFunction exchangeFunction;

    @Mock
    private ClientRequest request;

    @Captor
    private ArgumentCaptor<ExchangeFilterFunction> filterCaptor;

    @Test
    void problemMediaType() {
        customizer.customize(builder);
        verify(builder).filter(filterCaptor.capture());
        ExchangeFilterFunction filter = filterCaptor.getValue();
        assertThat(filter).isNotNull();

        ClientResponse response = ClientResponse.create(HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/problem+json")
                .body("{}")
                .build();
        when(exchangeFunction.exchange(request)).thenReturn(Mono.just(response));
        Mono<ClientResponse> result = filter.filter(request, exchangeFunction);

        assertThatExceptionOfType(Problem.class)
                .isThrownBy(result::block);
    }

    @Test
    void jsonMediaTypeErrorStatus() {
        customizer.customize(builder);
        verify(builder).filter(filterCaptor.capture());
        ExchangeFilterFunction filter = filterCaptor.getValue();
        assertThat(filter).isNotNull();

        ClientResponse response = ClientResponse.create(HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/json")
                .body("{}")
                .build();
        when(exchangeFunction.exchange(request)).thenReturn(Mono.just(response));
        Mono<ClientResponse> result = filter.filter(request, exchangeFunction);

        assertThatExceptionOfType(Problem.class)
                .isThrownBy(result::block);
    }

    @Test
    void jsonMediaTypeNoErrorStatus() {
        customizer.customize(builder);
        verify(builder).filter(filterCaptor.capture());
        ExchangeFilterFunction filter = filterCaptor.getValue();
        assertThat(filter).isNotNull();

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
        customizer.customize(builder);
        verify(builder).filter(filterCaptor.capture());
        ExchangeFilterFunction filter = filterCaptor.getValue();
        assertThat(filter).isNotNull();

        ClientResponse response = ClientResponse.create(HttpStatus.OK)
                .header("Content-Type", "application/xml")
                .body("{}")
                .build();
        when(exchangeFunction.exchange(request)).thenReturn(Mono.just(response));
        Mono<ClientResponse> result = filter.filter(request, exchangeFunction);

        assertThat(result.block()).isEqualTo(response);
    }

}
