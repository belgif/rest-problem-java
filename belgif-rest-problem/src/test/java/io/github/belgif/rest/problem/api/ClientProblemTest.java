package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.Test;

class ClientProblemTest {

    @Test
    void constructWithTypeTitleStatus() {
        ClientProblem problem = new ClientProblem(URI.create("urn:problem-type:belgif:test"), "Title", 499) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(499);
    }

    @Test
    void constructWithTypeTitleStatusCause() {
        Throwable cause = new IllegalStateException("cause");
        ClientProblem problem = new ClientProblem(URI.create("urn:problem-type:belgif:test"), "Title", 499, cause) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(499);
        assertThat(problem).hasCause(cause);
    }

    @Test
    void constructWithTypeHrefTitleStatus() {
        ClientProblem problem = new ClientProblem(URI.create("urn:problem-type:belgif:test"),
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
        ClientProblem problem = new ClientProblem(URI.create("urn:problem-type:belgif:test"),
                URI.create("https://www.belgif.be"), "Title", 499, cause) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getHref()).hasToString("https://www.belgif.be");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(499);
        assertThat(problem).hasCause(cause);
    }

}
