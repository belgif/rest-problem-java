package io.github.belgif.rest.problem;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestProblemOpenApiValidationSpringBoot2IT extends AbstractOpenApiValidationSpringBootIT {

    @LocalServerPort
    private int port;

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.with().baseUri("http://localhost").port(port).basePath("/spring/openapi-validation");
    }

    @Override
    @Disabled("Only seems to work in SB3. In SB2 the atlassian lib throws the same exception, " +
            "but the missing path seems not to be handled by a controller and not caught by the ControllerAdvice.")
    void notFound() {
        super.notFound();
    }
}
