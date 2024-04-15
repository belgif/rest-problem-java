package io.github.belgif.rest.problem;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
class RestProblemJavaEeIT {

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

    enum Client {
        MICROPROFILE, JAXRS, JAXRS_ASYNC, RESTEASY, RESTEASY_PROXY
    }

    private RequestSpecification spec;

    @BeforeEach
    void before() {
        spec = RestAssured.with().baseUri("http://" + JBOSS_CONTAINER.getHost())
                .port(JBOSS_CONTAINER.getMappedPort(8080))
                .basePath("/rest-problem/frontend");
    }

    @Test
    void badRequest() {
        spec.when().get("/badRequest").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("Bad Request from frontend"));
    }

    @Test
    void custom() {
        spec.when().get("/custom").then().assertThat()
                .statusCode(409)
                .body("type", equalTo("urn:problem-type:acme:custom"))
                .body("customField", equalTo("value from frontend"));
    }

    @Test
    void runtime() {
        spec.when().get("/runtime").then().assertThat()
                .statusCode(500)
                .body("type", equalTo("urn:problem-type:belgif:internalServerError"));
    }

    @Test
    void unmapped() {
        spec.when().get("/unmapped").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:test:unmapped"))
                .body("detail", equalTo("Unmapped problem from frontend"));
    }

    @Test
    void retryAfter() {
        spec.when().get("/retryAfter").then().assertThat()
                .statusCode(503)
                .header("Retry-After", "10000")
                .body("type", equalTo("urn:problem-type:belgif:serviceUnavailable"));
    }

    @ParameterizedTest
    @EnumSource
    void badRequestFromBackend(Client client) {
        spec.when().get("/badRequestFromBackend?client=" + client).then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("Bad Request from backend (caught successfully by frontend)"));
    }

    @ParameterizedTest
    @EnumSource
    void customFromBackend(Client client) {
        spec.when().get("/customFromBackend?client=" + client).then().assertThat()
                .statusCode(409)
                .body("type", equalTo("urn:problem-type:acme:custom"))
                .body("customField", equalTo("value from backend (caught successfully by frontend)"));
    }

    @ParameterizedTest
    @EnumSource
    void unmappedFromBackend(Client client) {
        spec.when().get("/unmappedFromBackend?client=" + client).then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:test:unmapped"))
                .body("detail", equalTo("Unmapped problem from backend (caught successfully by frontend)"));
    }

    @Test
    void beanValidation() {
        spec.when().get("/beanValidation?positive=-1").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].detail", equalTo("must be greater than 0"))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("positive"))
                .body("issues[0].value", equalTo(-1))
                .body("issues[1].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[1].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[1].detail", equalTo("must not be null"))
                .body("issues[1].in", equalTo("query"))
                .body("issues[1].name", equalTo("required"))
                .body("issues[1].value", nullValue());
    }

    @Test
    void notFound() {
        spec.when().get("/not/found").then().assertThat()
                .statusCode(404);
    }

}
