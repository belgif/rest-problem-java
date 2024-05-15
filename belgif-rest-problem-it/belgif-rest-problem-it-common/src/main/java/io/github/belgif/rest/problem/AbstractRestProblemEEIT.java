package io.github.belgif.rest.problem;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

abstract class AbstractRestProblemEEIT extends AbstractRestProblemIT {

    @Test
    void beanValidation() {
        getSpec().when().queryParam("positive", -1)
                .get("/beanValidation").then().assertThat()
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

}
