package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;

import java.time.OffsetDateTime;
import java.util.Date;

import org.junit.jupiter.api.Test;

class RetryAfterTest {

    private static class RetryAfterProblem implements RetryAfter {
        private final OffsetDateTime retryAfter;
        private final Long retryAfterSec;

        private RetryAfterProblem(OffsetDateTime retryAfter, Long retryAfterSec) {
            this.retryAfter = retryAfter;
            this.retryAfterSec = retryAfterSec;
        }

        @Override
        public OffsetDateTime getRetryAfter() {
            return retryAfter;
        }

        @Override
        public Long getRetryAfterSec() {
            return retryAfterSec;
        }
    }

    @Test
    void getHttpHeadersEmpty() {
        RetryAfter tested = new RetryAfterProblem(null, null);
        assertThat(tested.getHttpResponseHeaders()).isEmpty();
    }

    @Test
    void getHttpHeadersRetryAfterDate() {
        RetryAfter tested = new RetryAfterProblem(OffsetDateTime.now(), null);
        assertThat(tested.getHttpResponseHeaders()).containsExactly(
                entry(RetryAfter.RETRY_AFTER, Date.from(tested.getRetryAfter().toInstant())));
    }

    @Test
    void getHttpHeadersRetryAfterSec() {
        RetryAfter tested = new RetryAfterProblem(null, 12345L);
        assertThat(tested.getHttpResponseHeaders()).containsExactly(
                entry(RetryAfter.RETRY_AFTER, 12345L));
    }

    @Test
    void getHttpHeadersRetryAfterDateAndSec() {
        RetryAfter tested = new RetryAfterProblem(OffsetDateTime.now(), 12345L);
        assertThat(tested.getHttpResponseHeaders()).containsExactly(
                entry(RetryAfter.RETRY_AFTER, Date.from(tested.getRetryAfter().toInstant())));
    }

}
