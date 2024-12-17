package io.github.belgif.rest.problem.api;

import java.net.URI;

/**
 * Provides default methods with fluent Problem properties (detail, href, instance, additionalProperty).
 *
 * @param <SELF> the concrete Problem self-type
 */
@SuppressWarnings("unchecked")
public interface FluentProblem<SELF extends Problem & FluentProblem<SELF>> {

    void setDetail(String detail);

    void setHref(URI href);

    void setInstance(URI instance);

    void setAdditionalProperty(String name, Object value);

    default SELF detail(String detail) {
        setDetail(detail);
        return (SELF) this;
    }

    default SELF href(URI href) {
        setHref(href);
        return (SELF) this;
    }

    default SELF instance(URI instance) {
        setInstance(instance);
        return (SELF) this;
    }

    default SELF additionalProperty(String name, Object value) {
        setAdditionalProperty(name, value);
        return (SELF) this;
    }

}
