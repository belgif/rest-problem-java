package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.config.ProblemConfig;

class ProblemExtConfigurationPropertiesTest {

    private final ProblemExtConfigurationProperties properties = new ProblemExtConfigurationProperties();

    @BeforeEach
    @AfterEach
    void cleanup() {
        ProblemConfig.reset();
    }

    @Test
    void empty() {
        properties.afterPropertiesSet();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isEqualTo(ProblemConfig.DEFAULT_EXT_ISSUE_TYPES);
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isEqualTo(ProblemConfig.DEFAULT_EXT_INPUTS_ARRAY);
    }

    @Test
    void extIssueTypesEnabled() {
        properties.setIssueTypes(true);
        properties.afterPropertiesSet();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isTrue();
    }

    @Test
    void extIssueTypesDisabled() {
        properties.setIssueTypes(false);
        properties.afterPropertiesSet();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isFalse();
    }

    @Test
    void extInputsArrayEnabled() {
        properties.setInputsArray(true);
        properties.afterPropertiesSet();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isTrue();
    }

    @Test
    void extInputsArrayDisabled() {
        properties.setInputsArray(false);
        properties.afterPropertiesSet();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isFalse();
    }

}
