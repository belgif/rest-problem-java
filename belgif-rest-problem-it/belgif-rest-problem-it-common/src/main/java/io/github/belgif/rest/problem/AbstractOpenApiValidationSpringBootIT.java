package io.github.belgif.rest.problem;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import io.restassured.specification.RequestSpecification;

abstract class AbstractOpenApiValidationSpringBootIT {

    private static final String BAD_REQUEST_URN = "urn:problem-type:belgif:badRequest";
    private static final String SCHEMA_VIOLATION_URN = "urn:problem-type:belgif:input-validation:schemaViolation";

    protected abstract RequestSpecification getSpec();

    @Test
    void validPathParamWorksTest() {
        getSpec().when().get("/myFirstPath/abc1234567").then().assertThat()
                .statusCode(200).log().all()
                .body(equalTo("All good!"));
    }

    @Test
    void unknownQueryParamTest() {
        getSpec().when().get("/myFirstPath?myUnknownParam=123").then().assertThat()
                .statusCode(400).log().all()
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:unknownInput"))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("myUnknownParam"))
                .body("issues[0].value", equalTo("123"))
                .body("issues[0].detail",
                        not(empty()));
    }

    @Test
    void invalidPathParamTest() {
        getSpec().when().get("/myFirstPath/a1a1234567").then().assertThat()
                .statusCode(400).log().all()
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("path"))
                .body("issues[0].name", equalTo("pathParam"))
                .body("issues[0].value", equalTo("a1a1234567"))
                .body("issues[0].detail",
                        not(empty()));
    }

    @Test
    void pathParamTooShortTest() {
        getSpec().when().get("/myFirstPath/abc").then().assertThat()
                .statusCode(400).log().all()
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("path"))
                .body("issues[0].name", equalTo("pathParam"))
                .body("issues[0].value", equalTo("abc"))
                .body("issues[0].detail",
                        not(empty()));
    }

    @Test
    void validQueryParamWorksTest() {
        getSpec().when().get("/myFirstPath?myParam=abc1234567").then().assertThat()
                .statusCode(200).log().all()
                .body(equalTo("All good!"));
    }

    @Test
    void invalidQueryParamTest() {
        getSpec().when().get("/myFirstPath?myParam=a1a1234567").then().assertThat()
                .statusCode(400).log().all()
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("myParam"))
                .body("issues[0].value", equalTo("a1a1234567"))
                .body("issues[0].detail",
                        not(empty()));
    }

    @Test
    void queryParamTooShortTest() {
        getSpec().when().get("/myFirstPath?myParam=abc").then().assertThat()
                .statusCode(400).log().all()
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("myParam"))
                .body("issues[0].value", equalTo("abc"))
                .body("issues[0].detail",
                        not(empty()));
    }

    @Test
    void absentHeaderParamTest() {
        getSpec().when().get("/myHeaderPath").then().assertThat()
                .statusCode(400).log().all()
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("header"))
                .body("issues[0].name", equalTo("MyHeader"))
                .body("issues[0].value", nullValue())
                .body("issues[0].detail",
                        not(empty()));
    }

    @Test
    void headerParamTooShortTest() {
        getSpec().when().header("MyHeader", "abc").get("/myHeaderPath").then().assertThat()
                .statusCode(400).log().all()
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("header"))
                .body("issues[0].name", equalTo("MyHeader"))
                .body("issues[0].value", equalTo("abc"))
                .body("issues[0].detail",
                        not(empty()));
    }

    @Test
    void requestBodyJsonParseErrorTest() {
        getSpec().contentType("application/json").body("{" +
                "\"name\" ; this is my name" +
                "}").when().post("/myFirstPath").then().assertThat()
                .statusCode(400).log().all()
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].value", nullValue())
                .body("issues[0].detail",
                        not(empty()));
    }

    @Test
    void missingRequestBodyTest() {
        getSpec().contentType("application/json").when().post("/myFirstPath").then().assertThat()
                .statusCode(400).log().all()
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].value", nullValue())
                .body("issues[0].detail",
                        not(empty()));
    }

    @Test
    void invalidRequestBodyTest() {
        getSpec().contentType("application/json").body("{" +
                "\"myFirstProperty\": \"yes\"," +
                "\"myInnerObject\": {" +
                "\"myParam\": \"abc\"" +
                "}" +
                "}").when().post("/myFirstPath").then().assertThat()
                .statusCode(400).log().all()
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].name", equalTo("/myInnerObject/myParam"))
                .body("issues[0].value", equalTo("abc"))
                .body("issues[0].detail",
                        not(empty()));
    }

}
