package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InvalidParam;
import io.github.belgif.rest.problem.api.ProblemType;

class ResourceNotFoundProblemTest {

    @Test
    void construct() {
        ResourceNotFoundProblem problem = new ResourceNotFoundProblem();
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:resourceNotFound");
        assertThat(problem.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/problems/resourceNotFound.html");
        assertThat(problem.getTitle()).isEqualTo("Resource Not Found");
        assertThat(problem.getStatus()).isEqualTo(404);
    }

    @Test
    void constructWithDetailIssue() {
        InputValidationIssue issue = new InputValidationIssue(InEnum.QUERY, "test");
        ResourceNotFoundProblem problem = new ResourceNotFoundProblem("detail", issue);
        assertThat(problem.getDetail()).isEqualTo("detail");
        assertThat(problem.getIssues()).isUnmodifiable().containsExactly(issue);
    }

    @Test
    void constructWithResourceInNameValue() {
        ResourceNotFoundProblem problem = new ResourceNotFoundProblem("resource", InEnum.QUERY, "name", "value");
        assertThat(problem.getDetail()).isEqualTo("No data found for the resource with name [value]");
        assertThat(problem.getInvalidParams()).isEmpty();
        assertThat(problem.getIssues()).hasSize(1);
        InputValidationIssue issue = problem.getIssues().get(0);
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("name");
        assertThat(issue.getValue()).isEqualTo("value");
    }

    @Test
    void constructWithResourceInvalidParam() {
        InvalidParam invalidParam = new InvalidParam(InEnum.QUERY, "test", "value");
        ResourceNotFoundProblem problem = new ResourceNotFoundProblem("resource", invalidParam);
        assertThat(problem.getDetail()).isEqualTo("No data found for the resource with test [value]");
        assertThat(problem.getInvalidParams()).isEmpty();
        assertThat(problem.getIssues()).hasSize(1);
        InputValidationIssue issue = problem.getIssues().get(0);
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("value");
    }

    @Test
    void invalidParams() {
        InvalidParam invalidParam = new InvalidParam(InEnum.QUERY, "test", "value");
        ResourceNotFoundProblem problem = new ResourceNotFoundProblem();
        problem.setInvalidParams(Collections.singletonList(invalidParam));
        assertThat(problem.getInvalidParams()).isUnmodifiable().containsExactly(invalidParam);
    }

    @Test
    void problemTypeAnnotation() {
        assertThat(ResourceNotFoundProblem.class).hasAnnotation(ProblemType.class);
        assertThat(ResourceNotFoundProblem.class.getAnnotation(ProblemType.class).value())
                .isEqualTo("urn:problem-type:belgif:resourceNotFound");
    }

    @Test
    void equalsAndHashCode() {
        InputValidationIssue issue = new InputValidationIssue(InEnum.QUERY, "test");
        InputValidationIssue otherIssue = new InputValidationIssue(InEnum.QUERY, "other");
        ResourceNotFoundProblem problem = new ResourceNotFoundProblem("detail", issue);
        ResourceNotFoundProblem equal = new ResourceNotFoundProblem("detail", issue);
        ResourceNotFoundProblem other = new ResourceNotFoundProblem("detail", otherIssue);
        ResourceNotFoundProblem otherBis = new ResourceNotFoundProblem();
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
