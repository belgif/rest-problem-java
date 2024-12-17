package io.github.belgif.rest.problem.jaxrs.i18n;

import java.util.Locale;

import io.github.belgif.rest.problem.i18n.I18N;
import io.github.belgif.rest.problem.i18n.LocaleResolver;

/**
 * LocaleResolver implementation that uses a ThreadLocal.
 */
public class ThreadLocalLocaleResolver implements LocaleResolver {

    /**
     * ThreadLocal with the locale of the current request (defaults to English).
     */
    private static final ThreadLocal<Locale> LOCALE = new InheritableThreadLocal<Locale>() {
        @Override
        protected Locale initialValue() {
            return I18N.DEFAULT_LOCALE;
        }
    };

    @Override
    public Locale getLocale() {
        return LOCALE.get();
    }

    /**
     * Set the locale of the current request.
     *
     * <p>
     * NOTE: The locale is stored in a ThreadLocal that must be cleaned up with {@link #clear()}.
     * </p>
     *
     * @param locale the locale of the current request.
     */
    public static void setLocale(Locale locale) {
        LOCALE.set(locale);
    }

    /**
     * Clear the locale of the current request.
     */
    public static void clear() {
        LOCALE.remove();
    }

}
