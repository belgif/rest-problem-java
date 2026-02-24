package io.github.belgif.rest.problem.spring.client;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

class AbstractProblemRestClientCustomizerTest {

    @Test
    void customize() {
        ProblemResponseErrorHandler handler = mock(ProblemResponseErrorHandler.class);
        AbstractProblemRestClientCustomizer customizer = new AbstractProblemRestClientCustomizer(handler) {
        };
        RestClient.Builder builder = RestClient.builder();
        customizer.customize(builder);

        List<?> statusHandlers = (List<?>) ReflectionTestUtils.getField(builder, "statusHandlers");
        assertThat(statusHandlers).hasSize(1);
    }

}
