package io.github.belgif.rest.problem.it;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

public abstract class AbstractRestProblemEEIT extends AbstractRestProblemIT {

    @Test
    public void ejb() {
        getSpec().when()
                .get("/ejb").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("problem from EJB"));
    }

    @Test
    public void constraintViolationBeanParam() {
        getSpec().when()
                .queryParam("value", 10)
                .get("/beanValidation/beanParam/x").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].href",
                        equalTo("https://www.belgif.be/specification/rest/api-guide/issues/schemaViolation.html"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].detail", equalTo("size must be between 2 and 256"))
                .body("issues[0].in", equalTo("path"))
                .body("issues[0].name", equalTo("name"))
                .body("issues[0].value", equalTo("x"))
                .body("issues[1].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[1].href",
                        equalTo("https://www.belgif.be/specification/rest/api-guide/issues/schemaViolation.html"))
                .body("issues[1].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[1].detail", equalTo("must be less than or equal to 5"))
                .body("issues[1].in", equalTo("query"))
                .body("issues[1].name", equalTo("value"))
                .body("issues[1].value", equalTo(10));
    }

}
