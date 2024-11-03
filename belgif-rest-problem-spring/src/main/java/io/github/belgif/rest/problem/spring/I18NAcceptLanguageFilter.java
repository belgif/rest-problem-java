package io.github.belgif.rest.problem.spring;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestContextUtils;

import io.github.belgif.rest.problem.i18n.I18N;

/**
 * Filter that registers the requested locale, as specified in Accept-Language HTTP header,
 * with the I18N helper (and clears it afterward).
 */
@Component
@ConditionalOnProperty(prefix = "io.github.belgif.rest.problem", name = "i18n", havingValue = "true",
        matchIfMissing = true)
public class I18NAcceptLanguageFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        I18N.setRequestLocale(RequestContextUtils.getLocale((HttpServletRequest) servletRequest));
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            I18N.clearRequestLocale();
        }
    }

}
