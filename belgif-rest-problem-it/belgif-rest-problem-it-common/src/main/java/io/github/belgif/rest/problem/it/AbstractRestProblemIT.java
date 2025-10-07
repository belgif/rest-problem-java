package io.github.belgif.rest.problem.it;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.belgif.rest.problem.config.ProblemConfig;
import io.restassured.specification.RequestSpecification;

@TestInstance(PER_CLASS)
// PER_CLASS because otherwise @MethodSource("getClients") requires a static getClients() method
public abstract class AbstractRestProblemIT {

    protected abstract RequestSpecification getSpec();

    protected abstract Stream<String> getClients();

    @AfterAll
    static void afterAll() {
        ProblemConfig.reset();
    }

    @Test
    public void ping() {
        getSpec().when().get("/ping").then().assertThat()
                .statusCode(200)
                .body(is("pong"));
    }

    @Test
    public void badRequest() {
        getSpec().when().get("/badRequest").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("Bad Request from frontend"));
    }

    @Test
    public void custom() {
        getSpec().when().get("/custom").then().assertThat()
                .statusCode(409)
                .body("type", equalTo("urn:problem-type:acme:custom"))
                .body("customField", equalTo("value from frontend"));
    }

    @Test
    public void runtime() {
        getSpec().when().get("/runtime").then().assertThat()
                .statusCode(500)
                .body("type", equalTo("urn:problem-type:belgif:internalServerError"));
    }

    @Test
    public void unmapped() {
        getSpec().when().get("/unmapped").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:test:unmapped"))
                .body("detail", equalTo("Unmapped problem from frontend"));
    }

    @Test
    public void retryAfter() {
        getSpec().when().get("/retryAfter").then().assertThat()
                .statusCode(503)
                .header("Retry-After", "10000")
                .body("type", equalTo("urn:problem-type:belgif:serviceUnavailable"));
    }

    @ParameterizedTest
    @MethodSource("getClients")
    public void okFromBackend(String client) {
        getSpec().when().queryParam("client", client)
                .get("/okFromBackend").then().assertThat()
                .statusCode(200)
                .body(containsString("OK"));
    }

    @ParameterizedTest
    @MethodSource("getClients")
    public void badRequestFromBackend(String client) {
        getSpec().when().queryParam("client", client)
                .get("/badRequestFromBackend").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("Bad Request from backend (caught successfully by frontend)"));
    }

    @ParameterizedTest
    @MethodSource("getClients")
    public void customFromBackend(String client) {
        getSpec().when().queryParam("client", client)
                .get("/customFromBackend").then().assertThat()
                .statusCode(409)
                .body("type", equalTo("urn:problem-type:acme:custom"))
                .body("customField", equalTo("value from backend (caught successfully by frontend)"));
    }

    @ParameterizedTest
    @MethodSource("getClients")
    public void unmappedFromBackend(String client) {
        getSpec().when().queryParam("client", client)
                .get("/unmappedFromBackend").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:test:unmapped"))
                .body("detail", equalTo("Unmapped problem from backend (caught successfully by frontend)"));
    }

    @ParameterizedTest
    @MethodSource("getClients")
    public void applicationJsonProblemFromBackend(String client) {
        getSpec().when().queryParam("client", client)
                .get("/applicationJsonProblemFromBackend").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("Bad Request with application/json media type from backend"
                        + " (caught successfully by frontend)"));
    }

    @ParameterizedTest
    @MethodSource("getClients")
    public void jacksonMismatchedInputFromBackend(String client) {
        getSpec().when().queryParam("client", client)
                .get("/jacksonMismatchedInputFromBackend").then().assertThat()
                .statusCode(500)
                .body("type", equalTo("urn:problem-type:belgif:internalServerError"));
    }

    @Test
    public void notFound() {
        getSpec().when().get("/not/found").then().assertThat()
                .statusCode(404);
    }

    @Test
    public void methodNotAllowed() {
        getSpec().when().post("/custom").then().assertThat()
                .statusCode(405)
                .header("Allow", containsString("GET"));
    }

    @Test
    public void notAcceptable() {
        getSpec()
                .when()
                .accept("application/xml")
                .get("/okFromBackend").then().assertThat()
                .statusCode(406);
    }

    @Test
    public void unsupportedMediaType() {
        getSpec().when()
                .contentType("application/xml")
                .post("/beanValidation/body").then().assertThat()
                .statusCode(415);
    }

    @Test
    public void constraintViolationMissingRequiredQueryParameter() {
        getSpec().when()
                .get("/beanValidation/queryParameter").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("The input message is incorrect"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].href",
                        equalTo("https://www.belgif.be/specification/rest/api-guide/issues/schemaViolation.html"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("param"))
                .body("issues[0].value", nullValue())
                .body("issues[0].detail", anyOf(
                        // JEE
                        equalTo("must not be null"),
                        // Spring Boot
                        containsString("is not present")));
    }

    @Test
    public void constraintViolationInvalidQueryParameter() {
        getSpec().when()
                .queryParam("param", -1)
                .queryParam("other", "OK")
                .get("/beanValidation/queryParameter").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].href",
                        equalTo("https://www.belgif.be/specification/rest/api-guide/issues/schemaViolation.html"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("param"))
                .body("issues[0].value", equalTo(-1))
                .body("issues[0].detail", equalTo("must be greater than 0"));
    }

    @Test
    public void constraintViolationMultipleInvalidQueryParameters() {
        getSpec().when()
                .queryParam("param", -1)
                .queryParam("other", "TOO_LONG")
                .get("/beanValidation/queryParameter").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].href",
                        equalTo("https://www.belgif.be/specification/rest/api-guide/issues/schemaViolation.html"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].detail", equalTo("size must be between 0 and 5"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("other"))
                .body("issues[0].value", equalTo("TOO_LONG"))
                .body("issues[1].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[1].href",
                        equalTo("https://www.belgif.be/specification/rest/api-guide/issues/schemaViolation.html"))
                .body("issues[1].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[1].detail", equalTo("must be greater than 0"))
                .body("issues[1].in", equalTo("query"))
                .body("issues[1].name", equalTo("param"))
                .body("issues[1].value", equalTo(-1));
    }

    @Test
    public void constraintViolationMissingRequiredHeaderParameter() {
        getSpec().when()
                .get("/beanValidation/headerParameter").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].href",
                        equalTo("https://www.belgif.be/specification/rest/api-guide/issues/schemaViolation.html"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].in", equalTo("header"))
                .body("issues[0].name", equalTo("param"))
                .body("issues[0].value", nullValue())
                .body("issues[0].detail", anyOf(
                        // JEE
                        equalTo("must not be null"),
                        // Spring Boot
                        containsString("is not present")));
    }

    @Test
    public void constraintViolationInvalidHeaderParameter() {
        getSpec().when()
                .header("param", -1)
                .get("/beanValidation/headerParameter").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].href",
                        equalTo("https://www.belgif.be/specification/rest/api-guide/issues/schemaViolation.html"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].in", equalTo("header"))
                .body("issues[0].name", equalTo("param"))
                .body("issues[0].value", equalTo(-1))
                .body("issues[0].detail", equalTo("must be greater than 0"));
    }

    @Test
    public void constraintViolationInvalidPathParameter() {
        getSpec().when()
                .get("/beanValidation/pathParameter/class/-1").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].href",
                        equalTo("https://www.belgif.be/specification/rest/api-guide/issues/schemaViolation.html"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].in", equalTo("path"))
                .body("issues[0].name", equalTo("param"))
                .body("issues[0].value", equalTo(-1))
                .body("issues[0].detail", equalTo("must be greater than 0"));
    }

    @Test
    public void constraintViolationInvalidPathParameterInherited() {
        getSpec().when().get("/beanValidation/pathParameter/inherited/-1").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("path"))
                .body("issues[0].name", equalTo("param"))
                .body("issues[0].value", equalTo(-1))
                .body("issues[0].detail", equalTo("must be greater than 0"));
    }

    @Test
    public void constraintViolationInvalidPathParameterOverridden() {
        getSpec().when()
                .queryParam("param", -1)
                .get("/beanValidation/pathParameter/overridden").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("param"))
                .body("issues[0].value", equalTo(-1))
                .body("issues[0].detail", equalTo("must be greater than 0"));
    }

    @Test
    public void constraintViolationMissingRequiredBody() {
        getSpec().when()
                .contentType("application/json")
                .post("/beanValidation/body").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].name", nullValue())
                .body("issues[0].value", nullValue())
                .body("issues[0].detail", anyOf(
                        // JEE
                        equalTo("must not be null"),
                        // Spring Boot
                        containsString("Required request body is missing")));
    }

    @Test
    public void constraintViolationBody() {
        getSpec().when().body("{" +
                "\"email\": \"mymail.com\"" +
                "}")
                .contentType("application/json")
                .post("/beanValidation/body").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].name", equalTo("email"))
                .body("issues[0].value", equalTo("mymail.com"))
                .body("issues[0].detail", equalTo("must be a well-formed email address"))
                .body("issues[1].in", equalTo("body"))
                .body("issues[1].name", equalTo("name"))
                .body("issues[1].value", nullValue())
                .body("issues[1].detail", equalTo("must not be blank"));
    }

    @Test
    public void constraintViolationBodyNested() {
        getSpec().when().body("{" +
                "\"nested\": {" +
                "\"email\": \"mymail.com\"" +
                "}\n}")
                .contentType("application/json")
                .post("/beanValidation/body/nested").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].name", equalTo("nested.email"))
                .body("issues[0].value", equalTo("mymail.com"))
                .body("issues[0].detail", equalTo("must be a well-formed email address"))
                .body("issues[1].in", equalTo("body"))
                .body("issues[1].name", equalTo("nested.name"))
                .body("issues[1].value", nullValue())
                .body("issues[1].detail", equalTo("must not be blank"));
    }

    @Test
    public void constraintViolationBodyInheritance() {
        getSpec().when().body("{" +
                "\"email\": \"mymail.com\"," +
                "\"age\": 4" +
                "}")
                .contentType("application/json")
                .post("/beanValidation/body/inheritance").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].name", equalTo("email"))
                .body("issues[0].value", equalTo("mymail.com"))
                .body("issues[0].detail", equalTo("must be a well-formed email address"))
                .body("issues[1].in", equalTo("body"))
                .body("issues[1].name", equalTo("name"))
                .body("issues[1].value", nullValue())
                .body("issues[1].detail", equalTo("must not be blank"));
    }

    @Test
    public void jacksonMismatchedInputException() {
        getSpec().when().body("{\"description\": \"description\"}")
                .contentType("application/json")
                .post("/jackson/mismatchedInputException").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].name", equalTo("id"))
                .body("issues[0].detail", equalTo("must not be null"));
    }

    @Test
    void i18n() {
        getSpec().when()
                .header("Accept-Language", "nl-BE")
                .queryParam("param", -1)
                .queryParam("other", "TOO_LONG")
                .get("/beanValidation/queryParameter").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("Het input bericht is ongeldig"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].href",
                        equalTo("https://www.belgif.be/specification/rest/api-guide/issues/schemaViolation.html"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].detail", equalTo("grootte moet tussen 0 en 5 liggen"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("other"))
                .body("issues[0].value", equalTo("TOO_LONG"))
                .body("issues[1].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[1].href",
                        equalTo("https://www.belgif.be/specification/rest/api-guide/issues/schemaViolation.html"))
                .body("issues[1].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[1].detail", equalTo("moet groter dan 0 zijn"))
                .body("issues[1].in", equalTo("query"))
                .body("issues[1].name", equalTo("param"))
                .body("issues[1].value", equalTo(-1));
    }

    @Test
    void i18nUnsupportedLanguage() {
        getSpec().when()
                .header("Accept-Language", "es")
                .queryParam("param", -1)
                .queryParam("other", "TOO_LONG")
                .get("/beanValidation/queryParameter").then().assertThat()
                .statusCode(400)
                .body("detail", equalTo("The input message is incorrect"));
    }

    @Test
    void i18nWeighted() {
        getSpec().when()
                .header("Accept-Language", "fr-BE;q=0.5, nl-BE;q=0.7")
                .queryParam("param", -1)
                .queryParam("other", "TOO_LONG")
                .get("/beanValidation/queryParameter").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("Het input bericht is ongeldig"))
                .body("issues[0].detail", equalTo("grootte moet tussen 0 en 5 liggen"));
    }

    @Test
    void i18nUnsupportedLanguageWeighted() {
        getSpec().when()
                .header("Accept-Language", "fr-BE;q=0.5, es;q=0.7")
                .queryParam("param", -1)
                .queryParam("other", "TOO_LONG")
                .get("/beanValidation/queryParameter").then().assertThat()
                .statusCode(400)
                .body("detail", equalTo("The input message is incorrect"));
    }

    @Test
    void i18nCustom() {
        getSpec().when()
                .header("Accept-Language", "nl-BE")
                .get("/custom").then().assertThat()
                .statusCode(409)
                .body("type", equalTo("urn:problem-type:acme:custom"))
                .body("customField", equalTo("value from frontend"))
                .body("detail", equalTo("NL detail"));
    }

    @Test
    void i18nDisabled() {
        try {
            getSpec().queryParam("enabled", "false").post("/i18n");
            getSpec().when()
                    .header("Accept-Language", "nl-BE")
                    .queryParam("param", -1)
                    .queryParam("other", "TOO_LONG")
                    .get("/beanValidation/queryParameter").then().assertThat()
                    .statusCode(400)
                    .body("detail", equalTo("The input message is incorrect"));
        } finally {
            getSpec().queryParam("enabled", "true").post("/i18n");
        }
    }

    @Test
    void requestValidator() {
        getSpec().when()
                .get("/requestValidator?ssin=999999999999&a=test").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("The input message is incorrect"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:invalidInput"))
                .body("issues[0].href",
                        equalTo("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html"))
                .body("issues[0].title", equalTo("Invalid input"))
                .body("issues[0].detail", equalTo("SSIN 999999999999 is invalid"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("ssin"))
                .body("issues[0].value", equalTo("999999999999"))
                .body("issues[1].type", equalTo("urn:problem-type:belgif:input-validation:invalidInput"))
                .body("issues[1].href",
                        equalTo("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html"))
                .body("issues[1].title", equalTo("Invalid input"))
                .body("issues[1].detail", equalTo("All or none of these inputs must be present: a, b"));
    }

}
