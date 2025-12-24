package io.github.belgif.rest.problem.quarkus.it;

import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.it.AbstractRestProblemIT;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@QuarkusTest
@TestHTTPEndpoint(Frontend.class)
class RestProblemQuarkusIT extends AbstractRestProblemIT {

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.given();
    }

    @Test
    @Override
    public void methodNotAllowed() {
        getSpec().when().post("/custom").then().assertThat()
                .statusCode(405);
    }

    @Test
    void constraintViolationBeanParameter() {
        getSpec().when()
                .queryParam("value", 10)
                .get("/beanValidation/beanParameter/x").then().assertThat()
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

    @Override
    protected Stream<String> getClients() {
        return Arrays.stream(Client.values()).map(Client::name);
    }
}
