package io.github.belgif.rest.problem.i18n;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.junitpioneer.jupiter.SetSystemProperty;

import io.github.belgif.rest.problem.BadGatewayProblem;
import io.github.belgif.rest.problem.api.ClientProblem;

class I18NTest {

    @AfterEach
    void cleanup() {
        TestLocaleResolver.clear();
        I18N.setEnabled(true);
    }

    @Test
    void enabledByDefault() {
        assertThat(I18N.isEnabled()).isTrue();
    }

    @Test
    void getRequestLocaleDefault() {
        assertThat(I18N.getRequestLocale()).isEqualTo(new Locale("en"));
    }

    @Test
    void setRequestLocale() {
        TestLocaleResolver.setLocale(new Locale("nl", "BE"));
        assertThat(I18N.getRequestLocale()).isEqualTo(new Locale("nl", "BE"));
    }

    @Test
    void getLocalizedMessageFromDefaultBelgifResourceBundle() {
        assertThat(I18N.getLocalizedString("BadGatewayProblem.detail"))
                .isEqualTo("Error in communication with upstream service");
    }

    @Test
    void getLocalizedMessageFromDefaultBelgifResourceBundleNl() {
        TestLocaleResolver.setLocale(new Locale("nl", "BE"));
        assertThat(I18N.getLocalizedString("BadGatewayProblem.detail"))
                .isEqualTo("Probleem in communicatie met upstream service");
    }

    @Test
    void getLocalizedMessageFromDefaultBelgifResourceBundleUnsupportedLanguage() {
        TestLocaleResolver.setLocale(new Locale("es"));
        assertThat(I18N.getLocalizedString("BadGatewayProblem.detail"))
                .isEqualTo("Error in communication with upstream service");
    }

    @Test
    void getLocalizedMessageFromDefaultBelgifResourceBundleWithArgs() {
        assertThat(I18N.getLocalizedString("unknownInput.detail", "test"))
                .isEqualTo("Input test is unknown");
    }

    @Test
    void getLocalizedBelgifProblemDetailMessage() {
        assertThat(I18N.getLocalizedDetail(BadGatewayProblem.class))
                .isEqualTo("Error in communication with upstream service");
    }

    @Test
    void getLocalizedCustomProblemDetailMessage() {
        assertThat(I18N.getLocalizedDetail(CustomProblem.class))
                .isEqualTo("Custom detail");
    }

    @Test
    void getLocalizedMessageFromCustomResourceBundle() {
        assertThat(I18N.getLocalizedString(I18NTest.class, "CustomProblem.detail"))
                .isEqualTo("Custom detail");
    }

    @Test
    @SetSystemProperty(key = I18N.I18N_FLAG, value = "false")
    void disabledViaSystemProperty() {
        I18N.init();
        assertThat(I18N.isEnabled()).isFalse();
        TestLocaleResolver.setLocale(new Locale("nl", "BE"));
        assertThat(I18N.getLocalizedString("BadGatewayProblem.detail"))
                .isEqualTo("Error in communication with upstream service");
    }

    @Test
    @SetEnvironmentVariable(key = I18N.I18N_FLAG, value = "false")
    void disabledViaEnvironmentVariable() {
        I18N.init();
        assertThat(I18N.isEnabled()).isFalse();
        TestLocaleResolver.setLocale(new Locale("nl", "BE"));
        assertThat(I18N.getLocalizedString("BadGatewayProblem.detail"))
                .isEqualTo("Error in communication with upstream service");
    }

    private static class CustomProblem extends ClientProblem {
        private CustomProblem(URI type, String title, int status) {
            super(type, title, status);
        }
    }

}
