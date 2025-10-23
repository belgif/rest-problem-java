package io.github.belgif.rest.problem;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.test.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import io.github.belgif.rest.problem.it.AbstractOpenApiValidationSpringBootIT;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class RestProblemOpenApiValidationSpringBoot3IT extends AbstractOpenApiValidationSpringBootIT {

    @LocalServerPort
    private int port;

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.with().baseUri("http://localhost").port(port).basePath("/spring/openapi-validation");
    }
}
