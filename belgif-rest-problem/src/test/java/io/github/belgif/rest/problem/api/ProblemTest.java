package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.config.ProblemConfig;

class ProblemTest {

    static class MyProblem extends Problem {
        private MyProblem() {
            super(URI.create("urn:problem-type:belgif:test"), "Title", 599);
        }
    }

    @Test
    void constructWithTypeTitleStatus() {
        Problem problem = new Problem(URI.create("urn:problem-type:belgif:test"), "Title", 599) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(599);
    }

    @Test
    void constructWithTypeTitleStatusCause() {
        Throwable cause = new IllegalStateException("cause");
        Problem problem = new Problem(URI.create("urn:problem-type:belgif:test"), "Title", 599, cause) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(599);
        assertThat(problem).hasCause(cause);
    }

    @Test
    void constructWithTypeHrefTitleStatus() {
        Problem problem = new Problem(URI.create("urn:problem-type:belgif:test"),
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
        Problem problem = new Problem(URI.create("urn:problem-type:belgif:test"),
                URI.create("https://www.belgif.be"), "Title", 599, cause) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getHref()).hasToString("https://www.belgif.be");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(599);
        assertThat(problem).hasCause(cause);
    }

    @Test
    void href() {
        Problem problem = new MyProblem();
        problem.setHref(URI.create("https://www.belgif.be"));
        assertThat(problem.getHref()).hasToString("https://www.belgif.be");
    }

    @Test
    void instance() {
        Problem problem = new MyProblem();
        problem.setInstance(URI.create("instance"));
        assertThat(problem.getInstance()).hasToString("instance");
    }

    @Test
    void additionalProperties() {
        Problem problem = new MyProblem();
        problem.setAdditionalProperty("key", "value");
        assertThat(problem.getAdditionalProperties()).containsEntry("key", "value");
    }

    @Test
    void additionalPropertiesIgnoresReadOnly() {
        Problem problem = new MyProblem();
        problem.setAdditionalProperty("type", "type");
        problem.setAdditionalProperty("status", "status");
        problem.setAdditionalProperty("title", "title");
        assertThat(problem.getAdditionalProperties()).isEmpty();
    }

    @Test
    void equalsAndHashCode() {
        Problem problem = new MyProblem();
        problem.setDetail("detail");
        Problem equal = new MyProblem();
        equal.setDetail("detail");
        Problem other = new MyProblem();
        other.setDetail("other");

        assertThat(problem).isEqualTo(problem);
        assertThat(problem).hasSameHashCodeAs(problem);
        assertThat(problem).isEqualTo(equal);
        assertThat(problem).hasSameHashCodeAs(equal);
        assertThat(problem).hasToString(equal.toString());
        assertThat(problem).isNotEqualTo(other);
        assertThat(problem).doesNotHaveSameHashCodeAs(other);
        assertThat(problem).isNotEqualTo("other type");
    }

    @Test
    void getMessage() {
        Problem problem = new MyProblem();
        assertThat(problem.getMessage()).isEqualTo("Title");
        problem.setDetail("detail");
        assertThat(problem.getMessage()).isEqualTo("Title: detail");
    }

    @Test
    void stackTraceDisabled() {
        try {
            ProblemConfig.setStackTraceEnabled(false);
            Problem problem = new MyProblem();
            assertThat(problem.getStackTrace()).isEmpty();
        } finally {
            ProblemConfig.reset();
        }
    }

    @Test
    void stackTraceEnabled() {
        try {
            ProblemConfig.setStackTraceEnabled(true);
            Problem problem = new MyProblem();
            assertThat(problem.getStackTrace()).isNotEmpty();
        } finally {
            ProblemConfig.reset();
        }
    }

}
