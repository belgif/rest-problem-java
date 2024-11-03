package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import io.github.belgif.rest.problem.i18n.I18N;

@ExtendWith(MockitoExtension.class)
class I18NAcceptLanguageFilterTest {

    private final I18NAcceptLanguageFilter filter = new I18NAcceptLanguageFilter();

    @Mock
    private FilterChain filterChain;

    @Test
    void languageRequested() throws ServletException, IOException {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        servletRequest.addHeader("Accept-Language", "nl-BE");
        AtomicReference<Locale> locale = new AtomicReference<>();
        Mockito.doAnswer(invocationOnMock -> {
            locale.set(I18N.getRequestLocale());
            return null;
        }).when(filterChain).doFilter(servletRequest, servletResponse);
        filter.doFilter(servletRequest, servletResponse, filterChain);
        // during the request processing, locale should be nl-BE
        assertThat(locale).hasValue(new Locale("nl", "BE"));
        // after the request processing, locale should be restored to default = en
        assertThat(I18N.getRequestLocale()).isEqualTo(new Locale("en"));
    }

    @Test
    void noLanguageRequested() throws ServletException, IOException {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        AtomicReference<Locale> locale = new AtomicReference<>();
        Mockito.doAnswer(invocationOnMock -> {
            locale.set(I18N.getRequestLocale());
            return null;
        }).when(filterChain).doFilter(servletRequest, servletResponse);
        filter.doFilter(servletRequest, servletResponse, filterChain);
        // during the request processing, locale should be default = en
        assertThat(locale).hasValue(new Locale("en"));
        // after the request processing, locale should be restored to default = en
        assertThat(I18N.getRequestLocale()).isEqualTo(new Locale("en"));
    }

}
