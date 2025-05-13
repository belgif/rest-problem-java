package io.github.belgif.rest.problem.quarkus;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

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
        new QuarkusProblemConfigurator(Optional.of(true), Optional.of(true), Optional.of(true));
        assertThat(ProblemConfig.isI18nEnabled()).isTrue();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isTrue();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isTrue();
    }

    @Test
    void allDisabled() {
        new QuarkusProblemConfigurator(Optional.of(false), Optional.of(false), Optional.of(false));
        assertThat(ProblemConfig.isI18nEnabled()).isFalse();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isFalse();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isFalse();
    }

    @Test
    void defaults() {
        new QuarkusProblemConfigurator(Optional.empty(), Optional.empty(), Optional.empty());
        assertThat(ProblemConfig.isI18nEnabled()).isEqualTo(ProblemConfig.DEFAULT_I18N_ENABLED);
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isEqualTo(ProblemConfig.DEFAULT_EXT_ISSUE_TYPES_ENABLED);
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isEqualTo(ProblemConfig.DEFAULT_EXT_INPUTS_ARRAY_ENABLED);
    }

}
