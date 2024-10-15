package io.github.belgif.rest.problem;

import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestProblemOpenapiValidationSpringBoot3IT extends AbstractOpenApiValidationSpringBootIT {

    @LocalServerPort
    private int port;

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.with().baseUri("http://localhost").port(port).basePath("/spring");
    }

    // Only seems to work in SB3. In SB2 the atlassian lib throws the same exception, but the missing path seems not to
    // be handled by a controller and not catched by the ControllerAdvice.
    @Test
    void notFound() {
        getSpec().when().get("/not/found").then().assertThat()
                .statusCode(404).log().all()
                .body("type", equalTo("urn:problem-type:belgif:resourceNotFound"));
    }

}
