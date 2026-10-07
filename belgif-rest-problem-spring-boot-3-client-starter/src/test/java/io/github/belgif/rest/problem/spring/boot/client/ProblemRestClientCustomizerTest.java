package io.github.belgif.rest.problem.spring.boot.client;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import io.github.belgif.rest.problem.spring.client.ProblemResponseErrorHandler;

class ProblemRestClientCustomizerTest {

    @Test
    void customize() {
        ProblemResponseErrorHandler handler = mock(ProblemResponseErrorHandler.class);
        ProblemRestClientCustomizer customizer = new ProblemRestClientCustomizer(handler) {
        };
        RestClient.Builder builder = RestClient.builder();
        customizer.customize(builder);

        assertThat(builder).extracting("statusHandlers", as(InstanceOfAssertFactories.LIST))
                .hasSize(1);
    }

}
