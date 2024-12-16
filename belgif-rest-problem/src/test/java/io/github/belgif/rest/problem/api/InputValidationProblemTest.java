package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.Collections;

import org.junit.jupiter.api.Test;

class InputValidationProblemTest {

    static class MyInputValidationProblem extends InputValidationProblem {
        MyInputValidationProblem() {
            super(URI.create("urn:problem-type:belgif:test"), "Title", 499);
        }
    }

    @Test
    void constructWithTypeTitleStatus() {
        InputValidationProblem problem = new InputValidationProblem(URI.create("urn:problem-type:belgif:test"),
                "Title", 499) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(499);
    }

    @Test
    void constructWithTypeHrefTitleStatus() {
        InputValidationProblem problem = new InputValidationProblem(URI.create("urn:problem-type:belgif:test"),
                URI.create("https://www.belgif.be"), "Title", 499) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getHref()).hasToString("https://www.belgif.be");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(499);
    }

    @Test
    void constructWithTypeTitleStatusCause() {
        Throwable cause = new IllegalStateException("cause");
        InputValidationProblem problem = new InputValidationProblem(URI.create("urn:problem-type:belgif:test"),
                "Title", 499, cause) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(499);
        assertThat(problem.getCause()).isEqualTo(cause);
    }

    @Test
    void constructWithTypeHrefTitleStatusCause() {
        Throwable cause = new IllegalStateException("cause");
        InputValidationProblem problem = new InputValidationProblem(URI.create("urn:problem-type:belgif:test"),
                URI.create("https://www.belgif.be"), "Title", 499, cause) {
        };
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:test");
        assertThat(problem.getHref()).hasToString("https://www.belgif.be");
        assertThat(problem.getTitle()).isEqualTo("Title");
        assertThat(problem.getStatus()).isEqualTo(499);
        assertThat(problem.getCause()).isEqualTo(cause);
    }

    @Test
    void issues() {
        InputValidationIssue issue = new InputValidationIssue(InEnum.QUERY, "test");
        InputValidationProblem problem = new MyInputValidationProblem();
        problem.setIssues(Collections.singletonList(issue));
        assertThat(problem.getIssues()).isUnmodifiable().containsExactly(issue);
    }

    @Test
    void issuesVarargs() {
        InputValidationIssue issue = new InputValidationIssue(InEnum.QUERY, "test");
        InputValidationProblem problem = new MyInputValidationProblem();
        problem.setIssues(issue);
        assertThat(problem.getIssues()).isUnmodifiable().containsExactly(issue);
    }

    @Test
    void addIssue() {
        InputValidationIssue issue = new InputValidationIssue(InEnum.QUERY, "test");
        InputValidationProblem problem = new MyInputValidationProblem();
        problem.addIssue(issue);
        assertThat(problem.getIssues()).isUnmodifiable().containsExactly(issue);
    }

    @Test
    void invalidParams() {
        InvalidParam invalidParam = new InvalidParam(InEnum.QUERY, "test");
        InputValidationProblem problem = new MyInputValidationProblem();
        problem.setInvalidParams(Collections.singletonList(invalidParam));
        assertThat(problem.getInvalidParams()).isUnmodifiable().containsExactly(invalidParam);
    }

    @Test
    void invalidParamsVarargs() {
        InvalidParam invalidParam = new InvalidParam(InEnum.QUERY, "test");
        InputValidationProblem problem = new MyInputValidationProblem();
        problem.setInvalidParams(invalidParam);
        assertThat(problem.getInvalidParams()).isUnmodifiable().containsExactly(invalidParam);
    }

    @Test
    void addInvalidParam() {
        InvalidParam invalidParam = new InvalidParam(InEnum.QUERY, "test");
        InputValidationProblem problem = new MyInputValidationProblem();
        problem.addInvalidParam(invalidParam);
        assertThat(problem.getInvalidParams()).isUnmodifiable().containsExactly(invalidParam);
    }

    @Test
    void equalsHashCodeToString() {
        InputValidationIssue issue = new InputValidationIssue(InEnum.QUERY, "test");
        InputValidationIssue otherIssue = new InputValidationIssue(InEnum.QUERY, "other");
        InputValidationProblem problem = new MyInputValidationProblem();
        problem.addIssue(issue);
        InputValidationProblem equal = new MyInputValidationProblem();
        equal.addIssue(issue);
        InputValidationProblem other = new MyInputValidationProblem();
        other.addIssue(otherIssue);
        InputValidationProblem otherBis = new MyInputValidationProblem();
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

    @Test
    void stringRepresentation() {
        InputValidationProblem problem = new MyInputValidationProblem();
        problem.setDetail("detail");
        problem.addIssue(new InputValidationIssue().title("title-only"));
        problem.addIssue(new InputValidationIssue().detail("detail-only"));
        problem.addIssue(new InputValidationIssue().title("title").detail("detail"));
        problem.addIssue(new InputValidationIssue().type("type"));
        assertThat(problem.toString())
                .contains("Title: detail")
                .contains("- title-only")
                .contains("- detail-only")
                .contains("- title: detail")
                .contains("- type");
    }

}
