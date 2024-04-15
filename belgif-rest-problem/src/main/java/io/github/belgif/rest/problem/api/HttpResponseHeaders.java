package io.github.belgif.rest.problem.api;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Interface implemented by problems that add HTTP response headers.
 */
public interface HttpResponseHeaders {

    /**
     * Return the HTTP response headers.
     *
     * @return the HTTP response headers
     */
    @JsonIgnore
    Map<String, Object> getHttpResponseHeaders();

}
