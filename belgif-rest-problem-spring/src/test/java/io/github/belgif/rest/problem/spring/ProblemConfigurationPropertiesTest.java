package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.config.ProblemConfig;

class ProblemConfigurationPropertiesTest {

    private final ProblemConfigurationProperties properties = new ProblemConfigurationProperties();

    @BeforeEach
    @AfterEach
    void cleanup() {
        ProblemConfig.reset();
    }

    @Test
    void empty() {
        properties.afterPropertiesSet();
        assertThat(properties.getScanAdditionalProblemPackages()).isEmpty();
        assertThat(ProblemConfig.isI18nEnabled()).isEqualTo(ProblemConfig.DEFAULT_I18N);
    }

    @Test
    void scanAdditionalProblemPackages() {
        List<String> value = Collections.singletonList("com.acme.custom");
        properties.setScanAdditionalProblemPackages(value);
        assertThat(properties.getScanAdditionalProblemPackages()).isEqualTo(value);
    }

    @Test
    void i18nEnabled() {
        properties.setI18n(true);
        properties.afterPropertiesSet();
        assertThat(ProblemConfig.isI18nEnabled()).isTrue();
    }

    @Test
    void i18nDisabled() {
        properties.setI18n(false);
        properties.afterPropertiesSet();
        assertThat(ProblemConfig.isI18nEnabled()).isFalse();
    }

}
