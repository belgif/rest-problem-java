package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.ProblemType;

class MissingPermissionProblemTest {

    @Test
    void construct() {
        MissingPermissionProblem problem = new MissingPermissionProblem();
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:missingPermission");
        assertThat(problem.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/problems/missingPermission.html");
        assertThat(problem.getTitle()).isEqualTo("Missing Permission");
        assertThat(problem.getStatus()).isEqualTo(403);
    }

    @Test
    void constructWithIssues() {
        InputValidationIssue issue = new InputValidationIssue(InEnum.QUERY, "test");
        MissingPermissionProblem problem = new MissingPermissionProblem("detail", issue);
        assertThat(problem.getDetail()).isEqualTo("detail");
        assertThat(problem.getIssues()).containsExactly(issue);
    }

    @Test
    void problemTypeAnnotation() {
        assertThat(MissingPermissionProblem.class).hasAnnotation(ProblemType.class);
        assertThat(MissingPermissionProblem.class.getAnnotation(ProblemType.class).value())
                .isEqualTo("urn:problem-type:belgif:missingPermission");
    }

}
