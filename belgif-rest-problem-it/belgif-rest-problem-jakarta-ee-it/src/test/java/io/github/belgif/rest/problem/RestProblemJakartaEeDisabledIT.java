package io.github.belgif.rest.problem;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.github.belgif.rest.problem.config.ProblemConfig;
import io.github.belgif.rest.problem.it.AbstractRestProblemDisabledIT;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@Testcontainers
class RestProblemJakartaEeDisabledIT extends AbstractRestProblemDisabledIT {

    @Container
    @SuppressWarnings("rawtypes")
    public static final GenericContainer JBOSS_CONTAINER = RestProblemJakartaEeIT.createContainer()
            .withEnv(ProblemConfig.PROPERTY_SERVER_SIDE_ENABLED, "false");

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.with().baseUri("http://" + JBOSS_CONTAINER.getHost())
                .port(JBOSS_CONTAINER.getMappedPort(8080))
                .basePath("/rest-problem/frontend");
    }

    @Test
    void enabled() {
        getSpec().basePath("/rest-problem/enabled").when()
                .get("/runtime").then()
                .assertThat()
                .statusCode(500)
                .body("type", equalTo("urn:problem-type:belgif:internalServerError"));
    }

}
