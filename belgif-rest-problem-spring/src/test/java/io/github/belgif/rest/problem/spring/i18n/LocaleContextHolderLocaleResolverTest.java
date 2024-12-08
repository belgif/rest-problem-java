package io.github.belgif.rest.problem.spring.i18n;

import static org.assertj.core.api.Assertions.*;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

class LocaleContextHolderLocaleResolverTest {

    private final LocaleContextHolderLocaleResolver localeResolver = new LocaleContextHolderLocaleResolver();

    @Test
    void getLocale() {
        try {
            Locale locale = Locale.forLanguageTag("nl-BE");
            LocaleContextHolder.setLocale(locale);
            assertThat(localeResolver.getLocale()).isEqualTo(locale);
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }

}
