package io.github.belgif.rest.problem.api;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Abstract base class for server problems with retryAfter/retryAfterSec.
 */
public abstract class RetryAfterServerProblem extends ServerProblem implements RetryAfter {

    private static final long serialVersionUID = 1L;

    private OffsetDateTime retryAfter;

    private Long retryAfterSec;

    protected RetryAfterServerProblem(URI type, String title, Integer status) {
        super(type, title, status);
    }

    protected RetryAfterServerProblem(URI type, URI href, String title, Integer status) {
        super(type, href, title, status);
    }

    protected RetryAfterServerProblem(URI type, String title, Integer status, Throwable cause) {
        super(type, title, status, cause);
    }

    protected RetryAfterServerProblem(URI type, URI href, String title, Integer status, Throwable cause) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        RetryAfterServerProblem that = (RetryAfterServerProblem) o;
        return Objects.equals(retryAfter, that.retryAfter) && Objects.equals(retryAfterSec,
                that.retryAfterSec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), retryAfter, retryAfterSec);
    }

}
