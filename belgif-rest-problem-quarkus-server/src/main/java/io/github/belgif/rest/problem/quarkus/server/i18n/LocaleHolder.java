package io.github.belgif.rest.problem.quarkus.server.i18n;

import java.util.Locale;

import jakarta.enterprise.context.RequestScoped;

import io.github.belgif.rest.problem.i18n.I18N;

/**
 * Request-scoped CDI bean to hold the request locale.
 */
@RequestScoped
public class LocaleHolder {

    private Locale locale = I18N.DEFAULT_LOCALE;

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

}
