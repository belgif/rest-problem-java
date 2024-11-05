package io.github.belgif.rest.problem.jaxrs.i18n;

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
        ThreadLocalLocaleResolver.clear();
        I18N.setEnabled(true);
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
        ThreadLocalLocaleResolver.setLocale(new Locale("nl", "BE"));
        filter.filter(requestContext, responseContext);
        assertThat(I18N.getRequestLocale()).isEqualTo(new Locale("en"));
    }

    @Test
    void disabledViaInitParam() {
        when(servletContext.getInitParameter(I18N.I18N_FLAG)).thenReturn("false");
        filter.initialize();
        filter.filter(requestContext);
        verifyNoInteractions(requestContext);
    }

}
