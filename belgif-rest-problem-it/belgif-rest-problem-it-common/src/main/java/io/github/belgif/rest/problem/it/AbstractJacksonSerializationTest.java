package io.github.belgif.rest.problem.it;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.acme.custom.CustomProblem;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.ProblemModule;
import io.github.belgif.rest.problem.TooManyRequestsProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.config.ProblemConfig;
import tools.jackson.core.json.PackageVersion;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

public abstract class AbstractJacksonSerializationTest {

    private ObjectMapper mapper;

    @BeforeAll
    static void printJacksonVersion() {
        print("jackson version: " + PackageVersion.VERSION);
    }

    @BeforeEach
    public void setUp() {
        TestProblemTypeRegistry registry = new TestProblemTypeRegistry();
        registry.registerProblemType(BadRequestProblem.class, CustomProblem.class, TooManyRequestsProblem.class);
        mapper = new JsonMapper().builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .addModule(new ProblemModule(registry))
                .build();
    }

    @AfterEach
    public void resetProblemConfig() {
        ProblemConfig.reset();
    }

    @Test
    public void badRequestProblem() {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("my detail message");
        problem.setAdditionalProperty("additional", "property");
        assertSerializationRoundtrip(problem);
    }

    @Test
    public void customProblem() {
        CustomProblem problem = new CustomProblem("custom");
        problem.setAdditionalProperty("additional", "property");
        assertSerializationRoundtrip(problem);
    }

    @Test
    public void retryAfterProblem() {
        TooManyRequestsProblem problem = new TooManyRequestsProblem();
        problem.setRetryAfterSec(60L);
        problem.setAdditionalProperty("additional", "property");
        assertSerializationRoundtrip(problem);
    }

    @Test
    public void badRequestProblemReplacedSsin() {
        BadRequestProblem problem = new BadRequestProblem(
                InputValidationIssues.replacedSsin(InEnum.BODY, "parent[1].ssin", "12345678901", "23456789012"));
        assertSerializationRoundtrip(problem);
    }

    @Test
    public void badRequestProblemMultipleInputs() {
        ProblemConfig.setExtInputsArrayEnabled(true);
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
    public void badRequestProblemWithInNameValueAndInputsArray() {
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
    public void badRequestProblemWithInputsArrayAndInNameValue() {
        String json = "{\n"
                + "  \"type\": \"urn:problem-type:belgif:badRequest\",\n"
                + "  \"href\": \"https://www.belgif.be/specification/rest/api-guide/problems/badRequest.html\",\n"
                + "  \"title\": \"Bad Request\",\n"
                + "  \"status\": 400,\n"
                + "  \"detail\": \"my detail message\",\n"
                + "  \"issues\": [\n"
                + "    {\n"
                + "      \"inputs\": [\n"
                + "        {\n"
                + "          \"in\": \"query\",\n"
                + "          \"name\": \"test\",\n"
                + "          \"value\": \"test\"\n"
                + "        }\n"
                + "      ],\n"
                + "      \"in\": \"query\",\n"
                + "      \"name\": \"test\",\n"
                + "      \"value\": \"test\"\n"
                + "    }\n"
                + "  ]\n"
                + "}";
        Problem result = mapper.readValue(json, Problem.class);
        assertThat(result).isInstanceOf(BadRequestProblem.class);
        InputValidationIssue issue = ((BadRequestProblem) result).getIssues().get(0);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getInputs().get(0).getName()).isEqualTo("test");
    }

    @Test
    public void unmappedProblem() {
        mapper = new JsonMapper().builder().enable(SerializationFeature.INDENT_OUTPUT).build();

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
    public void legacyInvalidParamProblem() {
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
    public void unknownProblemWithMessage() {
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
    public void additionalExceptionProperties() {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setAdditionalProperty("cause", "cause");
        problem.setAdditionalProperty("stackTrace", "stackTrace");
        problem.setAdditionalProperty("suppressed", "suppressed");
        problem.setAdditionalProperty("message", "message");
        problem.setAdditionalProperty("localizedMessage", "localizedMessage");
        assertSerializationRoundtrip(problem);
    }

    @Test
    public void issueWithStatusAndInstance() {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("my detail message");
        InputValidationIssue issue = new InputValidationIssue().detail("test");
        issue.setAdditionalProperty("status", 400);
        issue.setAdditionalProperty("instance", "instance");
        problem.addIssue(issue);
        assertSerializationRoundtrip(problem);
    }

    @Test
    public void issueWithNullValue() {
        BadRequestProblem problem = new BadRequestProblem(
                new InputValidationIssue(InEnum.BODY, "id", null));
        String json = mapper.writeValueAsString(problem);
        assertThat(json).doesNotContain("null");
        assertSerializationRoundtrip(problem);
    }

    @Test
    public void issueWithNullInputValue() {
        ProblemConfig.setExtInputsArrayEnabled(true);
        BadRequestProblem problem = new BadRequestProblem(new InputValidationIssue()
                .inputs(Input.body("a", null), Input.body("b", null)));
        String json = mapper.writeValueAsString(problem);
        assertThat(json).doesNotContain("null");
        assertSerializationRoundtrip(problem);
    }

    protected void assertSerializationRoundtrip(Problem problem) {
        String json = mapper.writeValueAsString(problem);
        print(json);
        Problem result = mapper.readValue(json, Problem.class);
        assertThat(result).withRepresentation(p -> mapper.writeValueAsString(p)).isEqualTo(problem);
    }

    private static void print(String value) {
        System.out.println(value); // SUPPRESS CHECKSTYLE
    }

}
