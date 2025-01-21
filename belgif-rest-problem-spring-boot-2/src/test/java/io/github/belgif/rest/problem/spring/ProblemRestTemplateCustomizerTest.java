package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

class ProblemRestTemplateCustomizerTest {

    @Test
    void customize() {
        ProblemResponseErrorHandler handler = new ProblemResponseErrorHandler(new ObjectMapper());
        ProblemRestTemplateCustomizer customizer = new ProblemRestTemplateCustomizer(handler);
        RestTemplate restTemplate = new RestTemplate();
        customizer.customize(restTemplate);

        assertThat(restTemplate.getErrorHandler()).isSameAs(handler);
    }

}
