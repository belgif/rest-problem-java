package io.github.belgif.rest.problem;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.github.belgif.rest.problem.config.ProblemConfig;
import io.github.belgif.rest.problem.it.AbstractRestProblemExtIT;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@Testcontainers
class RestProblemJakartaEeExtIT extends AbstractRestProblemExtIT {

    @Container
    @SuppressWarnings("rawtypes")
    public static final GenericContainer JBOSS_CONTAINER = RestProblemJakartaEeIT.createContainer()
            .withEnv(ProblemConfig.PROPERTY_EXT_ISSUE_TYPES_ENABLED, "true")
            .withEnv(ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY_ENABLED, "true");

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.with().baseUri("http://" + JBOSS_CONTAINER.getHost())
                .port(JBOSS_CONTAINER.getMappedPort(8080))
                .basePath("/rest-problem/frontend");
    }

}
