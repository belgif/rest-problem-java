package io.github.belgif.rest.problem.internal;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;

class JacksonUtilTest {

    @Test
    void mismatchedInput() {
        assertThatExceptionOfType(MismatchedInputException.class).isThrownBy(() -> {
            new ObjectMapper().readValue("{}", Model.class);
        }).satisfies(e -> {
            BadRequestProblem problem = JacksonUtil.toBadRequestProblem(e);
            InputValidationIssue issue = problem.getIssues().get(0);
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
            assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
            assertThat(issue.getName()).isEqualTo("id");
            assertThat(issue.getValue()).isNull();
            assertThat(issue.getDetail()).isEqualTo("Missing required creator property 'id' (index 0)");
        });
    }

    @Test
    void mismatchedInputNested() {
        assertThatExceptionOfType(MismatchedInputException.class).isThrownBy(() -> {
            new ObjectMapper().readValue("{\"model\": {}}", Nested.class);
        }).satisfies(e -> {
            BadRequestProblem problem = JacksonUtil.toBadRequestProblem(e);
            InputValidationIssue issue = problem.getIssues().get(0);
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
            assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
            assertThat(issue.getName()).isEqualTo("model.id");
            assertThat(issue.getValue()).isNull();
            assertThat(issue.getDetail()).isEqualTo("Missing required creator property 'id' (index 0)");
        });
    }

    @Test
    void mismatchedInputNestedWithArray() {
        assertThatExceptionOfType(MismatchedInputException.class).isThrownBy(() -> {
            new ObjectMapper().readValue("{\"models\": [{}]}", NestedWithArray.class);
        }).satisfies(e -> {
            BadRequestProblem problem = JacksonUtil.toBadRequestProblem(e);
            InputValidationIssue issue = problem.getIssues().get(0);
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
            assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
            assertThat(issue.getName()).isEqualTo("models[0].id");
            assertThat(issue.getValue()).isNull();
            assertThat(issue.getDetail()).isEqualTo("Missing required creator property 'id' (index 0)");
        });
    }

    static class Model {
        @JsonCreator
        Model(@JsonProperty(required = true, value = "id") Integer id) {
        }
    }

    static class Nested {
        @JsonProperty
        private Model model;
    }

    static class NestedWithArray {
        @JsonProperty
        private List<Model> models;
    }

}
