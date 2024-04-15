package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.ProblemModule;
import io.github.belgif.rest.problem.registry.TestProblemTypeRegistry;

class ProblemTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        TestProblemTypeRegistry registry = new TestProblemTypeRegistry();
        registry.registerProblemType(BadRequestProblem.class);
        mapper.registerModule(new ProblemModule(registry));
    }

    @Test
    void jacksonRoundtrip() throws JsonProcessingException {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("my detail message");
        String json = mapper.writeValueAsString(problem);
        print(json);
        Problem result = mapper.readValue(json, Problem.class);
        assertEquals(BadRequestProblem.class, result.getClass());
        print(mapper.writeValueAsString(result));
    }

    @Test
    void jacksonRoundtripSsinReplaced() throws JsonProcessingException {
        BadRequestProblem problem = new BadRequestProblem(
                InputValidationIssues.replacedSsin(InEnum.BODY, "parent[1].ssin", "12345678901", "23456789012"));
        String json = mapper.writeValueAsString(problem);
        print(json);
        Problem result = mapper.readValue(json, Problem.class);
        assertEquals(BadRequestProblem.class, result.getClass());
        assertEquals("23456789012",
                ((BadRequestProblem) result).getIssues().get(0).getAdditionalProperties().get("replacedBy"));
        print(mapper.writeValueAsString(result));
    }

    @Test
    void jacksonRoundtripMultipleInputs() throws JsonProcessingException {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("my detail message");
        InputValidationIssue issue = new InputValidationIssue();
        issue.setType(URI.create("urn:problem-type:cbss:input-validation:exactlyOneOfExpected"));
        issue.setTitle("Exactly one of these inputs is expected");
        issue.setTitle("Exactly one of inputs [one, two] is expected");
        issue.addInput(new Input(InEnum.QUERY, "one", 1));
        issue.addInput(new Input(InEnum.QUERY, "two", 2));
        problem.addIssue(issue);
        String json = mapper.writeValueAsString(problem);
        print(json);
        Problem result = mapper.readValue(json, Problem.class);
        assertEquals(BadRequestProblem.class, result.getClass());
        assertThat(((BadRequestProblem) result).getIssues().get(0).getInputs()).hasSize(2);
        print(mapper.writeValueAsString(result));
    }

    @Test
    void jacksonUnmarshallInNameValueWithInputsArray() throws JsonProcessingException {
        String json = "{\n"
                + "  \"type\": \"urn:problem-type:belgif:badRequest\",\n"
                + "  \"href\": \"https://www.belgif.be/specification/rest/api-guide/problems/badRequest.html\",\n"
                + "  \"title\": \"Bad Request\",\n"
                + "  \"status\": 400,\n"
                + "  \"detail\": \"my detail message\",\n"
                + "  \"issues\": [\n"
                + "    {\n"
                + "      \"in\": \"query\",\n"
                + "      \"name\": \"test\",\n"
                + "      \"value\": \"test\",\n"
                + "      \"inputs\": [\n"
                + "        {\n"
                + "          \"in\": \"query\",\n"
                + "          \"name\": \"test\",\n"
                + "          \"value\": \"test\"\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  ]\n"
                + "}";
        Problem result = mapper.readValue(json, Problem.class);
        assertEquals(BadRequestProblem.class, result.getClass());
        InputValidationIssue issue = ((BadRequestProblem) result).getIssues().get(0);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getInputs().get(0).getName()).isEqualTo("test");
        print(mapper.writeValueAsString(result));
    }

    @Test
    void fallbackToDefaultProblemWhenProblemTypeNotMapped() throws JsonProcessingException {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("my detail message");
        String json = mapper.writeValueAsString(problem);
        print(json);
        Problem result = mapper.readValue(json, Problem.class);
        assertEquals(DefaultProblem.class, result.getClass());
        print(mapper.writeValueAsString(result));
    }

    @Test
    void legacyInvalidParamProblem() throws JsonProcessingException {
        String json = "{\n"
                + "   \"type\": \"urn:problem-type:belgif:badRequest\",\n"
                + "   \"href\": \"https://www.belgif.be/specification/rest/api-guide/problems/badRequest.html\",\n"
                + "   \"title\": \"Bad Request\",\n"
                + "   \"status\": 400,\n"
                + "   \"detail\": \"The input message is incorrect\",\n"
                + "   \"invalidParams\": [   {\n"
                + "      \"in\": \"body\",\n"
                + "      \"name\": \"sector\",\n"
                + "      \"reason\": \"must be less than or equal to 999\",\n"
                + "      \"value\": 9999,\n"
                + "      \"issueType\": \"schemaViolation\"\n"
                + "   }]\n"
                + "}";
        Problem problem = mapper.readValue(json, Problem.class);
        assertEquals(BadRequestProblem.class, problem.getClass());
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        InvalidParam invalidParam = badRequestProblem.getInvalidParams().get(0);
    }

    @Test
    void unknownProblemWithMessage() throws JsonProcessingException {
        String json = "{\n"
                + "  \"id\" : \"08eb8aa6-d4a5-44fc-b25d-007b9f6a272a\",\n"
                + "  \"code\" : \"Bad Request\",\n"
                + "  \"message\" : \"552-Id Value is invalid\",\n"
                + "  \"details\" : [ ]\n"
                + "}";
        Problem problem = mapper.readValue(json, Problem.class);
        assertEquals(DefaultProblem.class, problem.getClass());
        DefaultProblem defaultProblem = (DefaultProblem) problem;
        assertThat(defaultProblem.getAdditionalProperties()).containsEntry("message", "552-Id Value is invalid");
    }

    private static void print(String value) {
        System.out.println(value); // SUPPRESS CHECKSTYLE
    }

}
