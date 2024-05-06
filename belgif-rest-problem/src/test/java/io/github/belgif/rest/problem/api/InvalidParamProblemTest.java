package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.Collections;

import org.junit.jupiter.api.Test;

class InvalidParamProblemTest {

    static class MyInvalidParamProblem extends InvalidParamProblem {
        MyInvalidParamProblem() {
            super(URI.create("urn:problem-type:belgif:test"), "Title", 499);
        }
    }

    @Test
    void constructTypeTitleStatus() {
        InvalidParamProblem problem = new InvalidParamProblem(URI.create("urn:problem-type:belgif:test"),
                "Title", 499) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(499);
    }

    @Test
    void constructTypeHrefTitleStatus() {
        InvalidParamProblem problem = new InvalidParamProblem(URI.create("urn:problem-type:belgif:test"),
                URI.create("https://www.belgif.be"), "Title", 499) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getHref()).hasToString("https://www.belgif.be");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(499);
    }

    @Test
    void constructTypeTitleStatusCause() {
        Throwable cause = new IllegalStateException("cause");
        InvalidParamProblem problem = new InvalidParamProblem(URI.create("urn:problem-type:belgif:test"),
                "Title", 499, cause) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(499);
        assertThat(problem.getCause()).isEqualTo(cause);
    }

    @Test
    void constructTypeHrefTitleStatusCause() {
        Throwable cause = new IllegalStateException("cause");
        InvalidParamProblem problem = new InvalidParamProblem(URI.create("urn:problem-type:belgif:test"),
                URI.create("https://www.belgif.be"), "Title", 499, cause) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getHref()).hasToString("https://www.belgif.be");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(499);
        assertThat(problem.getCause()).isEqualTo(cause);
    }

    @Test
    void invalidParams() {
        InvalidParam invalidParam = new InvalidParam(InEnum.QUERY, "test");
        InvalidParamProblem problem = new MyInvalidParamProblem();
        problem.setInvalidParams(Collections.singletonList(invalidParam));
        assertThat(problem.getInvalidParams()).isUnmodifiable().containsExactly(invalidParam);
    }

    @Test
    void invalidParamsVarargs() {
        InvalidParam invalidParam = new InvalidParam(InEnum.QUERY, "test");
        InvalidParamProblem problem = new MyInvalidParamProblem();
        problem.setInvalidParams(invalidParam);
        assertThat(problem.getInvalidParams()).isUnmodifiable().containsExactly(invalidParam);
    }

    @Test
    void addInvalidParam() {
        InvalidParam invalidParam = new InvalidParam(InEnum.QUERY, "test");
        InvalidParamProblem problem = new MyInvalidParamProblem();
        problem.addInvalidParam(invalidParam);
        assertThat(problem.getInvalidParams()).isUnmodifiable().containsExactly(invalidParam);
    }

    @Test
    void equalsHashCodeToString() {
        InvalidParam invalidParam = new InvalidParam(InEnum.QUERY, "test");
        InvalidParam otherInvalidParam = new InvalidParam(InEnum.QUERY, "other");
        InvalidParamProblem problem = new MyInvalidParamProblem();
        problem.addInvalidParam(invalidParam);
        InvalidParamProblem equal = new MyInvalidParamProblem();
        equal.addInvalidParam(invalidParam);
        InvalidParamProblem other = new MyInvalidParamProblem();
        other.addInvalidParam(otherInvalidParam);
        InvalidParamProblem otherBis = new MyInvalidParamProblem();
        otherBis.setDetail("other");

        assertThat(problem).isEqualTo(problem);
        assertThat(problem).hasSameHashCodeAs(problem);
        assertThat(problem).isEqualTo(equal);
        assertThat(problem).hasSameHashCodeAs(equal);
        assertThat(problem).hasToString(equal.toString());
        assertThat(problem).isNotEqualTo(other);
        assertThat(problem).isNotEqualTo(otherBis);
        assertThat(problem).doesNotHaveSameHashCodeAs(other);
        assertThat(problem).isNotEqualTo("other type");
    }

}
