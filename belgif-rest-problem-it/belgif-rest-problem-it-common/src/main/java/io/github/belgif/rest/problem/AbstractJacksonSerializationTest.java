package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.acme.custom.CustomProblem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.PackageVersion;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.registry.TestProblemTypeRegistry;

abstract class AbstractJacksonSerializationTest {

    private ObjectMapper mapper;

    @BeforeAll
    static void printJacksonVersion() {
        print("jackson version: " + PackageVersion.VERSION);
    }

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        TestProblemTypeRegistry registry = new TestProblemTypeRegistry();
        registry.registerProblemType(BadRequestProblem.class, CustomProblem.class, TooManyRequestsProblem.class);
        mapper.registerModule(new ProblemModule(registry));
    }

    @Test
    void badRequestProblem() throws JsonProcessingException {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("my detail message");
        problem.setAdditionalProperty("additional", "property");
        assertSerializationRoundtrip(problem);
    }

    @Test
    void customProblem() throws JsonProcessingException {
        CustomProblem problem = new CustomProblem("custom");
        problem.setAdditionalProperty("additional", "property");
        assertSerializationRoundtrip(problem);
    }

    @Test
    void retryAfterProblem() throws JsonProcessingException {
        TooManyRequestsProblem problem = new TooManyRequestsProblem();
        problem.setRetryAfterSec(60L);
        problem.setAdditionalProperty("additional", "property");
        assertSerializationRoundtrip(problem);
    }

    @Test
    void badRequestProblemReplacedSsin() throws JsonProcessingException {
        BadRequestProblem problem = new BadRequestProblem(
                InputValidationIssues.replacedSsin(InEnum.BODY, "parent[1].ssin", "12345678901", "23456789012"));
        assertSerializationRoundtrip(problem);
    }

    @Test
    void badRequestProblemMultipleInputs() throws JsonProcessingException {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("my detail message");
        InputValidationIssue issue = new InputValidationIssue();
        issue.setType(URI.create("urn:problem-type:cbss:input-validation:exactlyOneOfExpected"));
        issue.setTitle("Exactly one of these inputs is expected");
        issue.setTitle("Exactly one of inputs [one, two] is expected");
        issue.addInput(Input.query("one", 1));
        issue.addInput(Input.query("two", 2));
        problem.addIssue(issue);
        assertSerializationRoundtrip(problem);
    }

    @Test
    void badRequestProblemWithInNameValueAndInputsArray() throws JsonProcessingException {
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
        assertThat(result).isInstanceOf(BadRequestProblem.class);
        InputValidationIssue issue = ((BadRequestProblem) result).getIssues().get(0);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getInputs().get(0).getName()).isEqualTo("test");
        assertThat(mapper.writeValueAsString(result)).isEqualToIgnoringWhitespace(json);
    }

    @Test
    void unmappedProblem() throws JsonProcessingException {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("my detail message");
        problem.setAdditionalProperty("additional", "property");
        String json = mapper.writeValueAsString(problem);
        print(json);
        Problem result = mapper.readValue(json, Problem.class);
        assertThat(result).isInstanceOf(DefaultProblem.class);
        assertThat(mapper.writeValueAsString(result)).isEqualTo(json);
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
        assertThat(problem).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertThat(badRequestProblem.getInvalidParams()).isNotEmpty();
        assertThat(mapper.writeValueAsString(badRequestProblem)).isEqualToIgnoringWhitespace(json);
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
        assertThat(problem).isInstanceOf(DefaultProblem.class);
        DefaultProblem defaultProblem = (DefaultProblem) problem;
        assertThat(defaultProblem.getAdditionalProperties())
                .containsOnly(
                        entry("code", "Bad Request"),
                        entry("details", Collections.emptyList()),
                        entry("id", "08eb8aa6-d4a5-44fc-b25d-007b9f6a272a"),
                        entry("message", "552-Id Value is invalid"));
    }

    @Test
    void additionalExceptionProperties() throws JsonProcessingException {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setAdditionalProperty("cause", "cause");
        problem.setAdditionalProperty("stackTrace", "stackTrace");
        problem.setAdditionalProperty("suppressed", "suppressed");
        problem.setAdditionalProperty("message", "message");
        problem.setAdditionalProperty("localizedMessage", "localizedMessage");
        assertSerializationRoundtrip(problem);
    }

    @Test
    void issueWithStatusAndInstance() throws JsonProcessingException {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("my detail message");
        InputValidationIssue issue = new InputValidationIssue().detail("test");
        issue.setAdditionalProperty("status", 400);
        issue.setAdditionalProperty("instance", "instance");
        problem.addIssue(issue);
        assertSerializationRoundtrip(problem);
    }

    void assertSerializationRoundtrip(Problem problem) throws JsonProcessingException {
        String json = mapper.writeValueAsString(problem);
        print(json);
        Problem result = mapper.readValue(json, Problem.class);
        assertThat(result).withRepresentation(p -> {
            try {
                return mapper.writeValueAsString(p);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).isEqualTo(problem);
    }

    private static void print(String value) {
        System.out.println(value); // SUPPRESS CHECKSTYLE
    }

}
