package io.github.belgif.rest.problem.api;

import java.net.URI;
import java.time.OffsetDateTime;

/**
 * Abstract base class for client problems with retryAfter/retryAfterSec.
 */
public abstract class RetryAfterClientProblem extends ClientProblem implements RetryAfter {

    private static final long serialVersionUID = 1L;

    private OffsetDateTime retryAfter;

    private Long retryAfterSec;

    protected RetryAfterClientProblem(URI type, String title, int status) {
        super(type, title, status);
    }

    protected RetryAfterClientProblem(URI type, URI href, String title, int status) {
        super(type, href, title, status);
    }

    protected RetryAfterClientProblem(URI type, String title, int status, Throwable cause) {
        super(type, title, status, cause);
    }

    protected RetryAfterClientProblem(URI type, URI href, String title, int status, Throwable cause) {
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
