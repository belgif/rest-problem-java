package io.github.belgif.rest.problem.jaxrs;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import io.github.belgif.rest.problem.i18n.I18N;

/**
 * Filter that registers the requested locale, as specified in Accept-Language HTTP header,
 * with the I18N helper (and clears it afterward).
 */
@PreMatching
@Provider
public class I18NAcceptLanguageFilter implements ContainerRequestFilter, ContainerResponseFilter {

    public static final String I18N_FLAG = "io.github.belgif.rest.problem.i18n";

    @Context
    private ServletContext servletContext;

    private boolean enabled = true;

    @PostConstruct
    public void initialize() {
        if (servletContext.getInitParameter(I18N_FLAG) != null) {
            enabled = Boolean.parseBoolean(servletContext.getInitParameter(I18N_FLAG));
        } else if (System.getProperty(I18N_FLAG) != null) {
            enabled = Boolean.parseBoolean(System.getProperty(I18N_FLAG));
        } else if (System.getenv(I18N_FLAG) != null) {
            enabled = Boolean.parseBoolean(System.getenv(I18N_FLAG));
        } else {
            enabled = true;
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (enabled && !requestContext.getAcceptableLanguages().isEmpty()) {
            I18N.setRequestLocale(requestContext.getAcceptableLanguages().get(0));
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (enabled) {
            I18N.clearRequestLocale();
        }
    }

    protected void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
