package io.github.belgif.rest.problem.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import org.junit.jupiter.api.*;

import com.acme.custom.CustomProblem;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.TooManyRequestsProblem;
import io.github.belgif.rest.problem.api.*;
import io.github.belgif.rest.problem.config.ProblemConfig;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractJacksonSerializationTest {

    protected interface JacksonWrapper {
        String getVersion();

        void createMapper(Object module);

        Problem readProblem(String json) throws IOException;

        String writeValueAsString(Problem problem) throws IOException;
    }

    protected final JacksonWrapper jacksonWrapper = createJacksonWrapper();

    @BeforeAll
    void printJacksonVersion() {
        print("jackson version: " + jacksonWrapper.getVersion());
    }

    @BeforeEach
    protected abstract void setUp();

    @AfterEach
    void resetProblemConfig() {
        ProblemConfig.reset();
    }

    @Test
    void badRequestProblem() throws IOException {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("my detail message");
        problem.setAdditionalProperty("additional", "property");
        assertSerializationRoundtrip(problem);
    }

    @Test
    void customProblem() throws IOException {
        CustomProblem problem = new CustomProblem("custom");
        problem.setAdditionalProperty("additional", "property");
        assertSerializationRoundtrip(problem);
    }

    @Test
    void retryAfterProblem() throws IOException {
        TooManyRequestsProblem problem = new TooManyRequestsProblem();
        problem.setRetryAfterSec(60L);
        problem.setAdditionalProperty("additional", "property");
        assertSerializationRoundtrip(problem);
    }

    @Test
    void badRequestProblemReplacedSsin() throws IOException {
        BadRequestProblem problem = new BadRequestProblem(
                InputValidationIssues.replacedSsin(InEnum.BODY, "parent[1].ssin", "12345678901", "23456789012"));
        assertSerializationRoundtrip(problem);
    }

    @Test
    void badRequestProblemMultipleInputs() throws IOException {
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
    void badRequestProblemWithInNameValueAndInputsArray() throws IOException {
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
        Problem result = jacksonWrapper.readProblem(json);
        assertThat(result).isInstanceOf(BadRequestProblem.class);
        InputValidationIssue issue = ((BadRequestProblem) result).getIssues().get(0);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getInputs().get(0).getName()).isEqualTo("test");
        assertThat(jacksonWrapper.writeValueAsString(result)).isEqualToIgnoringWhitespace(json);
    }

    @Test
    void badRequestProblemWithInputsArrayAndInNameValue() throws IOException {
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
        Problem result = jacksonWrapper.readProblem(json);
        assertThat(result).isInstanceOf(BadRequestProblem.class);
        InputValidationIssue issue = ((BadRequestProblem) result).getIssues().get(0);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getInputs().get(0).getName()).isEqualTo("test");
    }

    @Test
    void unmappedProblem() throws IOException {
        jacksonWrapper.createMapper(null);

        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("my detail message");
        problem.setAdditionalProperty("additional", "property");
        String json = jacksonWrapper.writeValueAsString(problem);
        print(json);
        Problem result = jacksonWrapper.readProblem(json);
        assertThat(result).isInstanceOf(DefaultProblem.class);
        assertThat(jacksonWrapper.writeValueAsString(result)).isEqualTo(json);
    }

    @Test
    void legacyInvalidParamProblem() throws IOException {
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
        Problem problem = jacksonWrapper.readProblem(json);
        assertThat(problem).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertThat(badRequestProblem.getInvalidParams()).isNotEmpty();
        assertThat(jacksonWrapper.writeValueAsString(badRequestProblem)).isEqualToIgnoringWhitespace(json);
    }

    @Test
    void unknownProblemWithMessage() throws IOException {
        String json = "{\n"
                + "  \"id\" : \"08eb8aa6-d4a5-44fc-b25d-007b9f6a272a\",\n"
                + "  \"code\" : \"Bad Request\",\n"
                + "  \"message\" : \"552-Id Value is invalid\",\n"
                + "  \"details\" : [ ]\n"
                + "}";
        Problem problem = jacksonWrapper.readProblem(json);
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
    void additionalExceptionProperties() throws IOException {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setAdditionalProperty("cause", "cause");
        problem.setAdditionalProperty("stackTrace", "stackTrace");
        problem.setAdditionalProperty("suppressed", "suppressed");
        problem.setAdditionalProperty("message", "message");
        problem.setAdditionalProperty("localizedMessage", "localizedMessage");
        assertSerializationRoundtrip(problem);
    }

    @Test
    void issueWithStatusAndInstance() throws IOException {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("my detail message");
        InputValidationIssue issue = new InputValidationIssue().detail("test");
        issue.setAdditionalProperty("status", 400);
        issue.setAdditionalProperty("instance", "instance");
        problem.addIssue(issue);
        assertSerializationRoundtrip(problem);
    }

    @Test
    void issueWithNullValue() throws IOException {
        BadRequestProblem problem = new BadRequestProblem(
                new InputValidationIssue(InEnum.BODY, "id", null));
        String json = jacksonWrapper.writeValueAsString(problem);
        assertThat(json).doesNotContain("null");
        assertSerializationRoundtrip(problem);
    }

    @Test
    void issueWithNullInputValue() throws IOException {
        ProblemConfig.setExtInputsArrayEnabled(true);
        BadRequestProblem problem = new BadRequestProblem(new InputValidationIssue()
                .inputs(Input.body("a", null), Input.body("b", null)));
        String json = jacksonWrapper.writeValueAsString(problem);
        assertThat(json).doesNotContain("null");
        assertSerializationRoundtrip(problem);
    }

    protected void assertSerializationRoundtrip(Problem problem) throws IOException {
        String json = jacksonWrapper.writeValueAsString(problem);
        print(json);
        Problem result = jacksonWrapper.readProblem(json);
        assertThat(result).withRepresentation(p -> {
            try {
                return jacksonWrapper.writeValueAsString((Problem) p);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).isEqualTo(problem);
    }

    private static void print(String value) {
        System.out.println(value); // SUPPRESS CHECKSTYLE
    }

    protected abstract JacksonWrapper createJacksonWrapper();

}
