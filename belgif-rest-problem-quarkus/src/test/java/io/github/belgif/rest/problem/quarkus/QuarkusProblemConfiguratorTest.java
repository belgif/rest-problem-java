package io.github.belgif.rest.problem.quarkus;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.config.ProblemConfig;

class QuarkusProblemConfiguratorTest {

    @BeforeEach
    @AfterEach
    void cleanup() {
        ProblemConfig.reset();
    }

    @Test
    void allEnabled() {
        new QuarkusProblemConfigurator(true, true, true);
        assertThat(ProblemConfig.isI18nEnabled()).isTrue();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isTrue();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isTrue();
    }

    @Test
    void allDisabled() {
        new QuarkusProblemConfigurator(false, false, false);
        assertThat(ProblemConfig.isI18nEnabled()).isFalse();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isFalse();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isFalse();
    }

}
