package io.github.belgif.rest.problem.api;

import java.net.URI;
import java.time.OffsetDateTime;

/**
 * Abstract base class for server problems with retryAfter/retryAfterSec.
 */
public abstract class RetryAfterServerProblem extends ServerProblem implements RetryAfter {

    private static final long serialVersionUID = 1L;

    private OffsetDateTime retryAfter;

    private Long retryAfterSec;

    protected RetryAfterServerProblem(URI type, String title, int status) {
        super(type, title, status);
    }

    protected RetryAfterServerProblem(URI type, URI href, String title, int status) {
        super(type, href, title, status);
    }

    protected RetryAfterServerProblem(URI type, String title, int status, Throwable cause) {
        super(type, title, status, cause);
    }

    protected RetryAfterServerProblem(URI type, URI href, String title, int status, Throwable cause) {
        super(type, href, title, status, cause);
    }

    @Override
    public OffsetDateTime getRetryAfter() {
        return retryAfter;
    }

    public void setRetryAfter(OffsetDateTime retryAfter) {
        this.retryAfter = retryAfter;
    }

    @Override
    public Long getRetryAfterSec() {
        return retryAfterSec;
    }

    public void setRetryAfterSec(Long retryAfterSec) {
        this.retryAfterSec = retryAfterSec;
    }

}
