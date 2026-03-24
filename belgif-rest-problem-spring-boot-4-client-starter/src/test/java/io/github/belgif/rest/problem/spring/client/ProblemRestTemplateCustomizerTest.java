package io.github.belgif.rest.problem.spring.client;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class ProblemRestTemplateCustomizerTest {

    @Test
    void customize() {
        ProblemResponseErrorHandler handler = mock(ProblemResponseErrorHandler.class);
        ProblemRestTemplateCustomizer customizer = new ProblemRestTemplateCustomizer(handler) {
        };
        RestTemplate restTemplate = new RestTemplate();
        customizer.customize(restTemplate);

        assertThat(restTemplate.getErrorHandler()).isSameAs(handler);
    }

}
