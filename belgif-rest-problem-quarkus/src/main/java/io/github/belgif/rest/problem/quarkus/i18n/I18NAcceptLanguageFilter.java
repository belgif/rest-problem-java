package io.github.belgif.rest.problem.quarkus.i18n;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;

import io.github.belgif.rest.problem.i18n.I18N;

/**
 * Filter that registers the requested locale, as specified in Accept-Language HTTP header,
 * with the {@link LocaleHolder} @RequestScoped CDI bean.
 */
@PreMatching
@Provider
public class I18NAcceptLanguageFilter implements ContainerRequestFilter {

    @Inject
    private LocaleHolder localeHolder;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (I18N.isEnabled() && !requestContext.getAcceptableLanguages().isEmpty()) {
            localeHolder.setLocale(requestContext.getAcceptableLanguages().get(0));
        }
    }

}
