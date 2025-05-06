package io.github.belgif.rest.problem;

import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import io.github.belgif.rest.problem.it.AbstractRestProblemSpringBootIT;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class RestProblemSpringBoot3IT extends AbstractRestProblemSpringBootIT {

    @LocalServerPort
    private int port;

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.with().baseUri("http://localhost").port(port).basePath("/spring/frontend");
    }

    @Override
    protected Stream<String> getClients() {
        return Arrays.stream(Client.values()).map(Client::name);
    }

    // On Spring Boot 3.x only (for now), a proper resourceNotFound problem is returned
    @Override
    @Test
    public void notFound() {
        getSpec().when().get("/not/found").then().assertThat()
                .statusCode(404)
                .body("type", equalTo("urn:problem-type:belgif:resourceNotFound"))
                .body("detail", equalTo("No resource /frontend/not/found found"));
    }

}
