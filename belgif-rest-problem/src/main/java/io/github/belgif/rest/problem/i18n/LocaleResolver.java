package io.github.belgif.rest.problem.i18n;

import java.util.Locale;

/**
 * Interface for pluggable LocaleResolver implementation (via ServiceLoader SPI from file
 * /META-INF/services/io.github.belgif.rest.problem.i18n.LocaleResolver).
 */
public interface LocaleResolver {

    /**
     * Get the current Locale
     *
     * @return the current Locale
     */
    Locale getLocale();

}
