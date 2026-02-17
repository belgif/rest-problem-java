package io.github.belgif.rest.problem.quarkus.server.i18n;

import java.util.Locale;

import jakarta.enterprise.inject.spi.CDI;

import io.github.belgif.rest.problem.i18n.I18N;
import io.github.belgif.rest.problem.i18n.LocaleResolver;
import io.quarkus.arc.Arc;

/**
 * LocaleResolver implementation that resolves the Locale from the
 * request-scoped LocaleHolder CDI bean.
 */
public class QuarkusLocaleResolver implements LocaleResolver {

    @Override
    public Locale getLocale() {
        // Workaround for https://github.com/belgif/rest-problem-java/issues/221
        // RequestScope is not active in io.github.belgif.rest.problem.ee.jaxrs.client.ProblemResponseExceptionMapper,
        // but in fact I18N is not really needed on the client side anyway.
        if (Arc.container().requestContext().isActive()) {
            return CDI.current().select(LocaleHolder.class).get().getLocale();
        } else {
            return I18N.DEFAULT_LOCALE;
        }
    }

}
