package io.github.belgif.rest.problem.quarkus.i18n;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.i18n.I18N;

class I18NConfiguratorTest {

    @AfterEach
    void cleanup() {
        I18N.setEnabled(true);
    }

    @Test
    void enabled() {
        I18N.setEnabled(false);
        new I18NConfigurator(true);
        assertThat(I18N.isEnabled()).isTrue();
    }

    @Test
    void disabled() {
        I18N.setEnabled(true);
        new I18NConfigurator(false);
        assertThat(I18N.isEnabled()).isFalse();
    }

}
