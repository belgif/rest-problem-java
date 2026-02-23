package io.github.belgif.rest.problem.spring.client;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class AbstractProblemRestTemplateCustomizerTest {

    @Test
    void customize() {
        AbstractProblemResponseErrorHandler handler = mock(AbstractProblemResponseErrorHandler.class);
        AbstractProblemRestTemplateCustomizer customizer = new AbstractProblemRestTemplateCustomizer(handler) {
        };
        RestTemplate restTemplate = new RestTemplate();
        customizer.customize(restTemplate);

        assertThat(restTemplate.getErrorHandler()).isSameAs(handler);
    }

}
