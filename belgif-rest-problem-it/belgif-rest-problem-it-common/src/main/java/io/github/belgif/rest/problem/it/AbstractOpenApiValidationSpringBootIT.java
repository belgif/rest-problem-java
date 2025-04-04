package io.github.belgif.rest.problem.it;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public abstract class AbstractOpenApiValidationSpringBootIT {

    private static final String BAD_REQUEST_URN = "urn:problem-type:belgif:badRequest";
    private static final String SCHEMA_VIOLATION_URN = "urn:problem-type:belgif:input-validation:schemaViolation";

    protected abstract RequestSpecification getSpec();

    @Test
    public void validPathParamWorksTest() {
        getSpec().when().get("/myFirstPath/abc1234567").then().assertThat()
                .statusCode(200)
                .body(equalTo("All good!"));
    }

    @Test
    public void notFound() {
        getSpec().when().get("/not/found").then().assertThat()
                .statusCode(404)
                .body("type", equalTo("urn:problem-type:belgif:resourceNotFound"));
    }

    @Test
    public void unknownQueryParamTest() {
        getSpec().when().queryParam("myUnknownParam", 123).get("/myFirstPath").then().assertThat()
                .statusCode(400)
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
    public void missingQueryParamTest() {
        getSpec().when().get("/myQueryPath").then().assertThat()
                .statusCode(400)
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("myParam"))
                .body("issues[0].value", nullValue())
                .body("issues[0].detail",
                        not(empty()));
    }

    @Test
    public void invalidPathParamTest() {
        getSpec().when().get("/myFirstPath/a1a1234567").then().assertThat()
                .statusCode(400)
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
    public void pathParamTooShortTest() {
        getSpec().when().get("/myFirstPath/abc").then().assertThat()
                .statusCode(400)
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
    public void validQueryParamWorksTest() {
        getSpec().when().queryParam("myParam", "abc1234567").get("/myFirstPath").then().assertThat()
                .statusCode(200)
                .body(equalTo("All good!"));
    }

    @Test
    public void invalidQueryParamTest() {
        getSpec().when().queryParam("myParam", "a1a1234567").get("/myFirstPath").then().assertThat()
                .statusCode(400)
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
    public void queryParamTooShortTest() {
        getSpec().when().queryParam("myParam", "abc").get("/myFirstPath").then().assertThat()
                .statusCode(400)
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
    public void absentHeaderParamTest() {
        getSpec().when().get("/myHeaderPath").then().assertThat()
                .statusCode(400)
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
    public void headerParamTooShortTest() {
        getSpec().when().header("MyHeader", "abc").get("/myHeaderPath").then().assertThat()
                .statusCode(400)
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
    public void requestBodyJsonParseErrorTest() {
        getSpec().contentType(ContentType.JSON).body("{" +
                "\"name\" ; this is my name" +
                "}").when().post("/myFirstPath").then().assertThat()
                .statusCode(400)
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].value", nullValue())
                .body("issues[0].detail",
                        not(empty()));
    }

    @Test
    public void missingRequestBodyTest() {
        getSpec().contentType(ContentType.JSON).when().post("/myFirstPath").then().assertThat()
                .statusCode(400)
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].value", nullValue())
                .body("issues[0].detail",
                        not(empty()));
    }

    @Test
    public void invalidRequestBodyTest() {
        getSpec().contentType(ContentType.JSON).body("{" +
                "\"myFirstProperty\": \"yes\"," +
                "\"myInnerObject\": {" +
                "\"myParam\": \"abc\"" +
                "}" +
                "}").when().post("/myFirstPath").then().assertThat()
                .statusCode(400)
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].name", equalTo("/myInnerObject/myParam"))
                .body("issues[0].value", equalTo("abc"))
                .body("issues[0].detail",
                        not(empty()));
    }

    @Test
    public void missingPropertiesInRequestBodyNestedSchemaTest() {
        getSpec().contentType(ContentType.JSON).body("{" +
                "\"myInnerObject\": {" +
                "\"nonExistingParam\": \"abc\"" +
                "}" +
                "}").when().post("/myFirstPath").then().assertThat()
                .statusCode(400)
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].name", equalTo("/"))
                .body("issues[0].detail",
                        containsString("myFirstProperty"))
                .body("issues[1].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[1].title", not(empty()))
                .body("issues[1].in", equalTo("body"))
                .body("issues[1].name", equalTo("/myInnerObject"))
                .body("issues[1].detail",
                        containsString("myParam"));
    }

    @Test
    public void missingPropertiesInRequestBodyTest() {
        getSpec().contentType(ContentType.JSON).body("{" +
                "}").when().post("/myFirstPath").then().assertThat()
                .statusCode(400)
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].name", equalTo("/"))
                .body("issues[0].detail",
                        containsString("myFirstProperty"))
                .body("issues[0].detail",
                        containsString("myInnerObject"));
    }

    @Test
    public void nonCompliantRegexInAllOfRequestBodyTest() {
        getSpec().contentType(ContentType.JSON).body("{\n" +
                "    \"myFirstProperty\": \"yes\",\n" +
                "    \"myInnerObject\": {\n" +
                "        \"myParam\": \"abc\",\n" +
                "        \"myOtherParam\": \"abc\"\n" +
                "    }\n" +
                "}").when().post("/myFirstPath/allOf").then().assertThat()
                .statusCode(400)
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[1].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[1].title", not(empty()))
                .body("issues[1].in", equalTo("body"))
                .body("issues[1].name", equalTo("/myInnerObject/myOtherParam"))
                .body("issues[1].value", equalTo("abc"))
                .body("issues[1].detail",
                        not(empty()))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].name", equalTo("/myInnerObject/myParam"))
                .body("issues[0].value", equalTo("abc"))
                .body("issues[0].detail",
                        not(empty()));
    }

    @Test
    public void missingPropertiesInAllOfRequestBodyTest() {
        getSpec().contentType(ContentType.JSON).body("{\n" +
                "    \"myFirstProperty\": \"yes\",\n" +
                "    \"myInnerObject\": {\n" +
                "    }\n" +
                "}").when().post("/myFirstPath/allOf").then().assertThat()
                .statusCode(400)
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].name", equalTo("/myInnerObject"))
                .body("issues[0].detail",
                        containsString("myParam"))
                .body("issues[1].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[1].title", not(empty()))
                .body("issues[1].in", equalTo("body"))
                .body("issues[1].name", equalTo("/myInnerObject"))
                .body("issues[1].detail",
                        containsString("myOtherParam"));
    }

    @Test
    public void missingPropertiesInOneOfRequestBodyTest() {
        getSpec().contentType(ContentType.JSON).body("{\n" +
                "    \"myFirstProperty\": \"yes\",\n" +
                "    \"myInnerObject\": {\n" +
                "        \"myNonExistingParam\": \"abc\"\n" +
                "    }\n" +
                "}").when().post("/myFirstPath/oneOf").then().assertThat()
                .statusCode(400)
                .body("type", equalTo(BAD_REQUEST_URN))
                .body("issues[0].type", equalTo(SCHEMA_VIOLATION_URN))
                .body("issues[0].title", not(empty()))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].name", equalTo("/myInnerObject"))
                .body("issues[0].detail",
                        containsString("exactly one schema"))
                .body("issues[0].detail",
                        containsString("myParam"))
                .body("issues[0].detail",
                        containsString("myOtherParam"));
    }

}
