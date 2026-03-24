package io.github.belgif.rest.problem.spring.server.i18n;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;

import io.github.belgif.rest.problem.i18n.LocaleResolver;

/**
 * Spring LocaleResolver implementation that delegates to {@link LocaleContextHolder#getLocale()}.
 */
public class LocaleContextHolderLocaleResolver implements LocaleResolver {

    @Override
    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

}
