package io.github.belgif.rest.problem;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import io.restassured.http.Header;

abstract class AbstractRestProblemSpringBootIT extends AbstractRestProblemIT {

    @Test
    void missingServletRequestParameterException() {
        getSpec().when().queryParam("positive", -1)
                .get("/beanValidation").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("required"))
                .body("issues[0].value", nullValue())
                .body("issues[0].detail", equalTo(
                        "Required request parameter 'required' for method parameter type String is not present"));
    }

    @Test
    void missingRequestHeaderParameterException() {
        getSpec().when().header(new Header("id", "1"))
                .get("/constraintViolationHeader").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].in", equalTo("header"))
                .body("issues[0].name", equalTo("required"))
                .body("issues[0].value", nullValue())
                .body("issues[0].detail", equalTo(
                        "Required request header 'required' for method parameter type String is not present"));
    }

    @Test
    void pathParamInputViolation() {
        getSpec().when().get("/constraintViolationPath/1").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("path"))
                .body("issues[0].name", equalTo("id"))
                .body("issues[0].value", equalTo(1))
                .body("issues[0].detail", equalTo("must be greater than or equal to 3"));
    }

    @Test
    void pathParamTypeMismatch() {
        getSpec().when().get("/constraintViolationPath/test").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("path"))
                .body("issues[0].name", equalTo("id"))
                .body("issues[0].value", equalTo("test"))
                .body("issues[0].detail", equalTo("id should be of type int"));
    }

    @Test
    void queryParamInputViolation() {
        getSpec().when()
                .queryParam("id", 100)
                .get("/constraintViolationQuery").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("id"))
                .body("issues[0].value", equalTo(100))
                .body("issues[0].detail", equalTo("must be less than or equal to 10"));
    }

    @Test
    void queryParamTypeMismatch() {
        getSpec().when()
                .queryParam("id", "test")
                .get("/constraintViolationQuery").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("id"))
                .body("issues[0].value", equalTo("test"))
                .body("issues[0].detail", equalTo("id should be of type int"));
    }

    @Test
    void headerParamInputViolation() {
        getSpec().when().header(new Header("id", "100"))
                .header(new Header("required", "OK"))
                .get("/constraintViolationHeader").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("header"))
                .body("issues[0].name", equalTo("id"))
                .body("issues[0].value", equalTo(100))
                .body("issues[0].detail", equalTo("must be less than or equal to 10"));
    }

    @Test
    void headerParamTypeMismatch() {
        getSpec().when().header(new Header("id", "myId"))
                .header(new Header("required", "OK"))
                .get("/constraintViolationHeader").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("header"))
                .body("issues[0].name", equalTo("id"))
                .body("issues[0].value", equalTo("myId"))
                .body("issues[0].detail", equalTo("id should be of type int"));
    }

    @Test
    void bodyInputViolation() {
        getSpec().when().body("{" +
                "\"email\": \"mymail.com\"" +
                "}")
                .contentType("application/json")
                .post("/methodArgumentNotValid").then().assertThat()
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
    void nestedQueryParamsViolation() {
        getSpec().when()
                .queryParam("email", "myemail.com")
                .queryParam("name", "myName")
                .post("/nestedQueryParams").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("email"))
                .body("issues[0].value", equalTo("myemail.com"))
                .body("issues[0].detail", equalTo("must be a well-formed email address"));
    }

    @Test
    void overriddenParamViolation() {
        getSpec().when()
                .queryParam("id", 0)
                .get("/overriddenPath").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("id"))
                .body("issues[0].value", equalTo(0))
                .body("issues[0].detail", equalTo("must be greater than or equal to 3"));
    }

    @Test
    void doubleNestedQueryParamsViolation() {
        getSpec().when()
                .queryParam("email", "myemail.com")
                .post("/nestedQueryParams").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("email"))
                .body("issues[0].value", equalTo("myemail.com"))
                .body("issues[0].detail", equalTo("must be a well-formed email address"))
                .body("issues[1].in", equalTo("query"))
                .body("issues[1].name", equalTo("name"))
                .body("issues[1].value", nullValue())
                .body("issues[1].detail", equalTo("must not be blank"));
    }

    @Test
    void unreadableBody() {
        getSpec().when().body("{" +
                "\"email: \"mymail.com\"" +
                "}")
                .contentType("application/json")
                .post("/methodArgumentNotValid").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].detail", containsString(
                        "JSON parse error: Unexpected character ('m' (code 109)): "
                                + "was expecting a colon to separate field name and value"));
    }

    @Test
    void superClassViolation() {
        getSpec().when().body("{" +
                "\"email\": \"mymail.com\"," +
                "\"age\": 4" +
                "}")
                .contentType("application/json")
                .post("/superClassValidation").then().assertThat()
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
    void nestedBodyViolation() {
        getSpec().when().body("{" +
                "\"myRequestBody\": {" +
                "\"email\": \"mymail.com\"," +
                "\"age\": 4" +
                "}\n}")
                .contentType("application/json")
                .post("/nestedValidation").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("body"))
                .body("issues[0].name", equalTo("myRequestBody.email"))
                .body("issues[0].value", equalTo("mymail.com"))
                .body("issues[0].detail", equalTo("must be a well-formed email address"))
                .body("issues[1].in", equalTo("body"))
                .body("issues[1].name", equalTo("myRequestBody.name"))
                .body("issues[1].value", nullValue())
                .body("issues[1].detail", equalTo("must not be blank"));
    }

}
