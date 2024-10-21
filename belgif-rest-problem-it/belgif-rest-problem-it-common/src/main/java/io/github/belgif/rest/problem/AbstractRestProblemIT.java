package io.github.belgif.rest.problem;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.restassured.specification.RequestSpecification;

@TestInstance(PER_CLASS)
// PER_CLASS because otherwise @MethodSource("getClients") requires a static getClients() method
abstract class AbstractRestProblemIT {

    protected abstract RequestSpecification getSpec();

    protected abstract Stream<String> getClients();

    @Test
    void badRequest() {
        getSpec().when().get("/badRequest").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("Bad Request from frontend"));
    }

    @Test
    void custom() {
        getSpec().when().get("/custom").then().assertThat()
                .statusCode(409)
                .body("type", equalTo("urn:problem-type:acme:custom"))
                .body("customField", equalTo("value from frontend"));
    }

    @Test
    void runtime() {
        getSpec().when().get("/runtime").then().assertThat()
                .statusCode(500)
                .body("type", equalTo("urn:problem-type:belgif:internalServerError"));
    }

    @Test
    void unmapped() {
        getSpec().when().get("/unmapped").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:test:unmapped"))
                .body("detail", equalTo("Unmapped problem from frontend"));
    }

    @Test
    void retryAfter() {
        getSpec().when().get("/retryAfter").then().assertThat()
                .statusCode(503)
                .header("Retry-After", "10000")
                .body("type", equalTo("urn:problem-type:belgif:serviceUnavailable"));
    }

    @ParameterizedTest
    @MethodSource("getClients")
    void okFromBackend(String client) {
        getSpec().when().queryParam("client", client)
                .get("/okFromBackend").then().assertThat()
                .statusCode(200)
                .body(containsString("OK"));
    }

    @ParameterizedTest
    @MethodSource("getClients")
    void badRequestFromBackend(String client) {
        getSpec().when().queryParam("client", client)
                .get("/badRequestFromBackend").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("Bad Request from backend (caught successfully by frontend)"));
    }

    @ParameterizedTest
    @MethodSource("getClients")
    void customFromBackend(String client) {
        getSpec().when().queryParam("client", client)
                .get("/customFromBackend").then().assertThat()
                .statusCode(409)
                .body("type", equalTo("urn:problem-type:acme:custom"))
                .body("customField", equalTo("value from backend (caught successfully by frontend)"));
    }

    @ParameterizedTest
    @MethodSource("getClients")
    void unmappedFromBackend(String client) {
        getSpec().when().queryParam("client", client)
                .get("/unmappedFromBackend").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:test:unmapped"))
                .body("detail", equalTo("Unmapped problem from backend (caught successfully by frontend)"));
    }

    @Test
    void notFound() {
        getSpec().when().get("/not/found").then().assertThat()
                .statusCode(404);
    }

    @Test
    void methodNotAllowed() {
        getSpec().when().post("/custom").then().assertThat()
                .statusCode(405)
                .header("Allow", containsString("GET"));
    }

    @Test
    void notAcceptable() {
        getSpec()
                .when()
                .accept("application/xml")
                .get("/okFromBackend").then().assertThat()
                .statusCode(406);
    }

    @Test
    void unsupportedMediaType() {
        getSpec().when()
                .contentType("application/xml")
                .post("/beanValidation/body").then().assertThat()
                .statusCode(415);
    }

    @Test
    void constraintViolationMissingRequiredQueryParameter() {
        getSpec().when()
                .get("/beanValidation/queryParameter").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
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
    void constraintViolationInvalidQueryParameter() {
        getSpec().when()
                .queryParam("param", -1)
                .queryParam("other", "OK")
                .get("/beanValidation/queryParameter").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("param"))
                .body("issues[0].value", equalTo(-1))
                .body("issues[0].detail", equalTo("must be greater than 0"));
    }

    @Test
    void constraintViolationMultipleInvalidQueryParameters() {
        getSpec().when()
                .queryParam("param", -1)
                .queryParam("other", "TOO_LONG")
                .get("/beanValidation/queryParameter").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].detail", equalTo("size must be between 0 and 5"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("other"))
                .body("issues[0].value", equalTo("TOO_LONG"))
                .body("issues[1].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[1].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[1].detail", equalTo("must be greater than 0"))
                .body("issues[1].in", equalTo("query"))
                .body("issues[1].name", equalTo("param"))
                .body("issues[1].value", equalTo(-1));
    }

    @Test
    void constraintViolationMissingRequiredHeaderParameter() {
        getSpec().when()
                .get("/beanValidation/headerParameter").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
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
    void constraintViolationInvalidHeaderParameter() {
        getSpec().when()
                .header("param", -1)
                .get("/beanValidation/headerParameter").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].in", equalTo("header"))
                .body("issues[0].name", equalTo("param"))
                .body("issues[0].value", equalTo(-1))
                .body("issues[0].detail", equalTo("must be greater than 0"));
    }

    @Test
    void constraintViolationInvalidPathParameter() {
        getSpec().when()
                .get("/beanValidation/pathParameter/class/-1").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].in", equalTo("path"))
                .body("issues[0].name", equalTo("param"))
                .body("issues[0].value", equalTo(-1))
                .body("issues[0].detail", equalTo("must be greater than 0"));
    }

    @Test
    void constraintViolationInvalidPathParameterInherited() {
        getSpec().when().get("/beanValidation/pathParameter/inherited/-1").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("path"))
                .body("issues[0].name", equalTo("param"))
                .body("issues[0].value", equalTo(-1))
                .body("issues[0].detail", equalTo("must be greater than 0"));
    }

    @Test
    void constraintViolationInvalidPathParameterOverridden() {
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
    void constraintViolationMissingRequiredBody() {
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
    void constraintViolationBody() {
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
    void constraintViolationBodyNested() {
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
    void constraintViolationBodyInheritance() {
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

}
