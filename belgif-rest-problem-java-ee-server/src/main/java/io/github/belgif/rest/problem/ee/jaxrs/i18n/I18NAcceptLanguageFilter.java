package io.github.belgif.rest.problem.ee.jaxrs.i18n;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import io.github.belgif.rest.problem.i18n.I18N;

/**
 * Filter that registers the requested locale, as specified in Accept-Language HTTP header,
 * with the {@link ThreadLocalLocaleResolver} (and clears it afterward).
 */
@PreMatching
@Provider
public class I18NAcceptLanguageFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (I18N.isEnabled() && !requestContext.getAcceptableLanguages().isEmpty()) {
            ThreadLocalLocaleResolver.setLocale(requestContext.getAcceptableLanguages().get(0));
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (I18N.isEnabled()) {
            ThreadLocalLocaleResolver.clear();
        }
    }

}
