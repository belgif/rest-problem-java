package io.github.belgif.rest.problem;

import java.util.Arrays;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@Testcontainers
class RestProblemJavaEeIT extends AbstractRestProblemIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestProblemJavaEeIT.class);

    @Container
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final GenericContainer JBOSS_CONTAINER =
            new GenericContainer("registry.redhat.io/jboss-eap-7/eap-xp4-openjdk17-openshift-rhel8:4.0-29")
                    .withCopyFileToContainer(
                            MountableFile.forHostPath("target/belgif-rest-problem-java-ee-it.war"),
                            "/deployments/belgif-rest-problem-java-ee-it.war")
                    .withExposedPorts(8080)
                    .waitingFor(Wait.forLogMessage(".*WFLYSRV0025.*", 1))
                    .withLogConsumer(new Slf4jLogConsumer(LOGGER));

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
