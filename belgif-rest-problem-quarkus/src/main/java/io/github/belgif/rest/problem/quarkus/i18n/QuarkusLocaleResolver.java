package io.github.belgif.rest.problem.quarkus.i18n;

import java.util.Locale;

import jakarta.enterprise.inject.spi.CDI;

import io.github.belgif.rest.problem.i18n.LocaleResolver;

/**
 * LocaleResolver implementation that resolves the Locale from the
 * request-scoped LocaleHolder CDI bean.
 */
public class QuarkusLocaleResolver implements LocaleResolver {

    @Override
    public Locale getLocale() {
        return CDI.current().select(LocaleHolder.class).get().getLocale();
    }

}
