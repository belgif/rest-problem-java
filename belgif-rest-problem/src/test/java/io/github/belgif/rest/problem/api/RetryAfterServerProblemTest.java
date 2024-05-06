package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Date;

import org.junit.jupiter.api.Test;

class RetryAfterServerProblemTest {

    @Test
    void constructWithTypeTitleStatus() {
        RetryAfterServerProblem problem =
                new RetryAfterServerProblem(URI.create("urn:problem-type:belgif:test"), "Title", 599) {
                };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(599);
    }

    @Test
    void constructWithTypeTitleStatusCause() {
        Throwable cause = new IllegalStateException("cause");
        RetryAfterServerProblem problem =
                new RetryAfterServerProblem(URI.create("urn:problem-type:belgif:test"), "Title", 599, cause) {
                };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(599);
        assertThat(problem).hasCause(cause);
    }

    @Test
    void constructWithTypeHrefTitleStatus() {
        RetryAfterServerProblem problem = new RetryAfterServerProblem(URI.create("urn:problem-type:belgif:test"),
                URI.create("https://www.belgif.be"), "Title", 599) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getHref()).hasToString("https://www.belgif.be");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(599);
    }

    @Test
    void constructWithTypeHrefTitleStatusCause() {
        Throwable cause = new IllegalStateException("cause");
        RetryAfterServerProblem problem = new RetryAfterServerProblem(URI.create("urn:problem-type:belgif:test"),
                URI.create("https://www.belgif.be"), "Title", 599, cause) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getHref()).hasToString("https://www.belgif.be");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(599);
        assertThat(problem).hasCause(cause);
    }

    @Test
    void retryAfterOffsetDateTime() {
        RetryAfterServerProblem problem =
                new RetryAfterServerProblem(URI.create("urn:problem-type:belgif:test"), "Title", 599) {
                };
        OffsetDateTime retryAfter = OffsetDateTime.now();
        problem.setRetryAfter(retryAfter);
        assertThat(problem.getRetryAfter()).isEqualTo(retryAfter);
        assertThat(problem.getHttpResponseHeaders()).containsExactly(
                entry(RetryAfter.RETRY_AFTER, Date.from(retryAfter.toInstant())));
    }

    @Test
    void retryAfterSec() {
        RetryAfterServerProblem problem =
                new RetryAfterServerProblem(URI.create("urn:problem-type:belgif:test"), "Title", 599) {
                };
        problem.setRetryAfterSec(10L);
        assertThat(problem.getRetryAfterSec()).isEqualTo(10L);
        assertThat(problem.getHttpResponseHeaders()).containsExactly(
                entry(RetryAfter.RETRY_AFTER, 10L));
    }

    @Test
    void equalsAndHashCode() {
        class TestRetryAfterServerProblem extends RetryAfterServerProblem {
            TestRetryAfterServerProblem() {
                super(URI.create("urn:problem-type:belgif:test"), "Title", 599);
            }
        }
        RetryAfterServerProblem problem = new TestRetryAfterServerProblem();
        problem.setRetryAfterSec(10L);
        RetryAfterServerProblem equal = new TestRetryAfterServerProblem();
        equal.setRetryAfterSec(10L);
        RetryAfterServerProblem other = new TestRetryAfterServerProblem();
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
