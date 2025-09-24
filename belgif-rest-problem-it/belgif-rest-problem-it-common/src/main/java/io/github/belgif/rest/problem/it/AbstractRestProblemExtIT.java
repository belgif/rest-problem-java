package io.github.belgif.rest.problem.it;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.config.ProblemConfig;
import io.restassured.specification.RequestSpecification;

public abstract class AbstractRestProblemExtIT {

    protected abstract RequestSpecification getSpec();

    @AfterAll
    static void afterAll() {
        ProblemConfig.reset();
    }

    @Test
    public void ping() {
        getSpec().when().get("/ping").then().assertThat()
                .statusCode(200)
                .body(is("pong"));
    }

    @Test
    void requestValidator() {
        getSpec().when()
                .get("/requestValidator?ssin=999999999999&a=test").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("The input message is incorrect"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif-ext:input-validation:invalidStructure"))
                .body("issues[0].href",
                        equalTo("https://www.belgif.be/specification/rest/api-guide/issues/ext/invalidStructure.html"))
                .body("issues[0].title", equalTo("Input value has invalid structure"))
                .body("issues[0].detail", equalTo("SSIN 999999999999 is invalid"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("ssin"))
                .body("issues[0].value", equalTo("999999999999"))
                .body("issues[1].type", equalTo("urn:problem-type:belgif-ext:input-validation:zeroOrAllOfExpected"))
                .body("issues[1].href", equalTo(
                        "https://www.belgif.be/specification/rest/api-guide/issues/ext/zeroOrAllOfExpected.html"))
                .body("issues[1].title", equalTo("All or none of these inputs must be present"))
                .body("issues[1].detail", equalTo("All or none of these inputs must be present: a, b"))
                .body("issues[1].inputs[0].in", equalTo("query"))
                .body("issues[1].inputs[0].name", equalTo("a"))
                .body("issues[1].inputs[0].value", equalTo("test"))
                .body("issues[1].inputs[1].in", equalTo("query"))
                .body("issues[1].inputs[1].name", equalTo("b"));
    }

}
