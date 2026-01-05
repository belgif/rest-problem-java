package io.github.belgif.rest.problem;

import java.util.Arrays;
import java.util.stream.Stream;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.github.belgif.rest.problem.config.ProblemConfig;
import io.github.belgif.rest.problem.it.AbstractRestProblemEEIT;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@Testcontainers
class RestProblemJavaEeDisabledIT extends AbstractRestProblemEEIT {

    @Container
    @SuppressWarnings("rawtypes")
    public static final GenericContainer JBOSS_CONTAINER = RestProblemJavaEeIT.createContainer()
            .withEnv(ProblemConfig.PROPERTY_SERVER_SIDE_ENABLED, "false")
            .withEnv(ProblemConfig.PROPERTY_CLIENT_SIDE_ENABLED, "false");

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.with().baseUri("http://" + JBOSS_CONTAINER.getHost())
                .port(JBOSS_CONTAINER.getMappedPort(8080))
                .basePath("/rest-problem/frontend");
    }

    @Override
    protected Stream<String> getClients() {
        return Arrays.stream(Client.values()).map(Client::name);
    }

}
