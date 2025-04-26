package io.github.belgif.rest.problem;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.github.belgif.rest.problem.config.ProblemConfig;
import io.github.belgif.rest.problem.it.AbstractRestProblemExtIT;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@Testcontainers
class RestProblemJavaEeExtIT extends AbstractRestProblemExtIT {

    @Container
    @SuppressWarnings("rawtypes")
    public static final GenericContainer JBOSS_CONTAINER = RestProblemJavaEeIT.createContainer()
            .withEnv(ProblemConfig.PROPERTY_EXT_ISSUE_TYPES, "true")
            .withEnv(ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY, "true");

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.with().baseUri("http://" + JBOSS_CONTAINER.getHost())
                .port(JBOSS_CONTAINER.getMappedPort(8080))
                .basePath("/rest-problem/frontend");
    }

}
