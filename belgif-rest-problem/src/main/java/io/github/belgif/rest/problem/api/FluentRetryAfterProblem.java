package io.github.belgif.rest.problem.api;

import java.time.OffsetDateTime;

/**
 * Provides default methods with fluent RetryAfter problem properties (retryAfter, retryAfterSec).
 *
 * @param <SELF> the concrete RetryAfter Problem self-type
 */
@SuppressWarnings("unchecked")
public interface FluentRetryAfterProblem<SELF extends Problem & RetryAfter & FluentRetryAfterProblem<SELF>>
        extends FluentProblem<SELF> {

    void setRetryAfter(OffsetDateTime retryAfter);

    void setRetryAfterSec(Long retryAfterSec);

    default SELF retryAfter(OffsetDateTime retryAfter) {
        setRetryAfter(retryAfter);
        return (SELF) this;
    }

    default SELF retryAfterSec(Long retryAfterSec) {
        setRetryAfterSec(retryAfterSec);
        return (SELF) this;
    }

}
