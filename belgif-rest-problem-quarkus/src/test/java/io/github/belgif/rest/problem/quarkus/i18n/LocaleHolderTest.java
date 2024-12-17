package io.github.belgif.rest.problem.quarkus.i18n;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.i18n.I18N;

class LocaleHolderTest {

    private final LocaleHolder holder = new LocaleHolder();

    @Test
    void defaultLocale() {
        assertThat(holder.getLocale()).isEqualTo(I18N.DEFAULT_LOCALE);
    }

    @Test
    void setAndGetLocale() {
        Locale locale = Locale.forLanguageTag("nl-BE");
        holder.setLocale(locale);
        assertThat(holder.getLocale()).isEqualTo(locale);
    }

}
