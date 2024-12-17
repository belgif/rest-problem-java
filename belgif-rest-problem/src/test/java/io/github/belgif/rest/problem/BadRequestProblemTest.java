package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.api.InvalidParam;
import io.github.belgif.rest.problem.api.ProblemType;

class BadRequestProblemTest {

    @Test
    void construct() {
        BadRequestProblem problem = new BadRequestProblem();
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:badRequest");
        assertThat(problem.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/problems/badRequest.html");
        assertThat(problem.getTitle()).isEqualTo("Bad Request");
        assertThat(problem.getStatus()).isEqualTo(400);
    }

    @Test
    void constructWithIssue() {
        InputValidationIssue issue = new InputValidationIssue(InEnum.QUERY, "test");
        BadRequestProblem problem = new BadRequestProblem(issue);
        assertThat(problem.getDetail()).isEqualTo("The input message is incorrect");
        assertThat(problem.getIssues()).isUnmodifiable().containsExactly(issue);
    }

    @Test
    void constructWithIssues() {
        InputValidationIssue issue = new InputValidationIssue(InEnum.QUERY, "test");
        BadRequestProblem problem = new BadRequestProblem(Collections.singletonList(issue));
        assertThat(problem.getDetail()).isEqualTo("The input message is incorrect");
        assertThat(problem.getIssues()).isUnmodifiable().containsExactly(issue);
    }

    @Test
    void constructWithInvalidParam() {
        InvalidParam invalidParam = new InvalidParam(InEnum.QUERY, "test", "value");
        BadRequestProblem problem = new BadRequestProblem(invalidParam);
        assertThat(problem.getDetail()).isEqualTo("Invalid parameter 'test' [value]");
        assertThat(problem.getInvalidParams()).isEmpty();
        assertThat(problem.getIssues()).hasSize(1);
        InputValidationIssue issue = problem.getIssues().get(0);
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("value");
    }

    @Test
    void constructWithDetailInvalidParam() {
        InvalidParam invalidParam = new InvalidParam(InEnum.QUERY, "test", "value");
        BadRequestProblem problem = new BadRequestProblem("detail", invalidParam);
        assertThat(problem.getDetail()).isEqualTo("detail");
        assertThat(problem.getInvalidParams()).isEmpty();
        assertThat(problem.getIssues()).hasSize(1);
        InputValidationIssue issue = problem.getIssues().get(0);
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("value");
    }

    @Test
    void problemTypeAnnotation() {
        assertThat(BadRequestProblem.class).hasAnnotation(ProblemType.class);
        assertThat(BadRequestProblem.class.getAnnotation(ProblemType.class).value())
                .isEqualTo("urn:problem-type:belgif:badRequest");
    }

    @Test
    void fluentProperties() {
        BadRequestProblem problem = new BadRequestProblem()
                .detail("detail")
                .issues(InputValidationIssues.requiredInput(InEnum.QUERY, "test"))
                .href(URI.create("href"))
                .instance(URI.create("instance"))
                .additionalProperty("foo", "bar");
        assertThat(problem.getDetail()).isEqualTo("detail");
        assertThat(problem.getHref()).hasToString("href");
        assertThat(problem.getInstance()).hasToString("instance");
        assertThat(problem.getAdditionalProperties()).containsEntry("foo", "bar");
        assertThat(problem.getIssues()).containsExactly(InputValidationIssues.requiredInput(InEnum.QUERY, "test"));
    }

    @Test
    void equalsAndHashCode() {
        InputValidationIssue issue = new InputValidationIssue(InEnum.QUERY, "test");
        InputValidationIssue otherIssue = new InputValidationIssue(InEnum.QUERY, "other");
        BadRequestProblem problem = new BadRequestProblem(issue);
        BadRequestProblem equal = new BadRequestProblem(issue);
        BadRequestProblem other = new BadRequestProblem(otherIssue);
        BadRequestProblem otherBis = new BadRequestProblem();
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
