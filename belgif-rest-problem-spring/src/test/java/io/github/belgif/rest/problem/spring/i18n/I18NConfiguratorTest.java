package io.github.belgif.rest.problem.spring.i18n;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.i18n.I18N;
import io.github.belgif.rest.problem.spring.ProblemConfigurationProperties;

class I18NConfiguratorTest {

    private final ProblemConfigurationProperties configuration = new ProblemConfigurationProperties();

    @AfterEach
    void cleanup() {
        I18N.setEnabled(true);
    }

    @Test
    void enabledByDefault() {
        new I18NConfigurator(configuration);
        assertThat(I18N.isEnabled()).isTrue();
    }

    @Test
    void disabled() {
        configuration.setI18n(false);
        new I18NConfigurator(configuration);
        assertThat(I18N.isEnabled()).isFalse();
    }

}
