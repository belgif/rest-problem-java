package io.github.belgif.rest.problem.jaxrs;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.junitpioneer.jupiter.SetSystemProperty;
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
    private ContainerResponseContext responseContext;

    @Mock
    private ServletContext servletContext;

    @AfterEach
    void cleanup() {
        I18N.clearRequestLocale();
    }

    @Test
    void languageRequested() {
        when(requestContext.getAcceptableLanguages()).thenReturn(Collections.singletonList(new Locale("nl", "BE")));
        filter.filter(requestContext);
        assertThat(I18N.getRequestLocale()).isEqualTo(new Locale("nl", "BE"));
    }

    @Test
    void noLanguageRequested() {
        when(requestContext.getAcceptableLanguages()).thenReturn(Collections.emptyList());
        filter.filter(requestContext);
        assertThat(I18N.getRequestLocale()).isEqualTo(new Locale("en"));
    }

    @Test
    void clearLocaleAfterResponse() {
        I18N.setRequestLocale(new Locale("nl", "BE"));
        filter.filter(requestContext, responseContext);
        assertThat(I18N.getRequestLocale()).isEqualTo(new Locale("en"));
    }

    @Test
    void disabledViaInitParam() {
        when(servletContext.getInitParameter(I18NAcceptLanguageFilter.I18N_FLAG)).thenReturn("false");
        filter.initialize();
        filter.filter(requestContext);
        verifyNoInteractions(requestContext);
    }

    @Test
    @SetSystemProperty(key = I18NAcceptLanguageFilter.I18N_FLAG, value = "false")
    void disabledViaSystemProperty() {
        when(servletContext.getInitParameter(I18NAcceptLanguageFilter.I18N_FLAG)).thenReturn(null);
        filter.initialize();
        filter.filter(requestContext);
        verifyNoInteractions(requestContext);
    }

    @Test
    @SetEnvironmentVariable(key = I18NAcceptLanguageFilter.I18N_FLAG, value = "false")
    void disabledViaEnvironmentVariable() {
        when(servletContext.getInitParameter(I18NAcceptLanguageFilter.I18N_FLAG)).thenReturn(null);
        filter.initialize();
        filter.filter(requestContext);
        verifyNoInteractions(requestContext);
    }

}
