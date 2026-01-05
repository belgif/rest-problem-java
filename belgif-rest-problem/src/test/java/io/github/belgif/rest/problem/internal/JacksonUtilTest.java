package io.github.belgif.rest.problem.internal;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

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
            assertThat(issue.getDetail()).isEqualTo("must not be null");
        });
    }

    @Test
    void mismatchedInputType() {
        assertThatExceptionOfType(MismatchedInputException.class).isThrownBy(() -> {
            new ObjectMapper().readValue("{ \"id\": \"123\", \"nbr\": \"twenty-two\" }", Model.class);
        }).satisfies(e -> {
            BadRequestProblem problem = JacksonUtil.toBadRequestProblem(e);
            InputValidationIssue issue = problem.getIssues().get(0);
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
            assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
            assertThat(issue.getName()).isEqualTo("nbr");
            assertThat(issue.getValue()).isEqualTo("twenty-two");
            assertThat(issue.getDetail()).isEqualTo("not a valid `int` value");
        });
    }

    @Test
    void valueInstantiationException() {
        assertThatExceptionOfType(ValueInstantiationException.class).isThrownBy(() -> {
            new ObjectMapper().readValue("{ \"id\": \"123\", \"size\": \"XXL\" }", Model.class);
        }).satisfies(e -> {
            BadRequestProblem problem = JacksonUtil.toBadRequestProblem(e);
            InputValidationIssue issue = problem.getIssues().get(0);
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
            assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
            assertThat(issue.getName()).isEqualTo("size");
            assertThat(issue.getValue()).isNull();
            assertThat(issue.getDetail()).isEqualTo("Unexpected value 'XXL'");
        });
    }

    @Test
    void invalidFormatException() {
        assertThatExceptionOfType(InvalidFormatException.class).isThrownBy(() -> {
            new ObjectMapper().readValue("{ \"id\": \"123\", \"size2\": \"XXL\" }", Model.class);
        }).satisfies(e -> {
            BadRequestProblem problem = JacksonUtil.toBadRequestProblem(e);
            InputValidationIssue issue = problem.getIssues().get(0);
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
            assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
            assertThat(issue.getName()).isEqualTo("size2");
            assertThat(issue.getValue()).isEqualTo("XXL");
            assertThat(issue.getDetail()).isEqualTo("not one of the values accepted for enumeration: [S, L, M]");
        });
    }

    @Test
    void jsonParseExceptionWrappedJsonMappingException() {
        assertThatExceptionOfType(JsonMappingException.class).isThrownBy(() -> {
            new ObjectMapper().readValue("{\"model\": { \"id: \"123\" }}", Nested.class);
        }).satisfies(e -> {
            BadRequestProblem problem = JacksonUtil.toBadRequestProblem(e);
            InputValidationIssue issue = problem.getIssues().get(0);
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
            assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
            assertThat(issue.getName()).isNull();
            assertThat(issue.getValue()).isNull();
            assertThat(issue.getDetail()).isEqualTo("JSON syntax error");
        });
    }

    @Test
    void jsonParseExceptionColon() {
        assertThatExceptionOfType(JsonParseException.class).isThrownBy(() -> {
            new ObjectMapper().readValue("{ \"id: \"123\" }", Model.class);
        }).satisfies(e -> {
            BadRequestProblem problem = JacksonUtil.toBadRequestProblem(e);
            InputValidationIssue issue = problem.getIssues().get(0);
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
            assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
            assertThat(issue.getName()).isNull();
            assertThat(issue.getValue()).isNull();
            assertThat(issue.getDetail()).isEqualTo("JSON syntax error");
        });
    }

    @Test
    void jsonParseExceptionEOF() {
        assertThatExceptionOfType(JsonParseException.class).isThrownBy(() -> {
            new ObjectMapper().readValue("{ \"id\": \"123\" ", Model.class);
        }).satisfies(e -> {
            BadRequestProblem problem = JacksonUtil.toBadRequestProblem(e);
            InputValidationIssue issue = problem.getIssues().get(0);
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
            assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
            assertThat(issue.getName()).isNull();
            assertThat(issue.getValue()).isNull();
            assertThat(issue.getDetail()).isEqualTo("JSON syntax error");
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
            assertThat(issue.getDetail()).isEqualTo("must not be null");
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
            assertThat(issue.getDetail()).isEqualTo("must not be null");
        });
    }

    @Test
    void mismatchedInputFormatError() {
        assertThatExceptionOfType(MismatchedInputException.class).isThrownBy(() -> {
            new ObjectMapper().readValue("{\"id\":\"one two three\"}", Model.class);
        }).satisfies(e -> {
            BadRequestProblem problem = JacksonUtil.toBadRequestProblem(e);
            InputValidationIssue issue = problem.getIssues().get(0);
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
            assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
            assertThat(issue.getName()).isEqualTo("id");
            assertThat(issue.getValue()).isEqualTo("one two three");
            assertThat(issue.getDetail()).isEqualTo(
                    "not a valid `int` value");
        });
    }

    enum Size {
        small("S"), medium("M"), large("L");

        private final String value;

        Size(String value) {
            this.value = value;
        }

        @JsonCreator
        public static Size fromValue(String value) {
            for (Size b : Size.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    enum SimpleSize {
        S, M, L
    }

    static class Model {
        @JsonCreator
        Model(@JsonProperty(required = true, value = "id") Integer id,
                @JsonProperty(required = false, value = "nbr") Integer number,
                @JsonProperty(required = false, value = "size") Size size,
                @JsonProperty(required = false, value = "size2") SimpleSize size2) {
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
