package io.github.belgif.rest.problem.i18n;

import java.util.Locale;

public class TestLocaleResolver implements LocaleResolver {

    private static Locale locale = I18N.DEFAULT_LOCALE;

    @Override
    public Locale getLocale() {
        return locale;
    }

    public static void setLocale(Locale locale) {
        TestLocaleResolver.locale = locale;
    }

    public static void clear() {
        TestLocaleResolver.locale = I18N.DEFAULT_LOCALE;
    }

}
