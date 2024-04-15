package io.github.belgif.rest.problem.api;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface implemented by problems with retryAfter / retryAfterSec properties.
 *
 * The retry value is also returned in the Retry-After HTTP response header.
 */
public interface RetryAfter extends HttpResponseHeaders {

    /**
     * The Retry-After HTTP header.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Retry-After">Retry-After</a>
     */
    String RETRY_AFTER = "Retry-After";

    OffsetDateTime getRetryAfter();

    Long getRetryAfterSec();

    @Override
    default Map<String, Object> getHttpResponseHeaders() {
        Map<String, Object> httpResponseHeaders = new HashMap<>();
        if (getRetryAfter() != null) {
            // we need to convert the OffsetDateTime to a java.util.Date here in order to have the JAX-RS runtime
            // convert it to a proper https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Date
            httpResponseHeaders.put(RETRY_AFTER, Date.from(getRetryAfter().toInstant()));
        } else if (getRetryAfterSec() != null) {
            httpResponseHeaders.put(RETRY_AFTER, getRetryAfterSec());
        }
        return httpResponseHeaders;
    }

}
