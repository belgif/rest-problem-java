package io.github.belgif.rest.problem.quarkus.deployment.server;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProblemExtensionServerProcessorTest {

    private final ProblemExtensionServerProcessor processor = new ProblemExtensionServerProcessor();

    @Test
    void feature() {
        assertThat(processor.feature().getName()).isEqualTo("belgif-rest-problem-server");
    }

}
