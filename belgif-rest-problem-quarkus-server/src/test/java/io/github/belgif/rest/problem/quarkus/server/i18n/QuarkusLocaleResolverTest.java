package io.github.belgif.rest.problem.quarkus.server.i18n;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.i18n.I18N;
import io.quarkus.arc.Arc;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class QuarkusLocaleResolverTest {

    private final QuarkusLocaleResolver localeResolver = new QuarkusLocaleResolver();

    @InjectMock
    private LocaleHolder localeHolder;

    @BeforeEach
    void activateRequestContext() {
        // By default active during tests, explicitly activate it to ignore test execution order
        Arc.container().requestContext().activate();
    }

    @Test
    void getLocale() {
        when(localeHolder.getLocale()).thenReturn(Locale.forLanguageTag("nl-BE"));
        assertThat(localeResolver.getLocale()).isEqualTo(Locale.forLanguageTag("nl-BE"));
    }

    @Test
    void getLocaleRequestScopeNotActive() {
        Arc.container().requestContext().deactivate();
        assertThat(localeResolver.getLocale()).isEqualTo(I18N.DEFAULT_LOCALE);
    }

}
