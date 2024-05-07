package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.Test;

class ServerProblemTest {

    @Test
    void constructWithTypeTitleStatus() {
        ServerProblem problem = new ServerProblem(URI.create("urn:problem-type:belgif:test"), "Title", 599) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(599);
    }

    @Test
    void constructWithTypeTitleStatusCause() {
        Throwable cause = new IllegalStateException("cause");
        ServerProblem problem = new ServerProblem(URI.create("urn:problem-type:belgif:test"), "Title", 599, cause) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(599);
        assertThat(problem).hasCause(cause);
    }

    @Test
    void constructWithTypeHrefTitleStatus() {
        ServerProblem problem = new ServerProblem(URI.create("urn:problem-type:belgif:test"),
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
        ServerProblem problem = new ServerProblem(URI.create("urn:problem-type:belgif:test"),
                URI.create("https://www.belgif.be"), "Title", 599, cause) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getHref()).hasToString("https://www.belgif.be");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(599);
        assertThat(problem).hasCause(cause);
    }

}
