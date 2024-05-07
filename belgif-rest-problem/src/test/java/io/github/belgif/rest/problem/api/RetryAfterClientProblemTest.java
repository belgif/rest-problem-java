package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Date;

import org.junit.jupiter.api.Test;

class RetryAfterClientProblemTest {

    @Test
    void constructWithTypeTitleStatus() {
        RetryAfterClientProblem problem =
                new RetryAfterClientProblem(URI.create("urn:problem-type:belgif:test"), "Title", 499) {
                };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(499);
    }

    @Test
    void constructWithTypeTitleStatusCause() {
        Throwable cause = new IllegalStateException("cause");
        RetryAfterClientProblem problem =
                new RetryAfterClientProblem(URI.create("urn:problem-type:belgif:test"), "Title", 499, cause) {
                };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(499);
        assertThat(problem).hasCause(cause);
    }

    @Test
    void constructWithTypeHrefTitleStatus() {
        RetryAfterClientProblem problem = new RetryAfterClientProblem(URI.create("urn:problem-type:belgif:test"),
                URI.create("https://www.belgif.be"), "Title", 499) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getHref()).hasToString("https://www.belgif.be");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(499);
    }

    @Test
    void constructWithTypeHrefTitleStatusCause() {
        Throwable cause = new IllegalStateException("cause");
        RetryAfterClientProblem problem = new RetryAfterClientProblem(URI.create("urn:problem-type:belgif:test"),
                URI.create("https://www.belgif.be"), "Title", 499, cause) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getHref()).hasToString("https://www.belgif.be");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(499);
        assertThat(problem).hasCause(cause);
    }

    @Test
    void retryAfterOffsetDateTime() {
        RetryAfterClientProblem problem =
                new RetryAfterClientProblem(URI.create("urn:problem-type:belgif:test"), "Title", 499) {
                };
        OffsetDateTime retryAfter = OffsetDateTime.now();
        problem.setRetryAfter(retryAfter);
        assertThat(problem.getRetryAfter()).isEqualTo(retryAfter);
        assertThat(problem.getHttpResponseHeaders()).containsExactly(
                entry(RetryAfter.RETRY_AFTER, Date.from(retryAfter.toInstant())));
    }

    @Test
    void retryAfterSec() {
        RetryAfterClientProblem problem =
                new RetryAfterClientProblem(URI.create("urn:problem-type:belgif:test"), "Title", 499) {
                };
        problem.setRetryAfterSec(10L);
        assertThat(problem.getRetryAfterSec()).isEqualTo(10L);
        assertThat(problem.getHttpResponseHeaders()).containsExactly(
                entry(RetryAfter.RETRY_AFTER, 10L));
    }

    @Test
    void equalsAndHashCode() {
        class TestRetryAfterClientProblem extends RetryAfterClientProblem {
            TestRetryAfterClientProblem() {
                super(URI.create("urn:problem-type:belgif:test"), "Title", 499);
            }
        }
        RetryAfterClientProblem problem = new TestRetryAfterClientProblem();
        problem.setRetryAfterSec(10L);
        RetryAfterClientProblem equal = new TestRetryAfterClientProblem();
        equal.setRetryAfterSec(10L);
        RetryAfterClientProblem other = new TestRetryAfterClientProblem();
        other.setDetail("Detail");
        other.setRetryAfter(OffsetDateTime.now());

        assertThat(problem).isEqualTo(problem);
        assertThat(problem).hasSameHashCodeAs(problem);
        assertThat(problem).isEqualTo(equal);
        assertThat(problem).hasSameHashCodeAs(equal);
        assertThat(problem).isNotEqualTo(other);
        assertThat(problem).doesNotHaveSameHashCodeAs(other);
        assertThat(problem).isNotEqualTo("other type");
    }

}
