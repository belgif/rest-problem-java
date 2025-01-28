package io.github.belgif.rest.problem.ee.jaxrs.i18n;

import static org.assertj.core.api.Assertions.*;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ThreadLocalLocaleResolverTest {

    private final ThreadLocalLocaleResolver resolver = new ThreadLocalLocaleResolver();

    @AfterEach
    void cleanup() {
        ThreadLocalLocaleResolver.clear();
    }

    @Test
    void defaultLocale() {
        assertThat(resolver.getLocale()).isEqualTo(Locale.ENGLISH);
    }

    @Test
    void setAndGetLocale() {
        Locale locale = Locale.forLanguageTag("nl-BE");
        ThreadLocalLocaleResolver.setLocale(locale);
        assertThat(resolver.getLocale()).isEqualTo(locale);
    }

    @Test
    void clear() {
        Locale locale = Locale.forLanguageTag("nl-BE");
        ThreadLocalLocaleResolver.setLocale(locale);
        ThreadLocalLocaleResolver.clear();
        assertThat(resolver.getLocale()).isEqualTo(Locale.ENGLISH);
    }

}
