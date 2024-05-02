package io.github.belgif.rest.problem;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestProblemSpringIT {

    @LocalServerPort
    private int port;

    private RequestSpecification spec;

    @BeforeEach
    void before() {
        spec = RestAssured.with().baseUri("http://localhost").port(port).basePath("/spring/frontend");
    }

    @Test
    void badRequest() {
        spec.when().get("/badRequest").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("Bad Request from frontend"));
    }

    @Test
    void custom() {
        spec.when().get("/custom").then().assertThat()
                .statusCode(409)
                .body("type", equalTo("urn:problem-type:acme:custom"))
                .body("customField", equalTo("value from frontend"));
    }

    @Test
    void runtime() {
        spec.when().get("/runtime").then().assertThat()
                .statusCode(500)
                .body("type", equalTo("urn:problem-type:belgif:internalServerError"));
    }

    @Test
    void unmapped() {
        spec.when().get("/unmapped").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:test:unmapped"))
                .body("detail", equalTo("Unmapped problem from frontend"));
    }

    @Test
    void retryAfter() {
        spec.when().get("/retryAfter").then().assertThat()
                .statusCode(503)
                .header("Retry-After", "10000")
                .body("type", equalTo("urn:problem-type:belgif:serviceUnavailable"));
    }

    @Test
    void pathParamInputViolation() {
        spec.when().get("/constraintViolationPath/1").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues.in", hasItem("path"))
                .body("issues.detail", hasItem("must be greater than or equal to 3"));
    }

    @Test
    void queryParamInputViolation() {
        spec.when().get("/constraintViolationQuery?id=100").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues.in", hasItem("query"))
                .body("issues.detail", hasItem("must be less than or equal to 10"));
    }

    @Test
    void headerParamInputViolation() {
        spec.when().header(new Header("id", "100")).get("/constraintViolationHeader").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues.in", hasItem("header"))
                .body("issues.detail", hasItem("must match \"^\\d\\d?$\""));
    }

    @Test
    void bodyInputViolation() {
        spec.when().body("{" +
                "\"email\": \"mymail.com\"" +
                "}")
                .contentType("application/json")
                .post("/methodArgumentNotValid").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues.in", hasItem("body"))
                .body("issues.detail", hasItem("must be a well-formed email address"))
                .body("issues.detail", hasItem("must not be blank"));
    }

    @ParameterizedTest
    @EnumSource
    void badRequestFromBackend(Client client) {
        spec.when().get("/badRequestFromBackend?client=" + client).then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("Bad Request from backend (caught successfully by frontend)"));
    }

    @ParameterizedTest
    @EnumSource
    void customFromBackend(Client client) {
        spec.when().get("/customFromBackend?client=" + client).then().assertThat()
                .statusCode(409)
                .body("type", equalTo("urn:problem-type:acme:custom"))
                .body("customField", equalTo("value from backend (caught successfully by frontend)"));
    }

    @ParameterizedTest
    @EnumSource
    void unmappedFromBackend(Client client) {
        spec.when().get("/unmappedFromBackend?client=" + client).then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:test:unmapped"))
                .body("detail", equalTo("Unmapped problem from backend (caught successfully by frontend)"));
    }

}
