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
        new QuarkusProblemConfigurator(Optional.of(true), Optional.of(true), Optional.of(true), Optional.of(true));
        assertThat(ProblemConfig.isI18nEnabled()).isTrue();
        assertThat(ProblemConfig.isStackTraceEnabled()).isTrue();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isTrue();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isTrue();
    }

    @Test
    void allDisabled() {
        new QuarkusProblemConfigurator(Optional.of(false), Optional.of(false), Optional.of(false), Optional.of(false));
        assertThat(ProblemConfig.isI18nEnabled()).isFalse();
        assertThat(ProblemConfig.isStackTraceEnabled()).isFalse();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isFalse();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isFalse();
    }

    @Test
    void notConfigured() {
        boolean i18nEnabledBefore = ProblemConfig.isI18nEnabled();
        boolean stackTraceEnabledBefore = ProblemConfig.isStackTraceEnabled();
        boolean extIssueTypesEnabledBefore = ProblemConfig.isExtIssueTypesEnabled();
        boolean extInputsArrayEnabledBefore = ProblemConfig.isExtInputsArrayEnabled();
        new QuarkusProblemConfigurator(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertThat(ProblemConfig.isI18nEnabled()).isEqualTo(i18nEnabledBefore);
        assertThat(ProblemConfig.isStackTraceEnabled()).isEqualTo(stackTraceEnabledBefore);
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isEqualTo(extIssueTypesEnabledBefore);
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isEqualTo(extInputsArrayEnabledBefore);
    }

}
