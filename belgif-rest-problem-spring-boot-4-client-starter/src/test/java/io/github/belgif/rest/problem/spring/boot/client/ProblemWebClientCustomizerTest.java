package io.github.belgif.rest.problem.spring.boot.client;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.belgif.rest.problem.spring.client.ProblemExchangeFilterFunction;

@ExtendWith(MockitoExtension.class)
class ProblemWebClientCustomizerTest {

    @InjectMocks
    private ProblemWebClientCustomizer customizer;

    @Mock
    private ProblemExchangeFilterFunction exchangeFilterFunction;

    @Test
    void customize() {
        WebClient.Builder builder = WebClient.builder();
        customizer.customize(builder);

        assertThat(builder).extracting("filters", as(InstanceOfAssertFactories.LIST))
                .containsExactly(exchangeFilterFunction);
    }

}
