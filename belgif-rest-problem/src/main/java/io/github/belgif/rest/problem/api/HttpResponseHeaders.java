package io.github.belgif.rest.problem.api;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Interface implemented by problems that add HTTP response headers.
 */
public interface HttpResponseHeaders {

    /**
     * The Retry-After HTTP header.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Retry-After">Retry-After</a>
     */
    String RETRY_AFTER = "Retry-After";

    /**
     * The WWW-Authenticate HTTP header.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/WWW-Authenticate">WWW-Authenticate</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6750#section-3">rfc6750#section-3</a>
     */
    String WWW_AUTHENTICATE = "WWW-Authenticate";

    /**
     * Return the HTTP response headers.
     *
     * @return the HTTP response headers
     */
    @JsonIgnore
    Map<String, Object> getHttpResponseHeaders();

}
