package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

class ProblemRestClientCustomizerTest {

    @Test
    void customize() {
        ProblemResponseErrorHandler handler = new ProblemResponseErrorHandler(new ObjectMapper());
        ProblemRestClientCustomizer customizer = new ProblemRestClientCustomizer(handler);
        RestClient.Builder builder = RestClient.builder();
        customizer.customize(builder);

        List<?> statusHandlers = (List<?>) ReflectionTestUtils.getField(builder, "statusHandlers");
        assertThat(statusHandlers).hasSize(1);
    }

}
