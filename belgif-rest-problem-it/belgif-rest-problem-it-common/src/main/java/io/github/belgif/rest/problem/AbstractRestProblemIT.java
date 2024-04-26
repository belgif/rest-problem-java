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
    void badRequestFromBackend(String client) {
        getSpec().when().get("/badRequestFromBackend?client=" + client).then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("Bad Request from backend (caught successfully by frontend)"));
    }

    @ParameterizedTest
    @MethodSource("getClients")
    void customFromBackend(String client) {
        getSpec().when().get("/customFromBackend?client=" + client).then().assertThat()
                .statusCode(409)
                .body("type", equalTo("urn:problem-type:acme:custom"))
                .body("customField", equalTo("value from backend (caught successfully by frontend)"));
    }

    @ParameterizedTest
    @MethodSource("getClients")
    void unmappedFromBackend(String client) {
        getSpec().when().get("/unmappedFromBackend?client=" + client).then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:test:unmapped"))
                .body("detail", equalTo("Unmapped problem from backend (caught successfully by frontend)"));
    }

    @Test
    void beanValidation() {
        getSpec().when().get("/beanValidation?positive=-1").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].detail", equalTo("must be greater than 0"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("positive"))
                .body("issues[0].value", equalTo(-1))
                .body("issues[1].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[1].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[1].detail", equalTo("must not be null"))
                .body("issues[1].in", equalTo("query"))
                .body("issues[1].name", equalTo("required"))
                .body("issues[1].value", nullValue());
    }

    @Test
    void notFound() {
        getSpec().when().get("/not/found").then().assertThat()
                .statusCode(404);
    }

}
