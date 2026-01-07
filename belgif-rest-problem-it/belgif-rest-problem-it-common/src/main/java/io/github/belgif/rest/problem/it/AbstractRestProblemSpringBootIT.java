package io.github.belgif.rest.problem.it;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

public abstract class AbstractRestProblemSpringBootIT extends AbstractRestProblemIT {

    @Test
    public void constraintViolationInvalidQueryParameterType() {
        getSpec().when()
                .queryParam("param", "invalid")
                .queryParam("other", "OK")
                .get("/beanValidation/queryParameter").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("param"))
                .body("issues[0].value", equalTo("invalid"))
                .body("issues[0].detail", equalTo("param should be of type Integer"));
    }

    @Test
    public void constraintViolationInvalidHeaderParameterType() {
        getSpec().when()
                .header("param", "invalid")
                .get("/beanValidation/headerParameter").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].in", equalTo("header"))
                .body("issues[0].name", equalTo("param"))
                .body("issues[0].value", equalTo("invalid"))
                .body("issues[0].detail", equalTo("param should be of type Integer"));
    }

    @Test
    public void constraintViolationInvalidPathParameterType() {
        getSpec().when().get("/beanValidation/pathParameter/class/test").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].in", equalTo("path"))
                .body("issues[0].name", equalTo("param"))
                .body("issues[0].value", equalTo("test"))
                .body("issues[0].detail", equalTo("param should be of type Integer"));
    }

    @Test
    public void constraintViolationQueryParameterNested() {
        getSpec().when()
                .queryParam("email", "myemail.com")
                .post("/beanValidation/queryParameter/nested").then().assertThat()
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

    // On Spring Boot, a proper resourceNotFound problem is returned
    @Override
    @Test
    public void notFound() {
        getSpec().when().get("/not/found").then().assertThat()
                .statusCode(404)
                .body("type", equalTo("urn:problem-type:belgif:resourceNotFound"))
                .body("detail", equalTo("No resource /frontend/not/found found"));
    }

}
