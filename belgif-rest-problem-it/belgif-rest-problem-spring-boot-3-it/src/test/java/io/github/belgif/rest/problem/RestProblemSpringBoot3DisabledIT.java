package io.github.belgif.rest.problem;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.github.belgif.rest.problem.it.AbstractRestProblemDisabledIT;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("disabled")
@DirtiesContext
class RestProblemSpringBoot3DisabledIT extends AbstractRestProblemDisabledIT {

    @LocalServerPort
    private int port;

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.with().baseUri("http://localhost").port(port).basePath("/spring/frontend");
    }

    @Test
    void enabled() {
        getSpec().basePath("/spring/enabled").when().get("/runtime").then().assertThat()
                .statusCode(500)
                .body("type", equalTo("urn:problem-type:belgif:internalServerError"));
    }

}
