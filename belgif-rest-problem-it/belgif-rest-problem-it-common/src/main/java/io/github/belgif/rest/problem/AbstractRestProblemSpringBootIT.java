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
                .body("issues[0].detail", equalTo(
                        "Required request parameter 'required' for method parameter type String is not present"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("required"))
                .body("issues[0].value", nullValue());
    }

    @Test
    void pathParamInputViolation() {
        getSpec().when().get("/constraintViolationPath/1").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("path"))
                .body("issues[0].detail", equalTo("must be greater than or equal to 3"));
    }

    @Test
    void pathParamTypeMismatch() {
        getSpec().when().get("/constraintViolationPath/test").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("path"))
                .body("issues[0].detail", equalTo("id should be of type int"))
                .body("issues[0].value", equalTo("test"));
    }

    @Test
    void queryParamInputViolation() {
        getSpec().when().get("/constraintViolationQuery?id=100").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].detail", equalTo("must be less than or equal to 10"));
    }

    @Test
    void queryParamTypeMismatch() {
        getSpec().when().get("/constraintViolationQuery?id=test").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].detail", equalTo("id should be of type int"))
                .body("issues[0].value", equalTo("test"));
    }

    @Test
    void headerParamInputViolation() {
        getSpec().when().header(new Header("id", "100")).get("/constraintViolationHeader").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("header"))
                .body("issues[0].detail", equalTo("must be less than or equal to 10"));
    }

    @Test
    void headerParamTypeMismatch() {
        getSpec().when().header(new Header("id", "myId")).get("/constraintViolationHeader").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("header"))
                .body("issues[0].detail", equalTo("id should be of type int"))
                .body("issues[0].value", equalTo("myId"));
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
                .body("issues[0].detail", equalTo("must be a well-formed email address"))
                .body("issues[0].name", equalTo("email"))
                .body("issues[1].in", equalTo("body"))
                .body("issues[1].detail", equalTo("must not be blank"))
                .body("issues[1].name", equalTo("name"));

    }

    @Test
    void nestedQueryParamsViolation() {
        getSpec().when()
                .contentType("application/json")
                .post("/nestedQueryParams?email=myemail.com&name=myName").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].detail", equalTo("must be a well-formed email address"));
    }

    @Test
    void doubleNestedQueryParamsViolation() {
        getSpec().when()
                .contentType("application/json")
                .post("/nestedQueryParams?email=myemail.com").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].detail", equalTo("must be a well-formed email address"))
                .body("issues[0].name", equalTo("email"))
                .body("issues[1].in", equalTo("query"))
                .body("issues[1].detail", equalTo("must not be blank"))
                .body("issues[1].name", equalTo("name"));
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

}
