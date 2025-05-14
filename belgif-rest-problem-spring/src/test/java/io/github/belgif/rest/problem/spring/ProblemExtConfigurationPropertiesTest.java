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
        boolean extIssueTypesEnabledBefore = ProblemConfig.isExtIssueTypesEnabled();
        boolean extInputsArrayEnabledBefore = ProblemConfig.isExtInputsArrayEnabled();
        properties.afterPropertiesSet();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isEqualTo(extIssueTypesEnabledBefore);
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isEqualTo(extInputsArrayEnabledBefore);
    }

    @Test
    void extIssueTypesEnabled() {
        properties.setIssueTypesEnabled(true);
        properties.afterPropertiesSet();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isTrue();
    }

    @Test
    void extIssueTypesDisabled() {
        properties.setIssueTypesEnabled(false);
        properties.afterPropertiesSet();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isFalse();
    }

    @Test
    void extInputsArrayEnabled() {
        properties.setInputsArrayEnabled(true);
        properties.afterPropertiesSet();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isTrue();
    }

    @Test
    void extInputsArrayDisabled() {
        properties.setInputsArrayEnabled(false);
        properties.afterPropertiesSet();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isFalse();
    }

}
