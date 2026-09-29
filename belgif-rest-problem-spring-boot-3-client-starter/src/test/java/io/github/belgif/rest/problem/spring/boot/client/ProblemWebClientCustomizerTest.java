package io.github.belgif.rest.problem.spring.boot.client;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
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

        List<ExchangeFilterFunction> filters =
                (List<ExchangeFilterFunction>) ReflectionTestUtils.getField(builder, "filters");
        assertThat(filters).containsExactly(exchangeFilterFunction);
    }

}
