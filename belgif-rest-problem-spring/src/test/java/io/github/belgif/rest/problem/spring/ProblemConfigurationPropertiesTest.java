package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class ProblemConfigurationPropertiesTest {

    private final ProblemConfigurationProperties properties = new ProblemConfigurationProperties();

    @Test
    void empty() {
        assertThat(properties.getScanAdditionalProblemPackages()).isEmpty();
    }

    @Test
    void scanAdditionalProblemPackages() {
        List<String> value = Collections.singletonList("com.acme.custom");
        properties.setScanAdditionalProblemPackages(value);
        assertThat(properties.getScanAdditionalProblemPackages()).isEqualTo(value);
    }

}
