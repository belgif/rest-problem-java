package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProblemAutoConfigurationTest {

    @Test
    void autoConfiguration() {
        assertThatNoException().isThrownBy(ProblemAutoConfiguration::new);
    }

}
