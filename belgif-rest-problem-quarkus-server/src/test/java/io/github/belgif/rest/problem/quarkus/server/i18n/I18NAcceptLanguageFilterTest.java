package io.github.belgif.rest.problem.quarkus.server.i18n;

import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Locale;

import jakarta.ws.rs.container.ContainerRequestContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.belgif.rest.problem.i18n.I18N;

@ExtendWith(MockitoExtension.class)
class I18NAcceptLanguageFilterTest {

    @InjectMocks
    private I18NAcceptLanguageFilter filter;

    @Mock
    private ContainerRequestContext requestContext;

    @Mock
    private LocaleHolder localeHolder;

    @AfterEach
    void cleanup() {
        I18N.setEnabled(true);
    }

    @Test
    void languageRequested() {
        when(requestContext.getAcceptableLanguages())
                .thenReturn(Collections.singletonList(Locale.forLanguageTag("nl-BE")));
        filter.filter(requestContext);
        verify(localeHolder).setLocale(Locale.forLanguageTag("nl-BE"));
    }

    @Test
    void noLanguageRequested() {
        when(requestContext.getAcceptableLanguages()).thenReturn(Collections.emptyList());
        filter.filter(requestContext);
        verifyNoInteractions(localeHolder);
    }

    @Test
    void i18nDisabled() {
        I18N.setEnabled(false);
        filter.filter(requestContext);
        verifyNoInteractions(requestContext, localeHolder);
    }

}
