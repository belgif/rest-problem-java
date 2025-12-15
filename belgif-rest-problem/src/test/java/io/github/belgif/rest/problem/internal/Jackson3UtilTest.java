package io.github.belgif.rest.problem.internal;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.json.JsonMapper;

class Jackson3UtilTest {

    @Test
    void mismatchedInput() {
        assertThatExceptionOfType(MismatchedInputException.class).isThrownBy(() -> {
            new JsonMapper().readValue("{}", Model.class);
        }).satisfies(e -> {
            BadRequestProblem problem = Jackson3Util.toBadRequestProblem(e);
            InputValidationIssue issue = problem.getIssues().get(0);
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
            assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
            assertThat(issue.getName()).isEqualTo("id");
            assertThat(issue.getValue()).isNull();
            assertThat(issue.getDetail()).isEqualTo("must not be null");
        });
    }

    @Test
    void mismatchedInputNested() {
        assertThatExceptionOfType(MismatchedInputException.class).isThrownBy(() -> {
            new ObjectMapper().readValue("{\"model\": {}}", Nested.class);
        }).satisfies(e -> {
            BadRequestProblem problem = Jackson3Util.toBadRequestProblem(e);
            InputValidationIssue issue = problem.getIssues().get(0);
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
            assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
            assertThat(issue.getName()).isEqualTo("model.id");
            assertThat(issue.getValue()).isNull();
            assertThat(issue.getDetail()).isEqualTo("must not be null");
        });
    }

    @Test
    void mismatchedInputNestedWithArray() {
        assertThatExceptionOfType(DatabindException.class).isThrownBy(() -> {
            new ObjectMapper().readValue("{\"models\": [{}]}", NestedWithArray.class);
        }).satisfies(e -> {
            BadRequestProblem problem = Jackson3Util.toBadRequestProblem(e);
            InputValidationIssue issue = problem.getIssues().get(0);
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
            assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
            assertThat(issue.getName()).isEqualTo("models[0].id");
            assertThat(issue.getValue()).isNull();
            assertThat(issue.getDetail()).isEqualTo("must not be null");
        });
    }

    @Test
    void mismatchedInputFormatError() {
        assertThatExceptionOfType(MismatchedInputException.class).isThrownBy(() -> {
            new ObjectMapper().readValue("{\"id\":\"one two three\"}", Model.class);
        }).satisfies(e -> {
            BadRequestProblem problem = Jackson3Util.toBadRequestProblem(e);
            InputValidationIssue issue = problem.getIssues().get(0);
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
            assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
            assertThat(issue.getName()).isEqualTo("id");
            assertThat(issue.getValue()).isNull();
            assertThat(issue.getDetail()).isEqualTo(
                    "Cannot deserialize value of type `Integer` from String \"one two three\": not a valid `Integer` value");
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
